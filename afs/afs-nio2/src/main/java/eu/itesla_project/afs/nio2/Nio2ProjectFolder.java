/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.afs.nio2;

import eu.itesla_project.afs.*;
import eu.itesla_project.commons.jaxb.JaxbUtil;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public class Nio2ProjectFolder extends Nio2ProjectNode<Nio2ProjectFolder.Metadata> implements ProjectFolder {

    @XmlRootElement(name = "projectFolderMetadata")
    public static class Metadata extends Nio2ProjectNode.Metadata {

        private static final String XML_FILE_NAME = "projectFolderMetadata.xml";

        public static Metadata create() {
            return new Metadata(UUID.randomUUID().toString());
        }

        public static Metadata read(Path dir) {
            return JaxbUtil.unmarchallFile(Metadata.class, dir.resolve(XML_FILE_NAME));
        }

        public Metadata() {
        }

        public Metadata(String id) {
            super(id);
        }

        public void save(Path dir) {
            JaxbUtil.marshallElement(Metadata.class, this, dir.resolve(XML_FILE_NAME));
        }
    }

    private final String name;

    private final Nio2Project project;

    Nio2ProjectFolder(Path dir, Nio2ProjectFolder parent, String name, Nio2Project project) {
        super(dir, parent);
        this.name = Objects.requireNonNull(name);
        this.project = Objects.requireNonNull(project);
    }

    @Override
    protected Metadata readMetadata() {
        return Metadata.read(dir);
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
        Metadata metadata = Metadata.create();
        metadata.save(childDir);

        // create project node
        Nio2ProjectFolder folder = new Nio2ProjectFolder(childDir, this, name, project);

        // put id in the central directory
        project.getCentralDirectory().add(metadata.getId(), folder.getPath());

        return folder;
    }

    @Override
    public boolean isFolder() {
        checkNotDeleted();
        return true;
    }

    private static ProjectNode scanFolder(Path path, Nio2ProjectFolder parent) {
        if (Files.isDirectory(path)) {
            Path metadataFile = path.resolve(Metadata.XML_FILE_NAME);
            if (Files.exists(metadataFile)) {
                String name = path.getFileName().toString();
                Metadata metadata = JaxbUtil.unmarchallFile(Metadata.class, metadataFile);
                return new Nio2ProjectFolder(path, parent, name, parent.getProject());
            }
        }
        return null;
    }

    @Override
    public List<ProjectNode> getChildren() {
        checkNotDeleted();
        try (Stream<Path> stream = Files.list(dir)) {
            List<ProjectNode> children = new ArrayList<>();
            stream.forEach(path -> {
                ProjectNode node = scanFolder(path, this);
                if (node == null) {
                    for (Nio2ProjectFileScanner scanner : project.getFileSystem().getProjectFileScanners()) {
                        node = scanner.scan(Nio2ProjectFolder.this, path);
                        if (node != null) {
                            break;
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
    public String getName() {
        checkNotDeleted();
        return name;
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

