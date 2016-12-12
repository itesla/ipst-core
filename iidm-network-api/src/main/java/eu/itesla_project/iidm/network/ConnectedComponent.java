/**
 * Copyright (c) 2016, All partners of the iTesla project (http://www.itesla-project.eu/consortium)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.iidm.network;

import java.util.stream.Stream;

/**
 * A set of connected bus in the network.
 *
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public interface ConnectedComponent {

    int MAIN_CC_NUM = 0;

    /**
     * Get the number of the connected component.
     * <p>
     * The biggest one has the number zero and the smallest has the highest number.
     */
    int getNum();

    /**
     * Get the number of bus in the connected component.
     */
    int getSize();

    /**
     * Get buses in the connected component.
     */
    Iterable<Bus> getBuses();

    /**
     * Get buses in the connected component.
     */
    Stream<Bus> getBusStream();

}
