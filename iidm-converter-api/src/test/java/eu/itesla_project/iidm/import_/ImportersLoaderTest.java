/**
 * Copyright (c) 2016, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.iidm.import_;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public class ImportersLoaderTest implements ImportersLoader {

    private final List<Importer> importers;

    public ImportersLoaderTest(Importer... importers) {
        this(Arrays.asList(importers));
    }

    public ImportersLoaderTest(List<Importer> importers) {
        this.importers = Objects.requireNonNull(importers);
    }

    @Override
    public List<Importer> loadImporters() {
        return importers;
    }

    @Override
    public List<ImportPostProcessor> loadPostProcessors() {
        return Collections.emptyList();
    }
}
