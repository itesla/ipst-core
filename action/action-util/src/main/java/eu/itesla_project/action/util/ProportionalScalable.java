/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.action.util;

import eu.itesla_project.iidm.network.Network;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian@rte-france.com>
 */
class ProportionalScalable extends AbstractScalable {

    private static void checkPercentage(List<Float> percentage, List<Scalable> scalables) {
        if (scalables.size() != percentage.size()) {
            throw new IllegalArgumentException("percentage and scalable list must have the same size");
        }
        double sum = percentage.stream().mapToDouble(Double::valueOf).sum();
        if (Math.abs(100 - sum) > 0.01) {
            throw new IllegalArgumentException("Sum of percentages must be equals to 100 (" + sum + ")");
        }
    }

    private final List<Float> percentage;

    ProportionalScalable(List<Float> percentage, List<Scalable> scalables) {
        super(scalables);
        checkPercentage(percentage, scalables);
        this.percentage = Objects.requireNonNull(percentage);
    }

    @Override
    public float scale(Network n, Map<String, String> name2id, float asked) {
        float done = 0;
        for (int i = 0; i < scalables.size(); i++) {
            Scalable s = scalables.get(i);
            float p = percentage.get(i);
            done += s.scale(n, name2id, p / 100 * asked);
        }
        return done;
    }

}

