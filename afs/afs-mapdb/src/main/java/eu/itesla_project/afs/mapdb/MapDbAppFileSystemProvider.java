/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.afs.mapdb;

import com.google.auto.service.AutoService;
import eu.itesla_project.afs.AppFileSystem;
import eu.itesla_project.afs.AppFileSystemProvider;
import eu.itesla_project.afs.mapdb.storage.MapDbAppFileSystemStorage;
import eu.itesla_project.computation.ComputationManager;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
@AutoService(AppFileSystemProvider.class)
public class MapDbAppFileSystemProvider implements AppFileSystemProvider {

    private final List<MapDbAppFileSystemConfig> configs;

    private final BiFunction<String, Path, MapDbAppFileSystemStorage> storageProvider;

    public MapDbAppFileSystemProvider() {
        this(MapDbAppFileSystemConfig.load(), (name, path) -> MapDbAppFileSystemStorage.createMmapFile(name, path.toFile()));
    }

    public MapDbAppFileSystemProvider(List<MapDbAppFileSystemConfig> configs,
                                      BiFunction<String, Path, MapDbAppFileSystemStorage> storageProvider) {
        this.configs = Objects.requireNonNull(configs);
        this.storageProvider = Objects.requireNonNull(storageProvider);
    }

    @Override
    public List<AppFileSystem> getFileSystems(ComputationManager computationManager) {
        return configs.stream()
                .map(config ->  {
                    MapDbAppFileSystemStorage storage = storageProvider.apply(config.getDriveName(), config.getDbFile());
                    return new MapDbAppFileSystem(config.getDriveName(), config.isRemotelyAccessible(), storage);
                })
                .collect(Collectors.toList());
    }
}
