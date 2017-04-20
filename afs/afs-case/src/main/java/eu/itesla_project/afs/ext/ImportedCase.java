/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.afs.ext;

import eu.itesla_project.afs.FileIcon;
import eu.itesla_project.afs.NodePath;
import eu.itesla_project.afs.ProjectNodePathToString;
import eu.itesla_project.iidm.datasource.ReadOnlyDataSource;
import eu.itesla_project.iidm.import_.Importer;
import eu.itesla_project.iidm.network.Network;

import java.util.Properties;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public abstract class ImportedCase extends ProjectCase {

    @Override
    public FileIcon getIcon() {
        return CaseIconCache.INSTANCE.get(
                getProject().getFileSystem().getImportersLoader(),
                getProject().getFileSystem().getComputationManager(),
                getImporter().getFormat());
    }

    public abstract ReadOnlyDataSource getDataSource();

    public abstract Properties getParameters();

    public abstract Importer getImporter();

    @Override
    public Network loadNetwork() {
        Importer importer = getImporter();
        ReadOnlyDataSource dataSource = getDataSource();
        Properties parameters = getParameters();
        return importer.import_(dataSource, parameters);
    }
}
