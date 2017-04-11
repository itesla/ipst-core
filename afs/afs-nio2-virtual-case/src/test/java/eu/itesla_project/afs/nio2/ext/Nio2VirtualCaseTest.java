/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.afs.nio2.ext;

import com.google.common.collect.ImmutableList;
import com.google.common.io.CharStreams;
import eu.itesla_project.afs.Project;
import eu.itesla_project.afs.ProjectFolder;
import eu.itesla_project.afs.ext.*;
import eu.itesla_project.afs.nio2.*;
import eu.itesla_project.iidm.import_.Importer;
import eu.itesla_project.iidm.network.Network;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public class Nio2VirtualCaseTest extends Nio2AbstractProjectFileTest {

    @Override
    protected List<Importer> getImporters() {
        return ImmutableList.of(new TestImporter(network));
    }

    @Override
    protected List<Nio2FileScanner> getFileScanners() {
        return ImmutableList.of(new Nio2CaseScanner());
    }

    @Override
    protected List<Nio2ProjectFileScanner> getProjectFileScanners() {
        return ImmutableList.of(new Nio2ImportedCaseScanner(), new Nio2GroovyScriptScanner(), new Nio2VirtualCaseScanner());
    }

    @Override
    protected List<Nio2ProjectFileBuilderFactory> getProjectFileBuilderFactories() {
        return ImmutableList.of(new Nio2ImportedCaseBuilderFactory(), new Nio2GroovyScriptBuilderFactory(), new Nio2VirtualCaseBuilderFactory());
    }

    @Before
    public void setup() throws IOException {
        super.setup();
        Files.createFile(dataDir.resolve("network." + TestImporter.EXT));
    }

    @Test
    public void test() throws Exception {
        // get case
        Case _case = (Case) afs.getRootFolder().getChildren().get(0);

        // create project
        Project project = afs.getRootFolder().createProject("project", "");

        // create project folder
        ProjectFolder folder = project.getRootFolder().createFolder("folder");

        // import case into project
        ImportedCase importedCase = folder.fileBuilder(ImportedCaseBuilder.class)
                .withCase(_case)
                .build();

        // create groovy script
        GroovyScript script = folder.fileBuilder(GroovyScriptBuilder.class)
                .withName("script")
                .withContent("print 'hello'")
                .build();

        // create virtual by applying groovy script on imported case
        VirtualCase virtualCase = folder.fileBuilder(VirtualCaseBuilder.class)
                .withName("network2")
                .withCase("folder/network")
                .withScript("folder/script")
                .build();

        assertEquals("network2", virtualCase.getName());
        assertNotNull(virtualCase.getCase());
        assertNotNull(virtualCase.getScript());
        assertEquals(2, virtualCase.getDependencies().size());
        assertEquals(1, importedCase.getBackwardDependencies().size());
        assertEquals(1, script.getBackwardDependencies().size());
        Network network = virtualCase.loadNetwork();
        assertNotNull(network);

        // check script output
        assertEquals("hello", CharStreams.toString(virtualCase.getOutReader()));

        // test cache invalidation
        script.write("print 'bye'");
        assertNull(virtualCase.getOutReader());
        Network network2 = virtualCase.loadNetwork();
        assertNotNull(network2);
        assertEquals("bye", CharStreams.toString(virtualCase.getOutReader()));

        virtualCase.delete();
        assertTrue(importedCase.getBackwardDependencies().isEmpty());
        assertTrue(script.getBackwardDependencies().isEmpty());

    }
}