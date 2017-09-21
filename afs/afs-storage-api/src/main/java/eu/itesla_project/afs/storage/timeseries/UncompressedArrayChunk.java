/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.afs.storage.timeseries;

import com.fasterxml.jackson.core.JsonGenerator;
import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.list.array.TIntArrayList;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public class UncompressedArrayChunk implements ArrayChunk {

    private final int offset;

    private final double[] values;

    public UncompressedArrayChunk(int offset, double[] values) {
        this.offset = offset;
        this.values = Objects.requireNonNull(values);
    }

    public double[] getValues() {
        return values;
    }

    @Override
    public int getOffset() {
        return offset;
    }

    @Override
    public int getLength() {
        return values.length;
    }

    @Override
    public boolean isCompressed() {
        return false;
    }

    @Override
    public int getEstimatedSize() {
        return Double.BYTES * values.length;
    }

    @Override
    public double getCompressionFactor() {
        return 1d;
    }

    @Override
    public void fillArray(double[] array) {
        Arrays.copyOfRange(values, offset, values.length);
    }

    public ArrayChunk tryToCompress() {
        TDoubleArrayList stepValues = new TDoubleArrayList();
        TIntArrayList stepLengths = new TIntArrayList();
        for (double value : values) {
            if (stepValues.isEmpty()) {
                stepValues.add(value);
                stepLengths.add(1);
            } else {
                int previousIndex = stepValues.size() - 1;
                double previousValue = stepValues.getQuick(previousIndex);
                if (previousValue == value) {
                    stepLengths.set(previousIndex, stepLengths.getQuick(previousIndex) + 1);
                } else {
                    stepValues.add(value);
                    stepLengths.add(1);
                }
                // compression is not really interesting...
                if (stepValues.size() > values.length * 0.40) {
                    return this;
                }
            }
        }
        return new CompressedArrayChunk(offset, values.length, stepValues.toArray(), stepLengths.toArray());
    }

    @Override
    public Stream<Point> stream(TimeSeriesIndex index) {
        Objects.requireNonNull(index);
        return IntStream.range(0, values.length).mapToObj(i -> new Point(i, index.getInstantAt(i), values[i]));
    }

    @Override
    public void writeJson(JsonGenerator generator) throws IOException {
        generator.writeStartObject();
        generator.writeNumberField("offset", offset);
        generator.writeFieldName("values");
        generator.writeArray(values, 0, values.length);
        generator.writeEndObject();
    }
}
