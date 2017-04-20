/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.afs.nio2.ext;

import eu.itesla_project.afs.FileIcon;
import eu.itesla_project.afs.ext.Case;
import eu.itesla_project.afs.ext.CaseIconCache;
import eu.itesla_project.afs.nio2.Nio2AppFileSystem;
import eu.itesla_project.afs.nio2.Nio2Folder;
import eu.itesla_project.afs.nio2.Nio2Node;
import eu.itesla_project.iidm.datasource.ReadOnlyDataSource;
import eu.itesla_project.iidm.import_.Importer;

import java.nio.file.Path;
import java.util.Objects;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public class Nio2Case extends Nio2Node implements Case {

    private final ReadOnlyDataSource dataSource;

    private final Importer importer;

    Nio2Case(Path file, Nio2Folder parent, Nio2AppFileSystem fileSystem, ReadOnlyDataSource dataSource, Importer importer) {
        super(file, parent, fileSystem);
        this.dataSource = Objects.requireNonNull(dataSource);
        this.importer = Objects.requireNonNull(importer);
    }

    @Override
    public boolean isFolder() {
        return false;
    }

    @Override
    public FileIcon getIcon() {
        return CaseIconCache.INSTANCE.get(fileSystem.getImportersLoader(), fileSystem.getComputationManager(), importer.getFormat());
    }

    @Override
    public String getDescription() {
        return importer.getComment();
    }

    @Override
    public String getName() {
        return dataSource.getBaseName();
    }

    @Override
    public Importer getImporter() {
        return importer;
    }

    @Override
    public ReadOnlyDataSource getDataSource() {
        return dataSource;
    }
}
