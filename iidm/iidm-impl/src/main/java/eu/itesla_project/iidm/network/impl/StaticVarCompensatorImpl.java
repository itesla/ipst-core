/**
 * Copyright (c) 2016, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.iidm.network.impl;

import eu.itesla_project.iidm.network.ConnectableType;
import eu.itesla_project.iidm.network.StaticVarCompensator;
import eu.itesla_project.iidm.network.Terminal;
import eu.itesla_project.iidm.network.impl.util.Ref;
import gnu.trove.list.array.TFloatArrayList;
import gnu.trove.list.array.TIntArrayList;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
class StaticVarCompensatorImpl extends AbstractConnectable<StaticVarCompensator> implements StaticVarCompensator {

    static final String TYPE_DESCRIPTION = "staticVarCompensator";

    private float bMin;

    private float bMax;

    // attributes depending on the state

    private final TFloatArrayList voltageSetPoint;

    private final TFloatArrayList reactivePowerSetPoint;

    private final TIntArrayList regulationMode;

    StaticVarCompensatorImpl(String id, String name, float bMin, float bMax, float voltageSetPoint, float reactivePowerSetPoint,
                             RegulationMode regulationMode, Ref<? extends MultiStateObject> ref) {
        super(id, name);
        this.bMin = bMin;
        this.bMax = bMax;
        int stateArraySize = ref.get().getStateManager().getStateArraySize();
        this.voltageSetPoint = new TFloatArrayList(stateArraySize);
        this.reactivePowerSetPoint = new TFloatArrayList(stateArraySize);
        this.regulationMode = new TIntArrayList(stateArraySize);
        for (int i = 0; i < stateArraySize; i++) {
            this.voltageSetPoint.add(voltageSetPoint);
            this.reactivePowerSetPoint.add(reactivePowerSetPoint);
            this.regulationMode.add(regulationMode.ordinal());
        }
    }

    @Override
    public Terminal getTerminal() {
        return terminals.get(0);
    }

    @Override
    public ConnectableType getType() {
        return ConnectableType.STATIC_VAR_COMPENSATOR;
    }

    @Override
    protected String getTypeDescription() {
        return TYPE_DESCRIPTION;
    }

    @Override
    public float getBmin() {
        return bMin;
    }

    @Override
    public StaticVarCompensatorImpl setBmin(float bMin) {
        ValidationUtil.checkBmin(this, bMin);
        this.bMin = bMin;
        return this;
    }

    @Override
    public float getBmax() {
        return bMax;
    }

    @Override
    public StaticVarCompensatorImpl setBmax(float bMax) {
        ValidationUtil.checkBmax(this, bMax);
        this.bMax = bMax;
        return this;
    }

    @Override
    public float getVoltageSetPoint() {
        return voltageSetPoint.get(getNetwork().getStateIndex());
    }

    @Override
    public StaticVarCompensatorImpl setVoltageSetPoint(float voltageSetPoint) {
        ValidationUtil.checkSvcRegulator(this, voltageSetPoint, getReactivePowerSetPoint(), getRegulationMode());
        float oldValue = this.voltageSetPoint.set(getNetwork().getStateIndex(), voltageSetPoint);
        notifyUpdate("voltageSetPoint", oldValue, voltageSetPoint);
        return this;
    }

    @Override
    public float getReactivePowerSetPoint() {
        return reactivePowerSetPoint.get(getNetwork().getStateIndex());
    }

    @Override
    public StaticVarCompensatorImpl setReactivePowerSetPoint(float reactivePowerSetPoint) {
        ValidationUtil.checkSvcRegulator(this, getVoltageSetPoint(), reactivePowerSetPoint, getRegulationMode());
        float oldValue = this.reactivePowerSetPoint.set(getNetwork().getStateIndex(), reactivePowerSetPoint);
        notifyUpdate("reactivePowerSetPoint", oldValue, reactivePowerSetPoint);
        return this;
    }

    @Override
    public RegulationMode getRegulationMode() {
        return RegulationMode.values()[regulationMode.get(getNetwork().getStateIndex())];
    }

    @Override
    public StaticVarCompensatorImpl setRegulationMode(RegulationMode regulationMode) {
        ValidationUtil.checkSvcRegulator(this, getVoltageSetPoint(), getReactivePowerSetPoint(), regulationMode);
        RegulationMode oldValue = RegulationMode.values()[this.regulationMode.set(getNetwork().getStateIndex(), regulationMode.ordinal())];
        notifyUpdate("regulationMode", oldValue, regulationMode);
        return this;
    }

    @Override
    public void extendStateArraySize(int initStateArraySize, int number, int sourceIndex) {
        super.extendStateArraySize(initStateArraySize, number, sourceIndex);
        voltageSetPoint.ensureCapacity(voltageSetPoint.size() + number);
        reactivePowerSetPoint.ensureCapacity(reactivePowerSetPoint.size() + number);
        regulationMode.ensureCapacity(regulationMode.size() + number);
        for (int i = 0; i < number; i++) {
            voltageSetPoint.add(voltageSetPoint.get(sourceIndex));
            reactivePowerSetPoint.add(reactivePowerSetPoint.get(sourceIndex));
            regulationMode.add(regulationMode.get(sourceIndex));
        }
    }

    @Override
    public void reduceStateArraySize(int number) {
        super.reduceStateArraySize(number);
        voltageSetPoint.remove(voltageSetPoint.size() - number, number);
        reactivePowerSetPoint.remove(reactivePowerSetPoint.size() - number, number);
        regulationMode.remove(regulationMode.size() - number, number);
    }

    @Override
    public void deleteStateArrayElement(int index) {
        super.deleteStateArrayElement(index);
        // nothing to do
    }

    @Override
    public void allocateStateArrayElement(int[] indexes, int sourceIndex) {
        super.allocateStateArrayElement(indexes, sourceIndex);
        for (int index : indexes) {
            voltageSetPoint.set(index, voltageSetPoint.get(sourceIndex));
            reactivePowerSetPoint.set(index, reactivePowerSetPoint.get(sourceIndex));
            regulationMode.set(index, regulationMode.get(sourceIndex));
        }
    }

}
