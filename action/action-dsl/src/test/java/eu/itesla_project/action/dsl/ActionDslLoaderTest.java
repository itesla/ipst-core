/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.action.dsl;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import eu.itesla_project.contingency.Contingency;
import eu.itesla_project.contingency.ContingencyElement;
import eu.itesla_project.iidm.network.Network;
import eu.itesla_project.iidm.network.test.EurostagTutorialExample1Factory;
import groovy.lang.GroovyCodeSource;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Mathieu Bague <mathieu.bague at rte-france.com>
 */
public class ActionDslLoaderTest {

    @org.junit.Rule
    public final ExpectedException exception = ExpectedException.none();

    private Network network;

    @Before
    public void setUp() {
        network = EurostagTutorialExample1Factory.create();
    }

    @Test
    public void test() {
        ActionDb actionDb = new ActionDslLoader(new GroovyCodeSource(getClass().getResource("/actions.groovy"))).load(network);

        assertEquals(2, actionDb.getContingencies().size());
        Contingency contingency = actionDb.getContingency("contingency1");
        ContingencyElement element = contingency.getElements().iterator().next();
        assertEquals("NHV1_NHV2_1", element.getId());

        contingency = actionDb.getContingency("contingency2");
        element = contingency.getElements().iterator().next();
        assertEquals("GEN", element.getId());

        assertEquals(1, actionDb.getRules().size());
        Rule rule = actionDb.getRules().iterator().next();
        assertEquals("rule", rule.getId());
        assertEquals("rule description", rule.getDescription());
        assertTrue(rule.getActions().contains("action"));
        assertEquals(2, rule.getLife());

        Action action = actionDb.getAction("action");
        assertEquals("action", action.getId());
        assertEquals("action description", action.getDescription());
        assertEquals(0, action.getTasks().size());
    }

    @Test
    public void testDslExtension() {
        ActionDb actionDb = new ActionDslLoader(new GroovyCodeSource(getClass().getResource("/actions2.groovy"))).load(network);
        Action another = actionDb.getAction("anotherAction");
        exception.expect(RuntimeException.class);
        exception.expectMessage("Switch 'switchId' not found");
        another.run(network, null);
    }

    @Test
    public void testUnvalidate() {
        ActionDb actionDb = new ActionDslLoader(new GroovyCodeSource(getClass().getResource("/actions2.groovy"))).load(network);
        Action someAction = actionDb.getAction("someAction");
        exception.expect(ActionDslException.class);
        someAction.run(network, null);
    }

    @Test
    public void testConfigPars() {
        try (FileSystem fileSystem = Jimfs.newFileSystem(Configuration.unix())) {
            Files.createDirectory(fileSystem.getPath("/tmp"));
            Path configFile = Files.createFile(fileSystem.getPath("/tmp/config.groovy"));
            Path configFile2 = Files.createFile(fileSystem.getPath("/tmp/config_2.groovy"));
            Files.write(configFile, Collections.singletonList("transformer_r = 2.0"), Charset.forName("UTF-8"));
            Files.write(configFile2, Collections.singletonList("cat_life = 9"), Charset.forName("UTF-8"));

            ActionDb actionDb = new ActionDslLoader(new GroovyCodeSource(getClass().getResource("/actions2.groovy"))).load(network);
            Collection<Rule> rules = actionDb.getRules();
            boolean tested = false;
            for (Rule rule : rules) {
                if (Objects.equals(rule.getId(), "TestConfig")) {
                    assertEquals(9, rule.getLife());
                    tested = true;
                }
            }
            assertTrue(tested);

            Action action = actionDb.getAction("testConfig");
            action.run(network, null);
            assertEquals(2, network.getTwoWindingsTransformer("NGEN_NHV1").getR(), 0.0f);
        } catch (IOException e) {
            fail();
        }
    }
}
