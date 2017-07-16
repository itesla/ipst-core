/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.afs.core;

import com.google.common.collect.ImmutableList;
import eu.itesla_project.afs.storage.AppFileSystemStorage;
import eu.itesla_project.afs.storage.NodeId;
import eu.itesla_project.afs.mapdb.storage.MapDbAppFileSystemStorage;
import eu.itesla_project.computation.ComputationManager;
import eu.itesla_project.iidm.import_.ImportersLoader;
import eu.itesla_project.iidm.import_.ImportersLoaderList;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.Collections;

import static org.junit.Assert.*;

/**
 *
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public class AfsBaseTest {

    private AppFileSystemStorage storage;

    private AppFileSystem afs;

    private AppData ad;

    @Before
    public void setup() throws IOException {
        storage = MapDbAppFileSystemStorage.createHeap("mem");

        NodeId rootFolderId = storage.getRootNode();
        NodeId dir1FolderId = storage.createNode(rootFolderId, "dir1", Folder.PSEUDO_CLASS);
        storage.createNode(dir1FolderId, "dir2", Folder.PSEUDO_CLASS);
        storage.createNode(dir1FolderId, "dir3", Folder.PSEUDO_CLASS);

        ImportersLoader importersLoader = new ImportersLoaderList(Collections.emptyList(), Collections.emptyList());
        ComputationManager computationManager = Mockito.mock(ComputationManager.class);
        afs = new AppFileSystem("mem", storage);
        ad = new AppData(computationManager, importersLoader, Collections.singletonList(computationManager1 -> Collections.singletonList(afs)),
                Collections.emptyList(), Collections.emptyList());
    }

    @After
    public void tearDown() throws Exception {
        storage.close();
    }

    @Test
    public void test() throws IOException {
        assertEquals("mem", afs.getName());
        assertTrue(ad.getProjectFileClasses().isEmpty()); // no plugin
        Folder root = afs.getRootFolder();
        assertNotNull(root);
        Folder dir1 = (Folder) root.getChild("dir1");
        assertNotNull(dir1);
        assertTrue(dir1.isFolder());
        assertTrue(dir1.isWritable());
        assertEquals("dir1", dir1.getName());
        assertEquals(dir1.getName(), dir1.toString());
        assertEquals("mem", dir1.getFolder().getName());
        Folder dir2 = (Folder) dir1.getChild("dir2");
        assertNotNull(dir2);
        assertNotNull(dir2.getFolder());
        assertEquals("mem:/dir1", dir2.getFolder().getPath().toString());
        assertEquals(dir1.getChildren().size(), 2);
        Folder dir3 = (Folder) root.getChild("dir3");
        assertNull(dir3);
        String str = dir2.getPath().toString();
        assertEquals("mem:/dir1/dir2", str);
        Folder mayBeDir2 = (Folder) afs.getRootFolder().getChild("dir1/dir2");
        assertNotNull(mayBeDir2);
        assertEquals("dir2", mayBeDir2.getName());
        Folder mayBeDir2otherWay = (Folder) afs.getRootFolder().getChild("dir1", "dir2");
        assertNotNull(mayBeDir2otherWay);
        assertEquals("dir2", mayBeDir2otherWay.getName());

        Project project1 = dir2.createProject("project1", "test project");
        assertNotNull(project1);
        assertEquals("project1", project1.getName());
        assertEquals("test project", project1.getDescription());
        assertNotNull(project1.getIcon());
        assertNotNull(project1.getFolder());
        assertEquals("mem:/dir1/dir2", project1.getFolder().getPath().toString());
        assertTrue(project1.getRootFolder().getChildren().isEmpty());
        assertTrue(project1.getFileSystem() == afs);

        ProjectFolder dir4 = project1.getRootFolder().createFolder("dir4");
        assertTrue(dir4.isFolder());
        assertEquals("dir4", dir4.getName());
        assertNotNull(dir4.getFolder());
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
        assertEquals(ImmutableList.of("dir5", "dir6"), dir6.getPath().toList().subList(1, 3));
        assertEquals("dir5/dir6", dir6.getPath().toString());
        assertEquals("dir6", project1.getRootFolder().getChild("dir5/dir6").getName());
    }

}