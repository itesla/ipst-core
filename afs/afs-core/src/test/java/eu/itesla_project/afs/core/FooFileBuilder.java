/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.afs.core;

import eu.itesla_project.afs.storage.AppFileSystemStorage;
import eu.itesla_project.afs.storage.NodeId;

/**
 *
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
class FooFileBuilder implements ProjectFileBuilder<FooFile> {

    private final NodeId folderId;

    private final AppFileSystemStorage storage;

    private final NodeId projectId;

    private final AppFileSystem fileSystem;

    FooFileBuilder(NodeId folderId, AppFileSystemStorage storage, NodeId projectId, AppFileSystem fileSystem) {
        this.folderId = folderId;
        this.storage = storage;
        this.projectId = projectId;
        this.fileSystem = fileSystem;
    }

    @Override
    public FooFile build() {
        NodeId id = storage.createNode(folderId, "foo", "foo");
        return new FooFile(id, storage, projectId, fileSystem);
    }
}