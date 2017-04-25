/**
 * Copyright (c) 2016, All partners of the iTesla project (http://www.itesla-project.eu/consortium)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.iidm.network.impl;

import eu.itesla_project.iidm.network.Network;
import eu.itesla_project.iidm.network.NetworkFactoryService;

/**
 *
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public class NetworkFactoryServiceImpl implements NetworkFactoryService {

    @Override
    public Network createNetwork(String id, String sourceFormat) {
        return new NetworkImpl(id, id, sourceFormat);
    }

}
