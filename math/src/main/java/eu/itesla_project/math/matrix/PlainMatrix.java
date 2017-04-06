/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.math.matrix;

import com.google.common.base.Strings;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public class PlainMatrix extends AbstractMatrix {

    private final int m;

    private final int n;

    private final double[] values;

    public PlainMatrix(int m, int n) {
        this(m, n, new double[m * n]);
    }

    public PlainMatrix(int m, int n, double[] values) {
        if (values.length != m * n) {
            throw new IllegalArgumentException("values size (" + values.length +
                    ") is incorrect (should be " + m * n + ")");
        }
        this.m = m;
        this.n = n;
        this.values = Objects.requireNonNull(values);
    }

    public PlainMatrix(Jama.Matrix matrix) {
        this(matrix.getRowDimension(), matrix.getColumnDimension(), matrix.getColumnPackedCopy());
    }

    public double getValue(int i, int j) {
        return values[j * m + i];
    }

    @Override
    public void setValue(int i, int j, double value) {
        values[j * m + i] = value;
    }

    @Override
    public int getM() {
        return m;
    }

    @Override
    public int getN() {
        return n;
    }

    public double[] getValues() {
        return values;
    }

    Jama.Matrix toJamaMatrix() {
        return new Jama.Matrix(values, m);
    }

    @Override
    public LUDecomposition decomposeLU() {
        return new PlainLUDecomposition(toJamaMatrix().lu());
    }

    @Override
    public Matrix times(Matrix other) {
        return new PlainMatrix(toJamaMatrix().times(other.toPlain().toJamaMatrix()));
    }

    @Override
    public void iterateNonZeroValue(ElementHandler handler) {
        Objects.requireNonNull(handler);
        for (int j = 0; j < getN(); j++) {
            iterateNonZeroValueOfColumn(j, handler);
        }
    }

    @Override
    public void iterateNonZeroValueOfColumn(int j, ElementHandler handler) {
        for (int i = 0; i < getM(); i++) {
            double value = getValue(i, j);
            if (value != 0) {
                handler.onValue(i, j, value);
            }
        }
    }

    @Override
    public PlainMatrix toPlain() {
        return this;
    }

    @Override
    public SparseMatrix toSparse() {
        return (SparseMatrix) to(new SparseMatrixFactory());
    }

    @Override
    public Matrix to(MatrixFactory factory) {
        Objects.requireNonNull(factory);
        if (factory instanceof PlainMatrixFactory) {
            return this;
        }
        return copy(factory);
    }

    @Override
    protected int getEstimatedNonZeroValueCount() {
        return getM() * getN();
    }

    @Override
    public void print() {
        print(System.out);
    }

    @Override
    public void print(PrintStream out) {
        print(out, null, null);
    }

    @Override
    public void print(List<String> rowNames, List<String> columnNames) {
        print(System.out, rowNames, columnNames);
    }

    @Override
    public void print(PrintStream out, List<String> rowNames, List<String> columnNames) {
        int rowNamesWidth = 0;
        if (rowNames != null) {
            for (String rowName : rowNames) {
                rowNamesWidth = Math.max(rowNamesWidth, rowName.length());
            }
        }
        int[] width = new int[getN()];
        for (int i = 0; i < getM(); i++) {
            for (int j = 0; j < getN(); j++) {
                width[j] = Math.max(width[j], Double.toString(getValue(i, j)).length());
                if (columnNames != null) {
                    width[j] = Math.max(width[j], columnNames.get(j).length());
                }
            }
        }

        if (columnNames != null) {
            if (rowNames != null) {
                out.print(Strings.repeat(" ", rowNamesWidth + 1));
            }
            for (int j = 0; j < getN(); j++) {
                out.print(Strings.padStart(columnNames.get(j), width[j] + 1, ' '));
            }
            out.println();
        }
        for (int i = 0; i < getM(); i++) {
            if (rowNames != null) {
                out.print(Strings.padStart(rowNames.get(i), rowNamesWidth + 1, ' '));
            }
            for (int j = 0; j < getN(); j++) {
                out.print(Strings.padStart(Double.toString(getValue(i, j)), width[j] + 1, ' '));
            }
            out.println();
        }
    }

    @Override
    public int hashCode() {
        return m + n + Arrays.hashCode(values);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PlainMatrix) {
            PlainMatrix other = (PlainMatrix) obj;
            return m == other.m && n == other.n && Arrays.equals(values, other.values);
        }
        return false;
    }
}
