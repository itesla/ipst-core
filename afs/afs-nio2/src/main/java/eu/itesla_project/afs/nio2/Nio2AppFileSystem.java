/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.afs.nio2;

import eu.itesla_project.afs.AppFileSystem;
import eu.itesla_project.afs.Folder;
import eu.itesla_project.afs.ProjectFile;
import eu.itesla_project.afs.ProjectFileBuilder;
import eu.itesla_project.commons.util.ServiceLoaderCache;
import eu.itesla_project.computation.ComputationManager;
import eu.itesla_project.computation.local.LocalComputationManager;
import eu.itesla_project.iidm.import_.ImportersLoader;
import eu.itesla_project.iidm.import_.ImportersServiceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.FileSystem;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public class Nio2AppFileSystem extends AppFileSystem {

    private static final Logger LOGGER = LoggerFactory.getLogger(Nio2AppFileSystem.class);

    private final FileSystem fileSystem;

    private final String name;

    private final Folder root;

    private final List<Nio2FileScanner> fileScanners;

    private final List<Nio2ProjectFileScanner> projectFileScanners;

    private final Map<Class<? extends ProjectFileBuilder<? extends ProjectFile>>, Nio2ProjectFileBuilderFactory> projectFileBuilderFactories;

    private final ImportersLoader importersLoader;

    private final ComputationManager computationManager;

    public Nio2AppFileSystem(FileSystem fileSystem, String name) {
        this(fileSystem, name, null);
    }

    public Nio2AppFileSystem(FileSystem fileSystem, String name, String rootDir) {
        this(fileSystem, name, rootDir, new ServiceLoaderCache<>(Nio2FileScanner.class).getServices(),
                new ServiceLoaderCache<>(Nio2ProjectFileScanner.class).getServices(),
                new ServiceLoaderCache<>(Nio2ProjectFileBuilderFactory.class).getServices(),
                new ImportersServiceLoader(), LocalComputationManager.getDefault());
    }

    public Nio2AppFileSystem(FileSystem fileSystem, String name, String rootDir,
                             List<Nio2FileScanner> fileScanners, List<Nio2ProjectFileScanner> projectFileScanners,
                             List<Nio2ProjectFileBuilderFactory> projectFileBuilderFactories,
                             ImportersLoader importersLoader, ComputationManager computationManager) {
        this.fileSystem = Objects.requireNonNull(fileSystem);
        this.name = Objects.requireNonNull(name);
        root = new Nio2Folder(rootDir != null ? fileSystem.getPath(rootDir) : fileSystem.getRootDirectories().iterator().next(), null, this);
        this.fileScanners = Objects.requireNonNull(fileScanners);
        this.projectFileScanners = Objects.requireNonNull(projectFileScanners);
        this.projectFileBuilderFactories = Objects.requireNonNull(projectFileBuilderFactories).stream()
                .collect(Collectors.toMap(f -> f.getProjectFileBuilderClass(), Function.identity()));
        this.importersLoader = Objects.requireNonNull(importersLoader);
        this.computationManager = Objects.requireNonNull(computationManager);
    }

    public FileSystem getFileSystem() {
        return fileSystem;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Folder getRootFolder() {
        return root;
    }

    List<Nio2FileScanner> getFileScanners() {
        return fileScanners;
    }

    List<Nio2ProjectFileScanner> getProjectFileScanners() {
        return projectFileScanners;
    }

    @Override
    public Collection<Class<? extends ProjectFile>> getProjectFileTypes() {
        return projectFileScanners.stream().map(Nio2ProjectFileScanner::getType).collect(Collectors.toList());
    }

    Nio2ProjectFileBuilderFactory getProjectFileBuilderFactory(Class<? extends ProjectFileBuilder> projectFileBuilderClass) {
        Objects.requireNonNull(projectFileBuilderClass);
        Nio2ProjectFileBuilderFactory factory = projectFileBuilderFactories.get(projectFileBuilderClass);
        if (factory == null) {
            throw new RuntimeException("Builder factory implementation not found for '" + projectFileBuilderClass.getName() + "'");
        }
        return factory;
    }

    public ImportersLoader getImportersLoader() {
        return importersLoader;
    }

    public ComputationManager getComputationManager() {
        return computationManager;
    }

    @Override
    public String toString() {
        return name;
    }
}