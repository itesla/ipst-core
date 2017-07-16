/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.afs.local;

import eu.itesla_project.commons.config.ModuleConfig;
import eu.itesla_project.commons.config.PlatformConfig;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public class LocalAppFileSystemConfig {

    private String driveName;

    private Path rootDir;

    public static List<LocalAppFileSystemConfig> load() {
        return load(PlatformConfig.defaultConfig());
    }

    public static List<LocalAppFileSystemConfig> load(PlatformConfig platformConfig) {
        List<LocalAppFileSystemConfig> configs = new ArrayList<>();
        ModuleConfig moduleConfig = platformConfig.getModuleConfigIfExists("local-app-file-system");
        if (moduleConfig != null) {
            if (moduleConfig.hasProperty("drive-name") && moduleConfig.hasProperty("root-dir")) {
                String driveName = moduleConfig.getStringProperty("drive-name");
                Path rootDir = moduleConfig.getPathProperty("root-dir");
                configs.add(new LocalAppFileSystemConfig(driveName, rootDir));
            }
            for (int i = 0; i < 9; i++) {
                if (moduleConfig.hasProperty("drive-name-" + i) && moduleConfig.hasProperty("root-dir-" + i)) {
                    String driveName = moduleConfig.getStringProperty("drive-name-" + i);
                    Path rootDir = moduleConfig.getPathProperty("root-dir-" + i);
                    configs.add(new LocalAppFileSystemConfig(driveName, rootDir));
                }
            }
        }
        return configs;
    }

    private static Path checkRootDir(Path rootDir) {
        Objects.requireNonNull(rootDir);
        if (!Files.isDirectory(rootDir)) {
            throw new RuntimeException("Root path " + rootDir + " is not a directory");
        }
        return rootDir;
    }

    public LocalAppFileSystemConfig(String driveName, Path rootDir) {
        this.driveName = Objects.requireNonNull(driveName);
        this.rootDir = checkRootDir(rootDir);
    }

    public String getDriveName() {
        return driveName;
    }

    public LocalAppFileSystemConfig setDriveName(String driveName) {
        this.driveName = Objects.requireNonNull(driveName);
        return this;
    }

    public Path getRootDir() {
        return rootDir;
    }

    public LocalAppFileSystemConfig setRootDir(Path rootDir) {
        this.rootDir = checkRootDir(rootDir);
        return this;
    }
}
