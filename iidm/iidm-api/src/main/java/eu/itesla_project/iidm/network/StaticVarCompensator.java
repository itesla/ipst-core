/**
 * Copyright (c) 2016, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.iidm.network;

/**
 * Static VAR compensator model.
 *
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public interface StaticVarCompensator extends Injection<StaticVarCompensator> {

    enum RegulationMode {
        VOLTAGE,
        REACTIVE_POWER,
        OFF
    }

    /**
     * Get the minimum susceptance in S.
     * @return the minimum susceptance
     */
    float getBmin();

    /**
     * Set the minimum susceptance in S.
     * @param bMin minimum susceptance
     * @return this to allow method chaining
     */
    StaticVarCompensator setBmin(float bMin);

    /**
     * Get the maximum susceptance in S.
     * @return the maximum susceptance
     */
    float getBmax();

    /**
     * Set the maximum susceptance in S.
     * @param bMax the maximum susceptance
     * @return this to allow method chaining
     */
    StaticVarCompensator setBmax(float bMax);

    /**
     * <p>Get the voltage set point in Kv.</p>
     * <p>Needed only when regulating mode is set to {@link RegulationMode#VOLTAGE}.</p>
     * <p>Depends on the working state.</p>
     * @return the voltage set point
     */
    float getVoltageSetPoint();

    /**
     * <p>Set the voltage set point in Kv.</p>
     * <p>Needed only when regulating mode is set to {@link RegulationMode#VOLTAGE}.</p>
     * <p>Depends on the working state.</p>
     * @param voltageSetPoint the voltage set point
     * @return this to allow method chaining
     */
    StaticVarCompensator setVoltageSetPoint(float voltageSetPoint);

    /**
     * <p>Get the reactive power set point in KV.</p>
     * <p>Needed only when regulating mode is set to {@link RegulationMode#REACTIVE_POWER}.</p>
     * <p>Depends on the working state.</p>
     * @return the reactive power set point
     */
    float getReactivePowerSetPoint();

    /**
     * <p>Set the reactive power set point in KV.</p>
     * <p>Needed only when regulating mode is set to {@link RegulationMode#REACTIVE_POWER}.</p>
     * <p>Depends on the working state.</p>
     * @param reactivePowerSetPoint the reactive power set point
     * @return this to allow method chaining
     */
    StaticVarCompensator setReactivePowerSetPoint(float reactivePowerSetPoint);

    /**
     * <p>Get the regulating mode.</p>
     * <p>Depends on the working state.</p>
     * @return the regulating mode
     */
    RegulationMode getRegulationMode();

    /**
     * <p>set the regulating mode.</p>
     * <p>Depends on the working state.</p>
     * @param regulationMode the regulating mode
     * @return this to allow method chaining
     */
    StaticVarCompensator setRegulationMode(RegulationMode regulationMode);

}
