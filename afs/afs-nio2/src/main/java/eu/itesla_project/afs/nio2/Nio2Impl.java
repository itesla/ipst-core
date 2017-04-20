/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.afs.nio2;

import eu.itesla_project.afs.ProjectFile;
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
public class Nio2Impl {

    private final Path dir;

    protected boolean deleted = false;

    public Nio2Impl(Path dir) {
        this.dir = Objects.requireNonNull(dir);
    }

    private Metadata readMetadata() {
        return Metadata.read(dir);
    }

    private void writeMetadata(Metadata metadata) {
        metadata.save(dir);
    }

    public String getId() {
        return readMetadata().getId();
    }

    public Path getDir() {
        checkNotDeleted();
        return dir;
    }

    public void checkNotDeleted() {
        if (deleted) {
            throw new RuntimeException("Deleted project node");
        }
    }

    public void delete(Nio2Project project) {
        checkNotDeleted();

        String id = readMetadata().getId();

        // remove backward dependency link
        getDependencies(project).forEach(projectFile -> ((Nio2ProjectNode) projectFile).getImpl().removeBackwardDependency(id));

        // remove from central directory
        project.getCentralDirectory().remove(id);

        try {
            FileUtil.removeDir(dir);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void addBackwardDependency(String id) {
        Metadata metadata = readMetadata();
        metadata.getBackwardDependencies().add(id);
        metadata.save(dir);
    }

    public void removeBackwardDependency(String id) {
        Metadata metadata = readMetadata();
        metadata.getBackwardDependencies().remove(id);
        metadata.save(dir);
    }

    public String getDependencyPath(String type, Nio2Project project) {
        return project.getCentralDirectory().getPath(readMetadata().findDependencyByType(type).getId());
    }

    private ProjectFile resolveDependency(String id, Nio2Project project) {
        String path = project.getCentralDirectory().getPath(id);
        if (path == null) {
            throw new RuntimeException("Dependency '" + id + "' not found");
        }
        ProjectFile projectFile = (ProjectFile) project.getRootFolder().getChild(path);
        if (projectFile == null) {
            throw new RuntimeException("Project file '" + path + "' not found");
        }
        return projectFile;
    }

    public List<ProjectFile> getDependencies(Nio2Project project) {
        return readMetadata().getDependencies().stream()
                .map(dependency -> resolveDependency(dependency.getId(), project))
                .collect(Collectors.toList());
    }

    public List<ProjectFile> getBackwardDependencies(Nio2Project project) {
        return readMetadata().getBackwardDependencies().stream()
                .map(id -> resolveDependency(id, project))
                .collect(Collectors.toList());
    }

    public String getName() {
        checkNotDeleted();
        return dir.getFileName().toString();
    }

    public void invalidateBackwardDependenciesCache(Nio2Project project) {
        getBackwardDependencies(project).forEach(projectFile -> projectFile.onCacheInvalidation());
    }
}
