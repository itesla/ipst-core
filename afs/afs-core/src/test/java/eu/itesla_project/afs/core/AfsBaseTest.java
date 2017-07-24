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

    private static class FooFile extends ProjectFile {

        private FooFile(NodeId id, AppFileSystemStorage storage, NodeId projectId, AppFileSystem fileSystem) {
            super(id, storage, projectId, fileSystem);
        }

        @Override
        public FileIcon getIcon() {
            return new FileIcon("?", new byte[]{});
        }
    }

    private static class FooFileBuilder implements ProjectFileBuilder<FooFile> {

        private final NodeId folderId;

        private final AppFileSystemStorage storage;

        private final NodeId projectId;

        private final AppFileSystem fileSystem;

        private FooFileBuilder(NodeId folderId, AppFileSystemStorage storage, NodeId projectId, AppFileSystem fileSystem) {
            this.folderId = folderId;
            this.storage = storage;
            this.projectId = projectId;
            this.fileSystem = fileSystem;
        }

        @Override
        public FooFile build() {
            NodeId id = storage.createNode(folderId, "foo", "foo");
            return new FooFile(id, storage, projectId, fileSystem);
        }
    }

    private static class FooFileExtension implements ProjectFileExtension {
        @Override
        public Class<? extends ProjectFile> getProjectFileClass() {
            return FooFile.class;
        }

        @Override
        public String getProjectFilePseudoClass() {
            return "foo";
        }

        @Override
        public Class<? extends ProjectFileBuilder<? extends ProjectFile>> getProjectFileBuilderClass() {
            return FooFileBuilder.class;
        }

        @Override
        public FooFile createProjectFile(NodeId id, AppFileSystemStorage storage, NodeId projectId, AppFileSystem fileSystem) {
            return new FooFile(id, storage, projectId, fileSystem);
        }

        @Override
        public FooFileBuilder createProjectFileBuilder(NodeId folderId, AppFileSystemStorage storage, NodeId projectId, AppFileSystem fileSystem) {
            return new FooFileBuilder(folderId, storage, projectId, fileSystem);
        }
    }

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
                Collections.emptyList(), Collections.singletonList(new FooFileExtension()));
    }

    @After
    public void tearDown() throws Exception {
        storage.close();
    }

    @Test
    public void baseTest() throws IOException {
        assertSame(afs, ad.getFileSystem("mem"));
        assertNull(ad.getFileSystem("???"));
        assertEquals("mem", afs.getName());
        assertEquals(1, ad.getProjectFileClasses().size());
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

    @Test
    public void moveToTest() throws IOException {
        Project project = afs.getRootFolder().createProject("test", "");
        ProjectFolder test1 = project.getRootFolder().createFolder("test1");
        ProjectFolder test2 = project.getRootFolder().createFolder("test2");
        FooFile file = test1.fileBuilder(FooFileBuilder.class)
                .build();
        assertEquals(test1.getId(), file.getFolder().getId());
        assertEquals(1, test1.getChildren().size());
        assertTrue(test2.getChildren().isEmpty());
        file.moveTo(test2);
        assertEquals(test2.getId(), file.getFolder().getId());
        assertTrue(test1.getChildren().isEmpty());
        assertEquals(1, test2.getChildren().size());
    }

}