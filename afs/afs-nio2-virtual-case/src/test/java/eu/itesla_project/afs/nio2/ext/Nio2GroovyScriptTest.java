/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.afs.nio2.ext;

import com.google.common.collect.ImmutableList;
import eu.itesla_project.afs.Project;
import eu.itesla_project.afs.ProjectFolder;
import eu.itesla_project.afs.ProjectNode;
import eu.itesla_project.afs.ext.GroovyScript;
import eu.itesla_project.afs.ext.GroovyScriptBuilder;
import eu.itesla_project.afs.nio2.Nio2AbstractProjectFileTest;
import eu.itesla_project.afs.nio2.Nio2ProjectFileBuilderFactory;
import eu.itesla_project.afs.nio2.Nio2ProjectFileScanner;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public class Nio2GroovyScriptTest extends Nio2AbstractProjectFileTest {

    @Override
    protected List<Nio2ProjectFileScanner> getProjectFileScanners() {
        return ImmutableList.of(new Nio2GroovyScriptScanner());
    }

    @Override
    protected List<Nio2ProjectFileBuilderFactory> getProjectFileBuilderFactories() {
        return ImmutableList.of(new Nio2GroovyScriptBuilderFactory());
    }

    @Test
    public void test() throws Exception {
        Project project = afs.getRootFolder().createProject("project", "");
        ProjectFolder rootFolder = project.getRootFolder();

        // create groovy script
        GroovyScript script = rootFolder.fileBuilder(GroovyScriptBuilder.class)
                .withName("script")
                .withContent("println 'hello'")
                .build();
        assertNotNull(script);
        assertEquals("script", script.getName());
        assertNotNull(script.getIcon());
        assertFalse(script.isFolder());
        assertTrue(script.getDependencies().isEmpty());
        assertEquals("println 'hello'", script.read());
        script.write("println 'bye'");
        assertEquals("println 'bye'", script.read());

        // check groovy script file is correctly scanned
        assertEquals(1, rootFolder.getChildren().size());
        ProjectNode firstNode = rootFolder.getChildren().get(0);
        assertTrue(firstNode instanceof GroovyScript);
        assertEquals("script", firstNode.getName());
    }
}