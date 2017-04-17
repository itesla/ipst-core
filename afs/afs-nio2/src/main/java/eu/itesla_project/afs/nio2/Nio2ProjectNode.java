/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.afs.nio2;

import eu.itesla_project.afs.*;
import eu.itesla_project.commons.io.FileUtil;

import javax.xml.bind.annotation.*;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.ArrayList;
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

        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "")
        public static class Dependency {

            @XmlAttribute(name = "id", required = true)
            private String id;

            @XmlAttribute(name = "type", required = true)
            private String type;

            public Dependency() {
            }

            public Dependency(String id, String type) {
                this.id = Objects.requireNonNull(id);
                this.type = Objects.requireNonNull(type);
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = Objects.requireNonNull(id);
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = Objects.requireNonNull(type);
            }
        }


        @XmlAttribute(name = "id", required = true)
        private String id;

        @XmlElement(required = true)
        private final List<String> backwardDependencies = new ArrayList<>();

        @XmlElement(required = true)
        private final List<Dependency> dependencies = new ArrayList<>();

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

        public List<String> getBackwardDependencies() {
            return backwardDependencies;
        }

        public List<Dependency> getDependencies() {
            return dependencies;
        }

        public void addDependency(String id, String type) {
            dependencies.add(new Nio2ProjectNode.Metadata.Dependency(id, type));
        }

        public Dependency findDependencyByType(String type) {
            for (Dependency dependency : dependencies) {
                if (dependency.getType().equals(type)) {
                    return dependency;
                }
            }
            return null;
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

    public NodePath getPath() {
        return NodePath.getPath(this, Nio2ProjectNodePathToString.INSTANCE);
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
        getDependencies().forEach(projectFile -> ((Nio2ProjectNode) projectFile).removeBackwardDependency(this));

        // remove from central directory
        getProject().getCentralDirectory().remove(readMetadata().getId());

        try {
            FileUtil.removeDir(dir);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void addBackwardDependency(Nio2ProjectNode projectNode) {
        T metadata = readMetadata();
        metadata.getBackwardDependencies().add(projectNode.getId());
        metadata.save(dir);
    }

    public void removeBackwardDependency(Nio2ProjectNode projectNode) {
        T metadata = readMetadata();
        metadata.getBackwardDependencies().remove(projectNode.getId());
        metadata.save(dir);
    }

    private ProjectFile resolveBackwardDependency(String id) {
        String path = getProject().getCentralDirectory().getPath(id);
        if (path == null) {
            throw new RuntimeException("Backward dependency '" + id + "' not found");
        }
        ProjectFile projectFile = (ProjectFile) getProject().getRootFolder().getChild(path);
        if (projectFile == null) {
            throw new RuntimeException("Project file '" + path + "' not found");
        }
        return projectFile;
    }

    public List<ProjectFile> getBackwardDependencies() {
        return readMetadata().getBackwardDependencies().stream().map(this::resolveBackwardDependency).collect(Collectors.toList());
    }

    public Nio2Project getProject() {
        checkNotDeleted();
        return parent.getProject();
    }

    public abstract String getName();

    protected void invalidateCache() {
        getBackwardDependencies().forEach(projectFile -> ((Nio2ProjectNode) projectFile).invalidateCache());
    }

    @Override
    public String toString() {
        return getName();
    }
}
