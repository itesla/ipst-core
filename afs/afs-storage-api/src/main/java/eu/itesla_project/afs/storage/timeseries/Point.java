/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.afs.storage.timeseries;

import java.time.Instant;
import java.util.Objects;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public class Point {

    private final int index;

    private final Instant instant;

    private final double value;

    public Point(int index, Instant instant, double value) {
        this.index = index;
        this.instant = Objects.requireNonNull(instant);
        this.value = value;
    }

    public int getIndex() {
        return index;
    }

    public Instant getInstant() {
        return instant;
    }

    public double getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(instant, value);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Point) {
            Point other = (Point) obj;
            return instant == other.instant && instant.equals(other.instant) && value == value;
        }
        return false;
    }
}
