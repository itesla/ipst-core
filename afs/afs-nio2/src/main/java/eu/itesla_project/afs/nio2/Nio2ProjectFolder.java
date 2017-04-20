/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.afs.nio2;

import eu.itesla_project.afs.*;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public class Nio2ProjectFolder implements ProjectFolder, Nio2ProjectNode {

    private final Nio2ProjectFolder parent;

    private final Nio2Project project;

    protected final Nio2Impl impl;

    Nio2ProjectFolder(Path dir, Nio2ProjectFolder parent, Nio2Project project) {
        impl = new Nio2Impl(dir);
        this.parent = parent;
        this.project = Objects.requireNonNull(project);
    }

    @Override
    public Nio2Impl getImpl() {
        return impl;
    }

    @Override
    public ProjectFolder createFolder(String name) {
        // create the directory
        Path childDir = impl.getDir().resolve(name);
        try {
            Files.createDirectories(childDir);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        // create metadata
        Metadata metadata = Metadata.create(Nio2ProjectFolder.class.getName().toString());
        metadata.save(childDir);

        // create project node
        Nio2ProjectFolder folder = new Nio2ProjectFolder(childDir, this, project);

        // put id in the central directory
        project.getCentralDirectory().add(metadata.getId(), folder.getPath().toString());

        return folder;
    }

    @Override
    public boolean isFolder() {
        impl.checkNotDeleted();
        return true;
    }

    @Override
    public List<ProjectNode> getChildren() {
        try (Stream<Path> stream = Files.list(impl.getDir())) {
            List<ProjectNode> children = new ArrayList<>();
            stream.forEach(path -> {
                ProjectNode node = null;
                if (Files.isDirectory(path)) {
                    Path metadataFile = path.resolve(Metadata.XML_FILE_NAME);
                    if (Files.exists(metadataFile)) {
                        Metadata metadata = Metadata.read(path);
                        if (metadata.getNodeClass().equals(Nio2ProjectFolder.class.getName().toString())) {
                            String name = path.getFileName().toString();
                            node = new Nio2ProjectFolder(path, this, getProject());
                        } else {
                            for (Nio2ProjectFileScanner scanner : project.getFileSystem().getProjectFileScanners()) {
                                if (metadata.getNodeClass().equals(scanner.getType().getName().toString())) {
                                    node = scanner.load(Nio2ProjectFolder.this, path);
                                    if (node != null) {
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
                if (node != null) {
                    children.add(node);
                }
            });
            return children;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private ProjectNode getChild2(String name) {
        for (ProjectNode child : getChildren()) {
            if (child.getName().equals(name)) {
                return child;
            }
        }
        return null;
    }

    public ProjectNode getChild(String path) {
        Objects.requireNonNull(path);
        ProjectNode node = this;
        if (node.isFolder()) {
            for (String childName : path.split(AppFileSystem.PATH_SEPARATOR)) {
                node = ((Nio2ProjectFolder) node).getChild2(childName);
                if (node == null) {
                    return null;
                }
            }
        }
        return node;
    }

    @Override
    public Nio2ProjectFolder getParent() {
        return parent;
    }

    @Override
    public Nio2Project getProject() {
        return project; // because parent can be null
    }

    @Override
    public <F extends ProjectFile, B extends ProjectFileBuilder<F>> B fileBuilder(Class<B> clazz) {
        impl.checkNotDeleted();
        Nio2ProjectFileBuilderFactory factory = project.getFileSystem().getProjectFileBuilderFactory(clazz);
        ProjectFileBuilder<F> builder = (ProjectFileBuilder<F>) factory.create(this);
        return (B) builder;
    }

    @Override
    public String getName() {
        return impl.getName();
    }

    @Override
    public NodePath getPath() {
        return NodePath.getPath(this, Nio2ProjectNodePathToString.INSTANCE);
    }

    @Override
    public void delete() {
        impl.delete(getProject());
    }

    @Override
    public List<ProjectFile> getBackwardDependencies() {
        return impl.getBackwardDependencies(getProject());
    }
}

