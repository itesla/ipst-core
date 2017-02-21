/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.math.matrix;

import java.util.UUID;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public class SparseLUDecomposition implements LUDecomposition {

    private final String id;

    public SparseLUDecomposition(SparseMatrix matrix) {
        if (matrix.getM() != matrix.getN()) {
            throw new IllegalArgumentException("matrix is not square");
        }
        this.id = UUID.randomUUID().toString();
        init(id, matrix.getColumnStart(), matrix.getRowIndices(), matrix.getValues());
    }

    private native void init(String id, int[] ap, int [] ai, double[] ax);

    private native void release(String id);

    private native void solve(String id, double[] b);

    @Override
    public void solve(double[] b) {
        solve(id, b);
    }

    @Override
    public void close() {
        release(id);
    }
}