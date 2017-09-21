/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.afs.local.storage;

import eu.itesla_project.afs.storage.timeseries.TimeSeries;
import eu.itesla_project.afs.storage.timeseries.TimeSeriesData;
import eu.itesla_project.commons.datasource.DataSource;

import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;

/**
 *
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public interface LocalFile extends LocalNode {

    String getPseudoClass();

    String getStringAttribute(String name);

    OptionalInt getIntAttribute(String name);

    OptionalDouble getDoubleAttribute(String name);

    Optional<Boolean> getBooleanAttribute(String name);

    DataSource getDataSourceAttribute(String name);

    List<TimeSeries> getTimeSeries();

    List<TimeSeriesData> getTimeSeriesData(List<TimeSeries> timeSeries, int version);
}
