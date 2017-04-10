/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.afs.nio2;

import eu.itesla_project.afs.ProjectFile;
import eu.itesla_project.afs.ProjectFolder;
import eu.itesla_project.afs.ProjectNode;
import eu.itesla_project.commons.io.FileUtil;

import javax.xml.bind.annotation.*;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public abstract class Nio2ProjectNode<T extends Nio2ProjectNode.Metadata> implements ProjectNode {

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static abstract class Metadata {

        @XmlAttribute(name = "id", required = true)
        private String id;

        @XmlElement(required = true)
        private final List<String> depended = new ArrayList<>();

        public Metadata() {
            this("");
        }

        public Metadata(String id) {
            this.id = Objects.requireNonNull(id);
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = Objects.requireNonNull(id);
        }

        public List<String> getDepended() {
            return depended;
        }

        protected abstract void save(Path dir);
    }

    protected final Path dir;

    protected final Nio2ProjectFolder parent;

    protected boolean deleted = false;

    public Nio2ProjectNode(Path dir, Nio2ProjectFolder parent) {
        this.dir = Objects.requireNonNull(dir);
        this.parent = parent;
    }

    protected abstract T readMetadata();

    protected void writeMetadata(Metadata metadata) {
        metadata.save(dir);
    }

    public String getId() {
        return readMetadata().getId();
    }

    public Path getDir() {
        checkNotDeleted();
        return dir;
    }

    public ProjectFolder getParent() {
        checkNotDeleted();
        return parent;
    }

    protected void addPath(List<String> path) {
        if (parent != null) {
            parent.addPath(path);
            path.add(getName());
        }
    }

    public List<String> getPathList() {
        List<String> path = new ArrayList<>(1);
        addPath(path);
        return path;
    }

    public String getPath() {
        StringBuilder builder = new StringBuilder();
        Iterator<String> it = getPathList().iterator();
        while (it.hasNext()) {
            builder.append(it.next());
            if (it.hasNext()) {
                builder.append(Nio2AppFileSystem.PATH_SEPARATOR);
            }
        }
        return builder.toString();
    }

    protected void checkNotDeleted() {
        if (deleted) {
            throw new RuntimeException("Deleted project node");
        }
    }

    protected abstract List<ProjectFile> getDependencies();

    public void delete() {
        checkNotDeleted();

        // remove backward dependency link
        getDependencies().forEach(projectFile -> ((Nio2ProjectNode) projectFile).removeDepended(this));

        // remove from central directory
        getProject().getCentralDirectory().remove(readMetadata().getId());

        try {
            FileUtil.removeDir(dir);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void addDepended(Nio2ProjectNode projectNode) {
        T metadata = readMetadata();
        metadata.getDepended().add(projectNode.getId());
        metadata.save(dir);
    }

    public void removeDepended(Nio2ProjectNode projectNode) {
        T metadata = readMetadata();
        metadata.getDepended().remove(projectNode.getId());
        metadata.save(dir);
    }

    private ProjectFile resolveDepended(String id) {
        String path = getProject().getCentralDirectory().getPath(id);
        if (path == null) {
            throw new RuntimeException("Depended '" + id + "' not found");
        }
        ProjectFile projectFile = (ProjectFile) getProject().getRootFolder().getChild(path);
        if (projectFile == null) {
            throw new RuntimeException("Project file '" + path + "' not found");
        }
        return projectFile;
    }

    public List<ProjectFile> getDepended() {
        return readMetadata().getDepended().stream().map(this::resolveDepended).collect(Collectors.toList());
    }

    public Nio2Project getProject() {
        checkNotDeleted();
        return parent.getProject();
    }

    public abstract String getName();

    protected void invalidateCache() {
        getDepended().forEach(projectFile -> ((Nio2ProjectNode) projectFile).invalidateCache());
    }

    @Override
    public String toString() {
        return getName();
    }
}
