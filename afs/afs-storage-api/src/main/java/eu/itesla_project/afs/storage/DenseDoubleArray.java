/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.afs.storage;

import com.google.common.collect.Range;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.DoubleStream;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public class DenseDoubleArray implements DoubleArray, Serializable {

    private static final long serialVersionUID = -6040210794391604102L;

    private final double[] values;

    private final int offset;

    public DenseDoubleArray(double[] values, int offset) {
        this.values = Objects.requireNonNull(values);
        this.offset = offset;
    }

    public static Optional<CompressedDoubleArray> tryToCompress(float minCompressRatio) {
        return Optional.empty();
    }

    public double[] getValues() {
        return values;
    }

    public int getOffset() {
        return offset;
    }

    @Override
    public Range<Integer> getRange() {
        return Range.closed(offset, offset + values.length - 1);
    }

    @Override
    public int getLength() {
        return values.length;
    }

    @Override
    public DenseDoubleArray toDense() {
        return this;
    }

    @Override
    public DoubleStream stream() {
        return DoubleStream.of(values);
    }
}
