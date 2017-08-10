/**
 * Copyright (c) 2016, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.iidm.network.impl;

import eu.itesla_project.iidm.network.ConnectableType;
import eu.itesla_project.iidm.network.HvdcConverterStation;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 * @author Mathieu Bague <mathieu.bague at rte-france.com>
 */
abstract class AbstractHvdcConverterStation<T extends HvdcConverterStation<T>> extends AbstractConnectable<T> implements HvdcConverterStation<T> {

    private float lossFactor = Float.NaN;

    AbstractHvdcConverterStation(String id, String name, float lossFactor) {
        super(id, name);
        this.lossFactor = lossFactor;
    }

    @Override
    public TerminalExt getTerminal() {
        return terminals.get(0);
    }

    @Override
    public ConnectableType getType() {
        return ConnectableType.HVDC_CONVERTER_STATION;
    }

    @Override
    public float getLossFactor() {
        return lossFactor;
    }

    @Override
    public T setLossFactor(float lossFactor) {
        ValidationUtil.checkLossFactor(this, lossFactor);
        float oldValue = this.lossFactor;
        this.lossFactor = lossFactor;
        notifyUpdate("lossFactor", oldValue, lossFactor);
        return (T) this;
    }

}
