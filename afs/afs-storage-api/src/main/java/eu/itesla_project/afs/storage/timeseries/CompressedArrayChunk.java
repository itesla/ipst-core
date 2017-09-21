/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.afs.storage.timeseries;

import com.fasterxml.jackson.core.JsonGenerator;

import java.io.IOException;
import java.util.Iterator;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public class CompressedArrayChunk implements ArrayChunk {

    private final int offset;

    private final int uncompressedLength;

    private final double[] stepValues;

    private final int[] stepLengths;

    public CompressedArrayChunk(int offset, int uncompressedLength, double[] stepValues, int[] stepLengths) {
        if (offset < 0) {
            throw new IllegalArgumentException("Bad offset value " + offset);
        }
        if (uncompressedLength < 1) {
            throw new IllegalArgumentException("Bad uncompressed length value " + offset);
        }
        if (stepValues.length != stepLengths.length) {
            throw new IllegalArgumentException("Inconsistent step arrays size: "
                    + stepValues.length + " != " + stepLengths.length);
        }
        if (stepValues.length < 1) {
            throw new IllegalArgumentException("Bad step arrays length " + stepValues.length);
        }
        if (stepValues.length > uncompressedLength) {
            throw new IllegalArgumentException("Step arrays length is greater than uncompressed length");
        }
        this.offset = offset;
        this.uncompressedLength = uncompressedLength;
        this.stepValues = Objects.requireNonNull(stepValues);
        this.stepLengths = Objects.requireNonNull(stepLengths);
    }

    public double[] getStepValues() {
        return stepValues;
    }

    public int[] getStepLengths() {
        return stepLengths;
    }

    @Override
    public int getOffset() {
        return offset;
    }

    @Override
    public int getLength() {
        return uncompressedLength;
    }

    @Override
    public boolean isCompressed() {
        return true;
    }

    @Override
    public int getEstimatedSize() {
        return Double.BYTES * stepValues.length + Integer.BYTES * stepLengths.length;
    }

    @Override
    public double getCompressionFactor() {
        return ((double) getEstimatedSize()) / (Double.BYTES * uncompressedLength);
    }

    @Override
    public void fillArray(double[] array) {
        int k = 0;
        for (int i = 0; i < stepValues.length; i++) {
            double value = stepValues[i];
            for (int j = 0; j < stepLengths[i]; j++) {
                array[offset + k++] = value;
            }
        }
    }

    @Override
    public Stream<Point> stream(TimeSeriesIndex index) {
        Objects.requireNonNull(index);
        Iterator<Point> iterator = new Iterator<Point>() {

            private int i = offset;
            private int step = 0;

            @Override
            public boolean hasNext() {
                return i < uncompressedLength;
            }

            @Override
            public Point next() {
                Point point = new Point(i, index.getInstantAt(i), stepValues[step]);
                i += stepLengths[step];
                step++;
                return point;
            }
        };
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(
                iterator,
                Spliterator.ORDERED | Spliterator.IMMUTABLE), false);
    }

    @Override
    public void writeJson(JsonGenerator generator) throws IOException {
        generator.writeStartObject();
        generator.writeNumberField("offset", offset);
        generator.writeNumberField("uncompressedLength", uncompressedLength);
        generator.writeFieldName("stepValues");
        generator.writeArray(stepValues, 0, stepValues.length);
        generator.writeFieldName("stepLengths");
        generator.writeArray(stepLengths, 0, stepLengths.length);
        generator.writeEndObject();
    }
}
