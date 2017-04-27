/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.action.util;

import eu.itesla_project.iidm.network.Network;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian@rte-france.com>
 */
class StackScalable extends AbstractScalable {

    StackScalable(Scalable... scalables) {
        this(Arrays.asList(scalables));
    }

    StackScalable(List<Scalable> scalables) {
        super(scalables);
    }

    @Override
    public float scale(Network n, Map<String, String> name2id, float asked) {
        float done = 0;
        float remaining = asked;
        for (Scalable scalable : scalables) {
            if (remaining > 0) {
                float v = scalable.scale(n, name2id, remaining);
                done += v;
                remaining -= v;
            }
        }
        return done;
    }

}
