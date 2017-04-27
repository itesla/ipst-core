/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.action.util;

import eu.itesla_project.iidm.network.Generator;
import eu.itesla_project.iidm.network.Network;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian@rte-france.com>
 */
abstract class AbstractScalable extends Scalable {

    protected final List<Scalable> scalables;

    protected AbstractScalable(List<Scalable> scalables) {
        this.scalables = Objects.requireNonNull(scalables);
    }

    public float initialValue(Network n, Map<String, String> name2id) {
        float value = 0;
        for (Scalable scalable : scalables) {
            value += scalable.initialValue(n, name2id);
        }
        return value;
    }

    protected void reset(Network n, Map<String, String> name2id) {
        scalables.forEach(scalable -> scalable.reset(n, name2id));
    }

    public float maximumValue(Network n, Map<String, String> name2id) {
        float value = 0;
        for (Scalable scalable : scalables) {
            value += scalable.maximumValue(n, name2id);
        }
        return value;
    }

    public void listGenerators(Network n, Map<String, String> name2id, List<Generator> generators, List<String> notFoundGenerators) {
        for (Scalable scalable : scalables) {
            scalable.listGenerators(n, name2id, generators, notFoundGenerators);
        }
    }

}
