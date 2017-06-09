/**
 * Copyright (c) 2016, All partners of the iTesla project (http://www.itesla-project.eu/consortium)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.iidm.network.impl;

import eu.itesla_project.iidm.network.BusAdder;

/**
 *
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
class BusAdderImpl extends IdentifiableAdderImpl<BusAdderImpl> implements BusAdder {

    private final BusBreakerVoltageLevel voltageLevel;

    BusAdderImpl(BusBreakerVoltageLevel voltageLevel) {
        this.voltageLevel = voltageLevel;
    }

    @Override
    protected NetworkImpl getNetwork() {
        return voltageLevel.getNetwork();
    }

    @Override
    protected String getTypeDescription() {
        return "Bus";
    }

    @Override
    public ConfiguredBus add() {
        String id = checkAndGetUniqueId();
        ConfiguredBusImpl bus = new ConfiguredBusImpl(id, voltageLevel);
        voltageLevel.addBus(bus);
        getNetwork().getListeners().notifyCreation(bus);
        return bus;
    }

}
