/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.afs.hdfs;

import eu.itesla_project.commons.config.ModuleConfig;
import eu.itesla_project.commons.config.PlatformConfig;

import java.util.Objects;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public class HdfsAppFileSystemConfig {

    private static final String DEFAULT_DRIVE_NAME = "hdfs";

    public static HdfsAppFileSystemConfig load() {
        return load(PlatformConfig.defaultConfig());
    }

    public static HdfsAppFileSystemConfig load(PlatformConfig platformConfig) {
        ModuleConfig moduleConfig = platformConfig.getModuleConfig("hdfs-app-file-system");
        String host = moduleConfig.getStringProperty("host");
        int port = moduleConfig.getIntProperty("port");
        String driveName = moduleConfig.getStringProperty("drive-name", DEFAULT_DRIVE_NAME);
        String rootDir = moduleConfig.getStringProperty("root-dir", null);
        return new HdfsAppFileSystemConfig(host, port, driveName, rootDir);
    }

    private String host;

    private int port;

    private String driveName;

    private String rootDir;

    public HdfsAppFileSystemConfig(String host, int port, String driveName, String rootDir) {
        this.host = Objects.requireNonNull(host);
        this.port = port;
        this.driveName = Objects.requireNonNull(driveName);
        this.rootDir = rootDir;
    }

    public String getHost() {
        return host;
    }

    public HdfsAppFileSystemConfig setHost(String host) {
        this.host = Objects.requireNonNull(host);
        return this;
    }

    public int getPort() {
        return port;
    }

    public HdfsAppFileSystemConfig setPort(int port) {
        this.port = port;
        return this;
    }

    public String getDriveName() {
        return driveName;
    }

    public HdfsAppFileSystemConfig setDriveName(String driveName) {
        this.driveName = Objects.requireNonNull(driveName);
        return this;
    }

    public String getRootDir() {
        return rootDir;
    }

    public HdfsAppFileSystemConfig setRootDir(String rootDir) {
        this.rootDir = rootDir;
        return this;
    }
}
