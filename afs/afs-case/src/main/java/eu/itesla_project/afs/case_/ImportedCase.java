/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.afs.case_;

import eu.itesla_project.afs.core.AfsException;
import eu.itesla_project.afs.core.AppFileSystem;
import eu.itesla_project.afs.core.FileIcon;
import eu.itesla_project.afs.storage.AppFileSystemStorage;
import eu.itesla_project.afs.storage.NodeId;
import eu.itesla_project.iidm.datasource.ReadOnlyDataSource;
import eu.itesla_project.iidm.import_.Importer;
import eu.itesla_project.iidm.network.Network;

import java.io.IOException;
import java.io.StringReader;
import java.io.UncheckedIOException;
import java.util.Properties;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public class ImportedCase extends ProjectCase {

    public static final String PSEUDO_CLASS = "imported-case";

    public ImportedCase(NodeId id, AppFileSystemStorage storage, NodeId projectId, AppFileSystem fileSystem) {
        super(id, storage, projectId, fileSystem);
    }

    @Override
    public FileIcon getIcon() {
        return CaseIconCache.INSTANCE.get(
                getProject().getFileSystem().getData().getImportersLoader(),
                getProject().getFileSystem().getData().getComputationManager(),
                getImporter().getFormat());
    }

    public ReadOnlyDataSource getDataSource() {
        return storage.getDataSourceAttribute(id, "dataSource");
    }

    public Properties getParameters() {
        Properties parameters = new Properties();
        try (StringReader reader = new StringReader(storage.getStringAttribute(id, "parameters"))) {
            parameters.load(reader);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return parameters;
    }

    public Importer getImporter() {
        String format = storage.getStringAttribute(id, "format");
        return getProject().getFileSystem().getData().getImportersLoader().loadImporters()
                .stream()
                .filter(importer -> importer.getFormat().equals(format))
                .findFirst()
                .orElseThrow(() -> new AfsException("Import not found for format " + format));
    }

    @Override
    public Network loadNetwork() {
        Importer importer = getImporter();
        ReadOnlyDataSource dataSource = getDataSource();
        Properties parameters = getParameters();
        return importer.import_(dataSource, parameters);
    }
}
