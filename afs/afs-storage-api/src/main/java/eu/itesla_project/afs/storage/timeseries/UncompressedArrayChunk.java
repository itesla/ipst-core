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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
    public UncompressedArrayChunk toUncompressed() {
        return this;
    }

    public ArrayChunk tryToCompress() {
        TDoubleArrayList compressedValues = new TDoubleArrayList();
        TIntArrayList compressedLengths = new TIntArrayList();
        for (double value : values) {
            if (compressedValues.isEmpty()) {
                compressedValues.add(value);
                compressedLengths.add(1);
            } else {
                int previousIndex = compressedValues.size() - 1;
                double previousValue = compressedValues.getQuick(previousIndex);
                if (previousValue == value) {
                    compressedLengths.set(previousIndex, compressedLengths.getQuick(previousIndex) + 1);
                } else {
                    compressedValues.add(value);
                    compressedLengths.add(1);
                }
                // compression is not really interesting...
                if (compressedValues.size() > values.length * 0.40) {
                    return this;
                }
            }
        }
        return new CompressedArrayChunk(offset, values.length, compressedValues.toArray(), compressedLengths.toArray());
    }

    @Override
    public Stream<Point> stream(TimeSeriesIndex index) {
        return IntStream.range(0, values.length).mapToObj(i -> new Point(i, index.getInstantAt(i), values[i]));
    }

    @Override
    public List<ArrayChunk> split(int chunk) {
        List<ArrayChunk> chunks = new ArrayList<>();
        int chunkLength = (values.length / chunk) + 1;
        for (int i = 0; i < chunk; i++) {
            int chunkOffset = i * chunkLength;
            double[] chunkValues = Arrays.copyOfRange(values, chunkOffset, Math.min((i + 1) * chunkLength, values.length));
            chunks.add(new UncompressedArrayChunk(chunkOffset, chunkValues));
        }
        return chunks;
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
