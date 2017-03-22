/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.afs.local;

import eu.itesla_project.afs.nio2.Nio2AppFileSystem;

import java.nio.file.FileSystems;

/**
 *
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public class LocalAppFileSystem extends Nio2AppFileSystem {

    private static final LocalAppFileSystemConfig CONFIG = LocalAppFileSystemConfig.load();

    public LocalAppFileSystem() {
        super(FileSystems.getDefault(), CONFIG.getDriveName(), CONFIG.getRootDir());
    }
}