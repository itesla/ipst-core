/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.afs.nio2.ext;

import com.google.common.collect.ImmutableList;
import eu.itesla_project.afs.Folder;
import eu.itesla_project.afs.Project;
import eu.itesla_project.afs.ProjectFolder;
import eu.itesla_project.afs.ProjectNode;
import eu.itesla_project.afs.ext.Case;
import eu.itesla_project.afs.ext.ImportedCase;
import eu.itesla_project.afs.ext.ImportedCaseBuilder;
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
public class Nio2ImportedCaseTest extends Nio2AbstractProjectFileTest {

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
        return ImmutableList.of(new Nio2ImportedCaseScanner());
    }

    @Override
    protected List<Nio2ProjectFileBuilderFactory> getProjectFileBuilderFactories() {
        return ImmutableList.of(new Nio2ImportedCaseBuilderFactory());
    }

    @Before
    public void setup() throws IOException {
        super.setup();
        Files.createFile(dataDir.resolve("network." + TestImporter.EXT));
    }

    @Test
    public void test() throws Exception {
        Folder root = afs.getRootFolder();

        // check case exist
        assertEquals(1, root.getChildren().size());
        assertTrue(root.getChildren().get(0) instanceof Case);
        Case _case = (Case) root.getChildren().get(0);
        assertEquals("network", _case.getName());

        // create project
        Project project = root.createProject("project", "");
        assertNotNull(project);

        // create project folder
        ProjectFolder folder = project.getRootFolder().createFolder("folder");
        assertTrue(folder.getChildren().isEmpty());

        // import case into project
        ImportedCase importedCase = folder.fileBuilder(ImportedCaseBuilder.class)
                .withCase(_case)
                .withParameter("param1", "true")
                .build();
        assertNotNull(importedCase);
        Network network = importedCase.loadNetwork();
        assertNotNull(network);

        // try to reload the imported case
        assertEquals(1, folder.getChildren().size());
        ProjectNode projectNode = folder.getChildren().get(0);
        assertNotNull(projectNode);
        assertTrue(projectNode instanceof ImportedCase);

        // delete imported case
        projectNode.delete();
        assertTrue(folder.getChildren().isEmpty());
        try {
            projectNode.getName();
        } catch (Exception ignored) {
        }
    }
}