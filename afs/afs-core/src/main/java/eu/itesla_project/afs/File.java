/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.afs;

import eu.itesla_project.afs.storage.AppFileSystemStorage;
import eu.itesla_project.afs.storage.NodeId;

import java.util.Objects;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public class File extends Node {

    protected final FileIcon icon;

    public File(NodeId id, AppFileSystemStorage storage, AppFileSystem fileSystem, FileIcon icon) {
        super(id, storage, fileSystem, false);
        this.icon = Objects.requireNonNull(icon);
    }

    public FileIcon getIcon() {
        return icon;
    }

    public String getDescription() {
        return storage.getStringAttribute(id, "description");
    }
}
