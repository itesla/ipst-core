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
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public class Nio2ProjectFolder extends Nio2ProjectNode implements ProjectFolder {

    private final Nio2ProjectFolder parent;

    private final Nio2Project project;

    Nio2ProjectFolder(Path dir, Nio2ProjectFolder parent, Nio2Project project,
                      CentralDirectory centralDirectory) {
        super(dir, centralDirectory);
        this.parent = parent;
        this.project = Objects.requireNonNull(project);
    }

    @Override
    public ProjectFolder createFolder(String name) {
        checkNotDeleted();

        // create the directory
        Path childDir = dir.resolve(name);
        try {
            Files.createDirectories(childDir);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        // create metadata
        Metadata metadata = Metadata.create(Nio2ProjectFolder.class.getName().toString());
        metadata.save(childDir);

        // create project node
        Nio2ProjectFolder folder = new Nio2ProjectFolder(childDir, this, project, project.getCentralDirectory());

        // put id in the central directory
        project.getCentralDirectory().add(metadata.getId(), folder.getPath().toString());

        return folder;
    }

    @Override
    public boolean isFolder() {
        checkNotDeleted();
        return true;
    }

    @Override
    public List<ProjectNode> getChildren() {
        checkNotDeleted();
        try (Stream<Path> stream = Files.list(dir)) {
            List<ProjectNode> children = new ArrayList<>();
            stream.forEach(path -> {
                ProjectNode node = null;
                if (Files.isDirectory(path)) {
                    Path metadataFile = path.resolve(Metadata.XML_FILE_NAME);
                    if (Files.exists(metadataFile)) {
                        Metadata metadata = Metadata.read(path);
                        if (metadata.getNodeClass().equals(Nio2ProjectFolder.class.getName().toString())) {
                            String name = path.getFileName().toString();
                            node = new Nio2ProjectFolder(path, this, getProject(), project.getCentralDirectory());
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

    public Nio2ProjectNode getChild(String path) {
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
        return (Nio2ProjectNode) node;
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
    protected List<ProjectFile> getDependencies() {
        return Collections.emptyList();
    }

    @Override
    public <F extends ProjectFile, B extends ProjectFileBuilder<F>> B fileBuilder(Class<B> clazz) {
        checkNotDeleted();
        Nio2ProjectFileBuilderFactory factory = project.getFileSystem().getProjectFileBuilderFactory(clazz);
        ProjectFileBuilder<F> builder = (ProjectFileBuilder<F>) factory.create(this);
        return (B) builder;
    }
}

