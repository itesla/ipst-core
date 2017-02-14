/**
 * Copyright (c) 2016, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.iidm.network;

/**
 * VSC converter station.
 *
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 * @author Mathieu Bague <mathieu.bague at rte-france.com>
 */
public interface VscConverterStation extends HvdcConverterStation<VscConverterStation>, ReactiveLimitsHolder {

    /**
     * Check if voltage regulator is on.
     * @return true if voltage regulator is on, false otherwise
     */
    boolean isVoltageRegulatorOn();

    /**
     * Set voltage regulator status.
     * @param voltageRegulatorOn the new voltage regulator status
     * @return the converter itself to allow method chaining
     */
    HvdcConverterStation setVoltageRegulatorOn(boolean voltageRegulatorOn);

    /**
     * Get the voltage setpoint target (Kv).
     * @return the voltage setpoint target
     */
    float getVoltageSetpoint();

    /**
     * Set the voltage setpoint target (Kv).
     * @param voltageSetpoint the voltage setpoint target
     * @return the converter itself to allow method chaining
     */
    HvdcConverterStation setVoltageSetpoint(float voltageSetpoint);

    /**
     * Get the reactive power setpoint target (MVar).
     * @return the reactive power setpoint target
     */
    float getReactivePowerSetpoint();

    /**
     * Set the reactive power setpoint target (MVar).
     * @param reactivePowerSetpoint the reactive power setpoint target
     * @return the converter itself to allow method chaining
     */
    HvdcConverterStation setReactivePowerSetpoint(float reactivePowerSetpoint);
}
