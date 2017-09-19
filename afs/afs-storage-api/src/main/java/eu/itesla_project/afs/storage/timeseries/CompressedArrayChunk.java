/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.afs.storage.timeseries;

import com.fasterxml.jackson.core.JsonGenerator;

import java.io.IOException;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public class CompressedArrayChunk implements ArrayChunk {

    private final int offset;

    private final int length;

    private final double[] compressedValues;

    private final int[] compressedLengths;

    public CompressedArrayChunk(int offset, int length, double[] compressedValues, int[] compressedLengths) {
        this.offset = offset;
        this.length = length;
        this.compressedValues = compressedValues;
        this.compressedLengths = compressedLengths;
    }

    public double[] getCompressedValues() {
        return compressedValues;
    }

    public int[] getCompressedLengths() {
        return compressedLengths;
    }

    @Override
    public int getOffset() {
        return offset;
    }

    @Override
    public int getLength() {
        return length;
    }

    @Override
    public boolean isCompressed() {
        return true;
    }

    @Override
    public int getEstimatedSize() {
        return Double.BYTES * compressedValues.length + Integer.BYTES * compressedLengths.length;
    }

    @Override
    public double getCompressionFactor() {
        return ((double) getEstimatedSize()) / (Double.BYTES * length);
    }

    @Override
    public UncompressedArrayChunk toUncompressed() {
        double[] values = new double[length];
        int k = 0;
        for (int i = 0; i < compressedValues.length; i++) {
            double value = compressedValues[i];
            for (int j = 0; j < compressedLengths[i]; j++) {
                values[k++] = value;
            }
        }
        return new UncompressedArrayChunk(offset, values);
    }

    @Override
    public Stream<Point> stream(TimeSeriesIndex index) {
        return IntStream.range(0, compressedValues.length).mapToObj(i -> new Point(0, index.getInstantAt(i), compressedValues[i]));
    }

    @Override
    public List<ArrayChunk> split(int chunk) {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public void writeJson(JsonGenerator generator) throws IOException {
        generator.writeStartObject();
        generator.writeNumberField("offset", offset);
        generator.writeNumberField("length", length);
        generator.writeFieldName("compressed_values");
        generator.writeArray(compressedValues, 0, compressedValues.length);
        generator.writeFieldName("compressed_lengths");
        generator.writeArray(compressedLengths, 0, compressedLengths.length);
        generator.writeEndObject();
    }
}
