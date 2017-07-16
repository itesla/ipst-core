/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.afs.case_;

import eu.itesla_project.afs.core.FileIcon;
import eu.itesla_project.computation.ComputationManager;
import eu.itesla_project.iidm.import_.ImportConfig;
import eu.itesla_project.iidm.import_.Importer;
import eu.itesla_project.iidm.import_.Importers;
import eu.itesla_project.iidm.import_.ImportersLoader;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public enum CaseIconCache {
    INSTANCE;

    private Map<String, FileIcon> cache;

    public synchronized FileIcon get(ImportersLoader loader, ComputationManager computationManager, String format) {
        if (cache == null) {
            cache = new HashMap<>();
            for (Importer importer : Importers.list(loader, computationManager, new ImportConfig())) {
                InputStream is = importer.get16x16Icon();
                if (is != null) {
                    cache.put(importer.getFormat(), new FileIcon(importer.getFormat(), is));
                }
            }
        }
        return cache.get(format);
    }
}
