/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.afs.storage;

import com.google.common.collect.Range;

import java.util.stream.DoubleStream;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public interface DoubleArray {

    Range<Integer> getRange();

    int getLength();

    DenseDoubleArray toDense();

    DoubleStream stream();
}
