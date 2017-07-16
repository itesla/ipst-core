/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.afs.core;

import eu.itesla_project.afs.storage.AppFileSystemStorage;
import eu.itesla_project.afs.storage.NodeId;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public abstract class ProjectNode extends NodeBase<ProjectFolder> {

    protected final NodeId projectId;

    protected final AppFileSystem fileSystem;

    protected ProjectNode(NodeId id, AppFileSystemStorage storage, NodeId projectId, AppFileSystem fileSystem) {
        super(id, storage);
        this.projectId = Objects.requireNonNull(projectId);
        this.fileSystem = Objects.requireNonNull(fileSystem);
    }

    public NodeId getId() {
        return id;
    }

    @Override
    public ProjectFolder getFolder() {
        NodeId parentNode = storage.getParentNode(id);
        return parentNode != null ? new ProjectFolder(parentNode, storage, projectId, fileSystem) : null;
    }

    public NodePath getPath() {
        return NodePath.find(this, path -> {
            StringBuilder builder = new StringBuilder();
            Iterator<String> it = path.iterator();
            it.next(); // skip project node
            while (it.hasNext()) {
                builder.append(it.next());
                if (it.hasNext()) {
                    builder.append(AppFileSystem.PATH_SEPARATOR);
                }
            }
            return builder.toString();
        });
    }

    public Project getProject() {
        return new Project(projectId, storage, fileSystem);
    }

    public void delete() {
        storage.deleteNode(id);
    }

    protected ProjectNode findProjectNode(NodeId nodeId) {
        String projectNodePseudoClass = storage.getNodePseudoClass(nodeId);
        if (ProjectFolder.PSEUDO_CLASS.equals(projectNodePseudoClass)) {
            return new ProjectFolder(nodeId, storage, projectId, fileSystem);
        } else {
            ProjectFileExtension extension = getProject().getFileSystem().getData().getProjectFileExtensionByPseudoClass(projectNodePseudoClass);
            return extension.createProjectFile(nodeId, storage, projectId, fileSystem);
        }
    }

    protected ProjectFile findProjectFile(NodeId nodeId) {
        String projectNodePseudoClass = storage.getNodePseudoClass(nodeId);
        ProjectFileExtension extension = getProject().getFileSystem().getData().getProjectFileExtensionByPseudoClass(projectNodePseudoClass);
        return extension.createProjectFile(nodeId, storage, projectId, fileSystem);
    }

    public List<ProjectFile> getBackwardDependencies() {
        return storage.getBackwardDependencies(id)
                .stream()
                .map(this::findProjectFile)
                .collect(Collectors.toList());
    }

    protected void notifyDependencyUpdate() {
        getBackwardDependencies().forEach(projectFile -> {
            projectFile.onDependencyUpdate();
            // propagate
            projectFile.notifyDependencyUpdate();
        });
    }
}
