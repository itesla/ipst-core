/**
 * Copyright (c) 2016, All partners of the iTesla project (http://www.itesla-project.eu/consortium)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.commons.config;

import eu.itesla_project.commons.io.CacheManager;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.*;
import java.util.Objects;

/**
 *
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public abstract class PlatformConfig {

    @Deprecated
    public static final Path CONFIG_DIR;

    @Deprecated
    public static final Path CACHE_DIR;

    @Deprecated
    private static final String CONFIG_NAME;

    private static PlatformConfig defaultConfig;

    private static CacheManager defaultCacheManager;

    private final FileSystem fileSystem;

    private final Path configDir;

    private final Path cacheDir;

    static {
        CONFIG_DIR = createConfigDir(getDefaultConfigDir(FileSystems.getDefault()));

        CONFIG_NAME = System.getProperty("itesla.config.name");

        CACHE_DIR = createCacheDir(getDefaultCacheDir(FileSystems.getDefault()));
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
                    defaultConfig = new XmlPlatformConfig(FileSystems.getDefault(), configDir, cacheDir, configName);
                } catch (IOException | ParserConfigurationException | SAXException e) {
                    throw new RuntimeException(e);
                }
            } else {
                defaultConfig = new PropertiesPlatformConfig(FileSystems.getDefault(), configDir, cacheDir);
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

    protected PlatformConfig(FileSystem fileSystem) {
        this(fileSystem, getDefaultConfigDir(fileSystem), getDefaultCacheDir(fileSystem));
    }

    protected PlatformConfig(FileSystem fileSystem, Path configDir, Path cacheDir) {
        this.fileSystem = Objects.requireNonNull(fileSystem);
        this.configDir = createConfigDir(configDir);
        this.cacheDir = createCacheDir(cacheDir);
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

    public abstract boolean moduleExists(String name);

    public abstract ModuleConfig getModuleConfig(String name);

    public abstract ModuleConfig getModuleConfigIfExists(String name);

    static Path getDefaultConfigDir(FileSystem fileSystem) {
        Objects.requireNonNull(fileSystem);

        Path configDir;

        String iteslaConfigDir = System.getProperty("itesla.config.dir");
        if (iteslaConfigDir != null) {
            configDir = fileSystem.getPath(iteslaConfigDir);
        } else {
            configDir = fileSystem.getPath(System.getProperty("user.home"), ".itesla");
        }

        return configDir;
    }

    private static Path createConfigDir(Path configDir) {
        Objects.requireNonNull(configDir);
        try {
            if (! (Files.isDirectory(configDir))) {
                Files.createDirectories(configDir);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return configDir;
    }

    static Path getDefaultCacheDir(FileSystem fileSystem) {
        Objects.requireNonNull(fileSystem);

        Path cacheDir;

        String iteslaCacheDir = System.getProperty("itesla.cache.dir");
        if (iteslaCacheDir != null) {
            cacheDir = fileSystem.getPath(iteslaCacheDir);
        } else {
            cacheDir = fileSystem.getPath(System.getProperty("user.home"), ".cache", "itesla");
        }

        return cacheDir;
    }

    private static Path createCacheDir(Path cacheDir) {
        Objects.requireNonNull(cacheDir);
        try {
            Files.createDirectories(cacheDir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return cacheDir;
    }
}
