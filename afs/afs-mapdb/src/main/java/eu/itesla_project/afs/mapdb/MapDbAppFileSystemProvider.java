/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.afs.mapdb;

import com.google.auto.service.AutoService;
import eu.itesla_project.afs.core.AppFileSystem;
import eu.itesla_project.afs.core.AppFileSystemProvider;
import eu.itesla_project.computation.ComputationManager;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
@AutoService(AppFileSystemProvider.class)
public class MapDbAppFileSystemProvider implements AppFileSystemProvider {

    private final List<MapDbAppFileSystemConfig> configs;

    public MapDbAppFileSystemProvider() {
        this(MapDbAppFileSystemConfig.load());
    }

    public MapDbAppFileSystemProvider(List<MapDbAppFileSystemConfig> configs) {
        this.configs = Objects.requireNonNull(configs);
    }

    @Override
    public List<AppFileSystem> getFileSystems(ComputationManager computationManager) {
        return configs.stream()
                .map(config -> new MapDbAppFileSystem(config))
                .collect(Collectors.toList());
    }
}
