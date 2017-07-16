/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.afs.case_;

import eu.itesla_project.afs.core.AfsException;
import eu.itesla_project.afs.core.AppFileSystem;
import eu.itesla_project.afs.core.ProjectFileBuilder;
import eu.itesla_project.afs.storage.AppFileSystemStorage;
import eu.itesla_project.afs.storage.NodeId;

import java.util.Objects;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public class GroovyScriptBuilder implements ProjectFileBuilder<GroovyScript> {

    private final NodeId folderId;

    private final AppFileSystemStorage storage;

    private final NodeId projectId;

    private final AppFileSystem fileSystem;

    private String name;

    private String content;

    public GroovyScriptBuilder(NodeId folderId, AppFileSystemStorage storage, NodeId projectId, AppFileSystem fileSystem) {
        this.folderId = Objects.requireNonNull(folderId);
        this.storage = Objects.requireNonNull(storage);
        this.projectId = Objects.requireNonNull(projectId);
        this.fileSystem = Objects.requireNonNull(fileSystem);
    }

    public GroovyScriptBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public GroovyScriptBuilder withContent(String content) {
        this.content = content;
        return this;
    }

    @Override
    public GroovyScript build() {
        // check parameters
        if (name == null) {
            throw new AfsException("Name is not set");
        }
        if (content == null) {
            throw new AfsException("Content is not set");
        }

        try {
            // create project file
            NodeId id = storage.createNode(folderId, name, GroovyScript.PSEUDO_CLASS);

            // store script
            storage.setStringAttribute(id, "script", content);

            storage.commit();

            return new GroovyScript(id, storage, projectId, fileSystem);
        } catch (Exception e) {
            storage.rollback();
            throw e;
        }
    }
}
