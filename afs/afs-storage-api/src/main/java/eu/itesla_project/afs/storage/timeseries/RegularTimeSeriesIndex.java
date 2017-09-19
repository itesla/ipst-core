/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.afs.storage.timeseries;

import com.fasterxml.jackson.core.JsonGenerator;
import org.threeten.extra.Interval;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public class RegularTimeSeriesIndex implements TimeSeriesIndex {

    private static final long serialVersionUID = 7997935378544325662L;

    private final Interval interval;

    private final Duration spacing;

    private final int firstVersion;

    private final int versionCount;

    public RegularTimeSeriesIndex(Interval interval, Duration spacing, int firstVersion, int versionCount) {
        this.interval = Objects.requireNonNull(interval);
        this.spacing = Objects.requireNonNull(spacing);
        if (spacing.compareTo(interval.toDuration()) > 0) {
            throw new IllegalArgumentException("Spacing " + spacing + " is longer than interval " + interval);
        }
        this.firstVersion = firstVersion;
        this.versionCount = versionCount;
    }

    public Interval getInterval() {
        return interval;
    }

    public Duration getSpacing() {
        return spacing;
    }

    @Override
    public int getFirstVersion() {
        return firstVersion;
    }

    @Override
    public int getVersionCount() {
        return versionCount;
    }

    @Override
    public int getPointCount() {
        return Math.round(((float) interval.toDuration().toMillis()) / spacing.toMillis()) + 1;
    }

    @Override
    public Instant getInstantAt(int point) {
        return interval.getStart().plus(spacing.multipliedBy(point));
    }

    @Override
    public int hashCode() {
        return Objects.hash(interval, spacing, firstVersion, versionCount);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TimeSeriesIndex) {
            RegularTimeSeriesIndex otherIndex = (RegularTimeSeriesIndex) obj;
            return interval.equals(otherIndex.interval) &&
                    spacing.equals(otherIndex.spacing) &&
                    firstVersion == otherIndex.firstVersion &&
                    versionCount == otherIndex.versionCount;
        }
        return false;
    }

    @Override
    public void writeJson(JsonGenerator generator) throws IOException {
        generator.writeStartObject();
        generator.writeNumberField("start", interval.getStart().toEpochMilli());
        generator.writeNumberField("end", interval.getEnd().toEpochMilli());
        generator.writeNumberField("duration", spacing.toMillis());
        generator.writeNumberField("firstVersion", firstVersion);
        generator.writeNumberField("versionCount", versionCount);
        generator.writeEndObject();
    }

    @Override
    public String toString() {
        return "index(interval=" + interval + ", spacing=" + spacing + ", firstVersion=" + firstVersion +
                ", versionCount=" + versionCount + ")";
    }
}
