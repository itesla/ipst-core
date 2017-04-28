/**
 * Copyright (c) 2016, All partners of the iTesla project (http://www.itesla-project.eu/consortium)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.iidm.export;

import eu.itesla_project.iidm.datasource.DataSource;
import eu.itesla_project.iidm.network.Network;
import java.util.Properties;

/**
 * This is the base class for all IIDM exporters.
 *
 * <p><code>Exporter</code> lookup is based on the <code>ServiceLoader</code>
 * architecture so do not forget to create a
 * <code>META-INF/services/eu.itesla_project.iidm.export.Exporter</code> file
 * with the fully qualified name of your <code>Exporter</code> implementation.
 *
 * @see java.util.ServiceLoader
 * @see Exporters
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public interface Exporter {

    /**
     * Get a unique identifier of the format.
     */
    String getFormat();

    /**
     * Get some information about this exporter.
     */
    String getComment();

    /**
     * Export a model.
     *
     * @param network the model
     * @param parameters some properties to configure the export
     * @param dataSource data source
     */
    void export(Network network, Properties parameters, DataSource dataSource);

}
