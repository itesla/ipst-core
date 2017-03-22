/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.afs.hdfs;

import eu.itesla_project.afs.nio2.Nio2AppFileSystem;
import hdfs.jsr203.HadoopFileSystem;
import hdfs.jsr203.HadoopFileSystemProvider;

import java.io.IOException;
import java.nio.file.FileSystem;

/**
 *
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public class HdfsAppFileSystem extends Nio2AppFileSystem {

    private static final HdfsAppFileSystemConfig CONFIG = HdfsAppFileSystemConfig.load();

    private static FileSystem createHadoopFileSystem() {
        try {
            return new HadoopFileSystem(new HadoopFileSystemProvider(), CONFIG.getHost(), CONFIG.getPort());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public HdfsAppFileSystem() {
        super(createHadoopFileSystem(), CONFIG.getDriveName(), CONFIG.getRootDir());
    }
}
