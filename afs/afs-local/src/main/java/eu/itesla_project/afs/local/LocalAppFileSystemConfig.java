/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.afs.local;

import eu.itesla_project.commons.config.ModuleConfig;
import eu.itesla_project.commons.config.PlatformConfig;

import java.util.Objects;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public class LocalAppFileSystemConfig {

    private static final String DEFAULT_DRIVE_NAME = "local";

    private String driveName;

    private String rootDir;

    public static LocalAppFileSystemConfig load() {
        return load(PlatformConfig.defaultConfig());
    }

    public static LocalAppFileSystemConfig load(PlatformConfig platformConfig) {
        ModuleConfig moduleConfig = platformConfig.getModuleConfigIfExists("local-app-file-system");
        String rootDir = null;
        String driveName = DEFAULT_DRIVE_NAME;
        if (moduleConfig != null) {
            driveName = moduleConfig.getStringProperty("drive-name", DEFAULT_DRIVE_NAME);
            rootDir = moduleConfig.getStringProperty("root-dir", null);
        }
        return new LocalAppFileSystemConfig(driveName, rootDir);
    }

    public LocalAppFileSystemConfig(String driveName, String rootDir) {
        this.driveName = Objects.requireNonNull(driveName);
        this.rootDir = rootDir;
    }

    public String getDriveName() {
        return driveName;
    }

    public LocalAppFileSystemConfig setDriveName(String driveName) {
        this.driveName = Objects.requireNonNull(driveName);
        return this;
    }

    public String getRootDir() {
        return rootDir;
    }

    public LocalAppFileSystemConfig setRootDir(String rootDir) {
        this.rootDir = rootDir;
        return this;
    }
}
