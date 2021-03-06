/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.afs.local;

import eu.itesla_project.afs.AppFileSystem;
import eu.itesla_project.afs.local.storage.LocalAppFileSystemStorage;
import eu.itesla_project.afs.local.storage.LocalFileScanner;
import eu.itesla_project.afs.local.storage.LocalFolderScanner;
import eu.itesla_project.computation.ComputationManager;

import java.util.List;

/**
 *
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public class LocalAppFileSystem extends AppFileSystem {

    public LocalAppFileSystem(LocalAppFileSystemConfig config, List<LocalFileScanner> fileScanners,
                              List<LocalFolderScanner> folderScanners, ComputationManager computationManager) {
        super(config.getDriveName(),
                config.isRemotelyAccessible(),
                new LocalAppFileSystemStorage(config.getRootDir(), config.getDriveName(), fileScanners, folderScanners,
                                              computationManager));
    }
}
