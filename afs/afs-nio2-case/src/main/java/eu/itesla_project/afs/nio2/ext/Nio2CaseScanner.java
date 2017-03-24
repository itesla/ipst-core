/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.afs.nio2.ext;

import com.google.auto.service.AutoService;
import eu.itesla_project.afs.Node;
import eu.itesla_project.afs.nio2.Nio2AppFileSystem;
import eu.itesla_project.afs.nio2.Nio2FileScanner;
import eu.itesla_project.afs.nio2.Nio2Folder;
import eu.itesla_project.computation.ComputationManager;
import eu.itesla_project.iidm.datasource.ReadOnlyDataSource;
import eu.itesla_project.iidm.import_.ImportConfig;
import eu.itesla_project.iidm.import_.Importers;
import eu.itesla_project.iidm.import_.ImportersLoader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
@AutoService(Nio2FileScanner.class)
public class Nio2CaseScanner implements Nio2FileScanner {

    @Override
    public Collection<Node> scan(Nio2Folder parent, Path path) {
        if (Files.isRegularFile(path)) {
            ReadOnlyDataSource dataSource = Importers.createReadOnly(path);
            Nio2AppFileSystem fileSystem = parent.getFileSystem();
            ImportersLoader loader = fileSystem.getImportersLoader();
            ComputationManager computationManager = fileSystem.getComputationManager();
            return Importers.list(loader, computationManager, new ImportConfig()).stream()
                    .filter(importer -> importer.exists(dataSource))
                    .map(importer -> new Nio2Case(path, parent, fileSystem, dataSource, importer))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}
