/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.afs.nio2;

import com.google.common.collect.ImmutableList;
import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import eu.itesla_project.afs.AppFileSystem;
import eu.itesla_project.afs.Folder;
import eu.itesla_project.afs.Project;
import eu.itesla_project.afs.ProjectFolder;
import eu.itesla_project.computation.ComputationManager;
import eu.itesla_project.iidm.import_.ImportersLoader;
import eu.itesla_project.iidm.import_.ImportersLoaderList;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

import static org.junit.Assert.*;

/**
 *
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public class Nio2AppFileSystemTest {

    private FileSystem fs;

    private AppFileSystem afs;

    @Before
    public void setup() throws IOException {
        fs = Jimfs.newFileSystem(Configuration.unix());
        Path dataDir = fs.getPath("/data");
        Files.createDirectories(dataDir.resolve("dir1/dir2"));
        Files.createDirectories(dataDir.resolve("dir1/dir3"));
        ImportersLoader loader = new ImportersLoaderList(Collections.emptyList(), Collections.emptyList());
        ComputationManager computationManager = Mockito.mock(ComputationManager.class);
        afs = new Nio2AppFileSystem(fs, "mem", "/data", Collections.emptyList(), Collections.emptyList(), Collections.emptyList(),
                                    loader, computationManager);
    }

    @After
    public void tearDown() throws IOException {
        fs.close();
    }

    @Test
    public void test() throws IOException {
        assertEquals(afs.getName(), "mem");
        assertTrue(afs.getProjectFileTypes().isEmpty()); // no plugin
        Folder root = afs.getRootFolder();
        assertNotNull(root);
        Folder dir1 = (Folder) root.getChild("dir1");
        assertNotNull(dir1);
        assertTrue(dir1.isFolder());
        assertEquals(dir1.getName(), "dir1");
        assertEquals(dir1.getParent().getName(), "mem");
        Folder dir2 = (Folder) dir1.getChild("dir2");
        assertNotNull(dir2);
        assertNotNull(dir2.getParent());
        assertEquals(dir2.getParent().getPath(), "mem:/dir1");
        assertEquals(dir1.getChildren().size(), 2);
        Folder dir3 = (Folder) root.getChild("dir3");
        assertNull(dir3);
        String str = dir2.getPath();
        assertEquals("mem:/dir1/dir2", str);
        Folder mayBeDir2 = (Folder) afs.getRootFolder().getChild("dir1/dir2");
        assertNotNull(mayBeDir2);
        assertEquals("dir2", mayBeDir2.getName());

        Project project1 = dir2.createProject("project1", "test project");
        assertNotNull(project1);
        assertEquals(project1.getName(), "project1");
        assertEquals(project1.getDescription(), "test project");
        assertNotNull(project1.getParent());
        assertEquals(project1.getParent().getPath(), "mem:/dir1/dir2");
        assertTrue(project1.getRootFolder().getChildren().isEmpty());
        assertTrue(project1.getFileSystem() == afs);

        ProjectFolder dir4 = project1.getRootFolder().createFolder("dir4");
        assertTrue(dir4.isFolder());
        assertEquals(dir4.getName(), "dir4");
        assertNotNull(dir4.getParent());
        assertTrue(dir4.getChildren().isEmpty());
        assertEquals(1, project1.getRootFolder().getChildren().size());

        dir4.delete();
        assertTrue(project1.getRootFolder().getChildren().isEmpty());
        try {
            dir4.getChildren();
            fail();
        } catch (Exception ignored) {
        }

        ProjectFolder dir5 = project1.getRootFolder().createFolder("dir5");
        ProjectFolder dir6 = dir5.createFolder("dir6");
        assertEquals(ImmutableList.of("dir5", "dir6"), dir6.getPathList());
        assertEquals("dir5/dir6", dir6.getPath());
        assertEquals("dir6", project1.getRootFolder().getChild("dir5/dir6").getName());
    }

}