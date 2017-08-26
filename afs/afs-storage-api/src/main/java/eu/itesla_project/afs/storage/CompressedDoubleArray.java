/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.afs.storage;

import com.google.common.collect.Range;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.DoubleStream;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public class CompressedDoubleArray implements DoubleArray, Serializable {

    private static final long serialVersionUID = 6037532782370001585L;

    private final double[] values;

    private final int[] lengths;

    private final int offset;

    private final int length;

    public CompressedDoubleArray(double[] values, int[] lengths, int offset) {
        this.values = Objects.requireNonNull(values);
        this.lengths = lengths;
        this.offset = offset;
        length = Arrays.stream(lengths).sum();
    }

    @Override
    public Range<Integer> getRange() {
        return Range.closed(offset, offset + length - 1);
    }

    @Override
    public int getLength() {
        return length;
    }

    @Override
    public DenseDoubleArray toDense() {
        return null;
    }

    @Override
    public DoubleStream stream() {
        return null;
    }
}
