/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.afs.case_;

import eu.itesla_project.afs.core.*;
import eu.itesla_project.afs.storage.AppFileSystemStorage;
import eu.itesla_project.afs.storage.NodeId;
import eu.itesla_project.iidm.datasource.ReadOnlyDataSource;
import eu.itesla_project.iidm.import_.Importer;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public class Case extends File {

    public static final String PSEUDO_CLASS = "case";

    public Case(NodeId id, AppFileSystemStorage storage, AppFileSystem fileSystem) {
        super(id, storage, fileSystem);
    }

    private String getFormat() {
        return storage.getStringAttribute(id, "format");
    }

    @Override
    public FileIcon getIcon() {
        return CaseIconCache.INSTANCE.get(fileSystem.getData().getImportersLoader(), fileSystem.getData().getComputationManager(), getFormat());
    }

    public ReadOnlyDataSource getDataSource() {
        return storage.getDataSourceAttribute(id, "dataSource");
    }

    public Importer getImporter() {
        String format = getFormat();
        return fileSystem.getData().getImportersLoader().loadImporters()
                .stream()
                .filter(importer -> importer.getFormat().equals(format))
                .findFirst()
                .orElseThrow(() -> new AfsException("Import not found for format " + format));
    }

}
