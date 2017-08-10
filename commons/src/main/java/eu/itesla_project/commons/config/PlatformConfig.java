/**
 * Copyright (c) 2016, All partners of the iTesla project (http://www.itesla-project.eu/consortium)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.commons.config;

import eu.itesla_project.commons.io.CacheManager;
import eu.itesla_project.commons.io.FileUtil;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Objects;

/**
 *
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public class PlatformConfig {

    @Deprecated
    public static final Path CONFIG_DIR;

    @Deprecated
    public static final Path CACHE_DIR;

    @Deprecated
    private static final String CONFIG_NAME;

    private static PlatformConfig defaultConfig;

    private static CacheManager defaultCacheManager;

    protected final FileSystem fileSystem;

    protected final Path configDir;

    protected final Path cacheDir;

    protected final ModuleConfigContainer container;

    static {
        CONFIG_DIR = FileUtil.createDirectory(getDefaultConfigDir(FileSystems.getDefault()));

        CONFIG_NAME = System.getProperty("itesla.config.name");

        CACHE_DIR = FileUtil.createDirectory(getDefaultCacheDir(FileSystems.getDefault()));
    }

    public static synchronized void setDefaultConfig(PlatformConfig defaultConfig) {
        PlatformConfig.defaultConfig = defaultConfig;
    }

    public static synchronized PlatformConfig defaultConfig() {
        if (defaultConfig == null) {
            FileSystem fileSystem = FileSystems.getDefault();
            Path configDir = getDefaultConfigDir(fileSystem);
            Path cacheDir = getDefaultCacheDir(fileSystem);

            String configName = System.getProperty("itesla.config.name");
            if (configName != null) {
                try {
                    defaultConfig = new XmlPlatformConfig(fileSystem, configDir, cacheDir, configName);
                } catch (IOException | ParserConfigurationException | SAXException e) {
                    throw new RuntimeException(e);
                }
            } else {
                defaultConfig = new PropertiesPlatformConfig(fileSystem, configDir, cacheDir);
            }
        }
        return defaultConfig;
    }

    public static synchronized void setDefaultCacheManager(CacheManager defaultCacheManager) {
        PlatformConfig.defaultCacheManager = defaultCacheManager;
    }

    public static synchronized CacheManager defaultCacheManager() {
        if (defaultCacheManager == null) {
            defaultCacheManager = new CacheManager(CACHE_DIR);
        }
        return defaultCacheManager;
    }

    protected PlatformConfig(FileSystem fileSystem, ModuleConfigContainer container) {
        this(fileSystem, getDefaultConfigDir(fileSystem), getDefaultCacheDir(fileSystem), container);
    }

    protected PlatformConfig(FileSystem fileSystem, Path configDir, Path cacheDir, ModuleConfigContainer container) {
        this.fileSystem = Objects.requireNonNull(fileSystem);
        this.configDir = FileUtil.createDirectory(configDir);
        this.cacheDir = FileUtil.createDirectory(cacheDir);
        this.container = Objects.requireNonNull(container);
    }

    public FileSystem getFileSystem() {
        return fileSystem;
    }

    public Path getConfigDir() {
        return configDir;
    }

    public Path getCacheDir() {
        return cacheDir;
    }

    public boolean moduleExists(String name) {
        return container.moduleExists(name);
    }

    public ModuleConfig getModuleConfig(String name) {
        return container.getModuleConfig(name);
    }

    public ModuleConfig getModuleConfigIfExists(String name) {
        return container.getModuleConfigIfExists(name);
    }

    static Path getDefaultConfigDir(FileSystem fileSystem) {
        return getDirectory(fileSystem, "itesla.config.dir", ".itesla");
    }

    static Path getDefaultCacheDir(FileSystem fileSystem) {
        return getDirectory(fileSystem, "itesla.cache.dir", ".cache", "itesla");
    }

    private static Path getDirectory(FileSystem fileSystem, String propertyName, String... folders) {
        Objects.requireNonNull(fileSystem);
        Objects.requireNonNull(propertyName);
        Objects.requireNonNull(folders);

        Path directory;

        String directoryName = System.getProperty(propertyName);
        if (directoryName != null) {
            directory = fileSystem.getPath(directoryName);
        } else {
            directory = fileSystem.getPath(System.getProperty("user.home"), folders);
        }

        return directory;

    }
}
