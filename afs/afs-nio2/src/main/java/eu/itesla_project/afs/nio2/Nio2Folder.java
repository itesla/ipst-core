/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.afs.nio2;

import eu.itesla_project.afs.AppFileSystem;
import eu.itesla_project.afs.Folder;
import eu.itesla_project.afs.Node;
import eu.itesla_project.afs.Project;
import eu.itesla_project.commons.jaxb.JaxbUtil;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public class Nio2Folder extends Nio2Node implements Folder {

    private static final boolean SHOW_HIDDEN_FILEs = true;

    private static boolean isNotHidden(Path file) {
        try {
            return !(SHOW_HIDDEN_FILEs && Files.isHidden(file));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    Nio2Folder(Path path, Nio2Folder parent, Nio2AppFileSystem fileSystem) {
        super(path, parent, fileSystem);
    }

    @Override
    public String getName() {
        return parent == null ? fileSystem.getName() : path.getFileName().toString();
    }

    @Override
    public boolean isFolder() {
        return true;
    }

    public boolean isRoot() {
        return parent == null;
    }

    private Collection<Node> scanProject(Path path) {
        if (Files.isDirectory(path)) {
            Path projectMetadataXml = path.resolve(Nio2Project.Metadata.XML_FILE_NAME);
            String nodeName = path.getFileName().toString();
            if (Files.isRegularFile(projectMetadataXml)) {
                return Collections.singleton(new Nio2Project(path, this, fileSystem, nodeName, JaxbUtil.unmarchallFile(Nio2Project.Metadata.class, projectMetadataXml)));
            }
        }
        return Collections.emptyList();
    }

    private Collection<Node> scanFolder(Path path) {
        if (Files.isDirectory(path)) {
            return Collections.singleton(new Nio2Folder(path, this, fileSystem));
        }
        return Collections.emptyList();
    }

    @Override
    public List<Node> getChildren() {
        List<Node> children = new ArrayList<>();
        addChildren(children);
        return children;
    }

    private void addChildren(List<Node> children) {
        try {
            if (Files.isReadable(path)) {
                Files.list(path)
                        .filter(Nio2Folder::isNotHidden)
                        .forEach(child -> {
                            Collection<Node> nodes = scanProject(child);
                            if (nodes.isEmpty()) {
                                for (Nio2FileScanner scanner : fileSystem.getFileScanners()) {
                                    nodes = scanner.scan(this, child);
                                    if (nodes.size() > 0) {
                                        break;
                                    }
                                }
                                if (nodes.isEmpty()) {
                                    nodes = scanFolder(child);
                                }
                            }
                            children.addAll(nodes);
                        });
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static Node getChild(Node parent, String path) {
        Objects.requireNonNull(parent);
        Objects.requireNonNull(path);
        Node node = parent;
        String[] names = path.split(AppFileSystem.PATH_SEPARATOR);
        for (int i = 0; i < names.length; i++) {
            String name = names[i];
            if (node.isFolder()) {
                Node child = ((Folder) node).getChildren().stream()
                        .filter(folder -> folder.getName().equals(name))
                        .findFirst()
                        .orElse(null);
                if (child == null) {
                    return null;
                }
                node = child;
            } else {
                if (i < names.length - 1) {
                    return null; // path is too long
                }
            }
        }
        return node;
    }

    @Override
    public Node getChild(String name, String... more) {
        Node child = getChild(this, name);
        for (String otherName : more) {
            child = getChild(child, otherName);
        }
        return child;
    }

    @Override
    public Project createProject(String projectName, String description) {
        Path projectDir = path.resolve(projectName);
        if (Files.exists(projectDir)) {
            throw new RuntimeException("Projet " + projectName + " already exist in folder " + path);
        }
        try {
            Files.createDirectory(projectDir);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        Nio2Project.Metadata projectMetadata = new Nio2Project.Metadata(description);
        JaxbUtil.marshallElement(Nio2Project.Metadata.class, projectMetadata, projectDir.resolve(Nio2Project.Metadata.XML_FILE_NAME));
        return new Nio2Project(projectDir, this, fileSystem, projectName, projectMetadata);
    }
}

