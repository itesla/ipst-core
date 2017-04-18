/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.afs.nio2;

import eu.itesla_project.afs.NodePath;
import eu.itesla_project.afs.ProjectFile;
import eu.itesla_project.afs.ProjectFolder;
import eu.itesla_project.afs.ProjectNode;
import eu.itesla_project.commons.io.FileUtil;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public abstract class Nio2ProjectNode implements ProjectNode {

    protected final Path dir;

    protected final Nio2ProjectFolder parent;

    protected boolean deleted = false;

    public Nio2ProjectNode(Path dir, Nio2ProjectFolder parent) {
        this.dir = Objects.requireNonNull(dir);
        this.parent = parent;
    }

    protected Metadata readMetadata() {
        return Metadata.read(dir);
    }

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
        Metadata metadata = readMetadata();
        metadata.getBackwardDependencies().add(projectNode.getId());
        metadata.save(dir);
    }

    public void removeBackwardDependency(Nio2ProjectNode projectNode) {
        Metadata metadata = readMetadata();
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
