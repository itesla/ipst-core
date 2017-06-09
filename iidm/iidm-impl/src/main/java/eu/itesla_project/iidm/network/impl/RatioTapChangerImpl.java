/**
 * Copyright (c) 2016, All partners of the iTesla project (http://www.itesla-project.eu/consortium)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.iidm.network.impl;

import eu.itesla_project.iidm.network.RatioTapChanger;
import eu.itesla_project.iidm.network.Terminal;
import gnu.trove.list.array.TFloatArrayList;
import java.util.List;

/**
 *
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
class RatioTapChangerImpl extends TapChangerImpl<RatioTapChangerParent, RatioTapChangerImpl, RatioTapChangerStepImpl> implements RatioTapChanger {

    private boolean loadTapChangingCapabilities;

    // attributes depending on the state

    private final TFloatArrayList targetV;

    RatioTapChangerImpl(RatioTapChangerParent parent, int lowTapPosition,
                        List<RatioTapChangerStepImpl> steps, TerminalExt regulationTerminal, boolean loadTapChangingCapabilities,
                        int tapPosition, boolean regulating, float targetV) {
        super(parent.getNetwork().getRef(), parent, lowTapPosition, steps, regulationTerminal, tapPosition, regulating);
        this.loadTapChangingCapabilities = loadTapChangingCapabilities;
        int stateArraySize = network.get().getStateManager().getStateArraySize();
        this.targetV = new TFloatArrayList(stateArraySize);
        for (int i = 0; i < stateArraySize; i++) {
            this.targetV.add(targetV);
        }
    }

    @Override
    protected NetworkImpl getNetwork() {
        return parent.getNetwork();
    }

    @Override
    public RatioTapChangerImpl setRegulating(boolean regulating) {
        ValidationUtil.checkRatioTapChangerRegulation(parent, loadTapChangingCapabilities, regulating, regulationTerminal, getTargetV(), getNetwork());
        return super.setRegulating(regulating);
    }

    @Override
    public boolean hasLoadTapChangingCapabilities() {
        return loadTapChangingCapabilities;
    }

    @Override
    public float getTargetV() {
        return targetV.get(network.get().getStateIndex());
    }

    @Override
    public RatioTapChangerImpl setTargetV(float targetV) {
        ValidationUtil.checkRatioTapChangerRegulation(parent, loadTapChangingCapabilities, isRegulating(), regulationTerminal, targetV, getNetwork());
        this.targetV.set(network.get().getStateIndex(), targetV);
        return this;
    }

    @Override
    public RatioTapChangerImpl setRegulationTerminal(Terminal regulationTerminal) {
        ValidationUtil.checkRatioTapChangerRegulation(parent, loadTapChangingCapabilities, isRegulating(), regulationTerminal, getTargetV(), getNetwork());
        return super.setRegulationTerminal(regulationTerminal);
    }

    @Override
    public void remove() {
        parent.setRatioTapChanger(null);
    }

    @Override
    public void extendStateArraySize(int initStateArraySize, int number, int sourceIndex) {
        super.extendStateArraySize(initStateArraySize, number, sourceIndex);
        targetV.ensureCapacity(targetV.size() + number);
        for (int i = 0; i < number; i++) {
            targetV.add(targetV.get(sourceIndex));
        }
    }

    @Override
    public void reduceStateArraySize(int number) {
        super.reduceStateArraySize(number);
        targetV.remove(targetV.size() - number, number);
    }

    @Override
    public void deleteStateArrayElement(int index) {
        super.deleteStateArrayElement(index);
        // nothing to do
    }

    @Override
    public void allocateStateArrayElement(int[] indexes, final int sourceIndex) {
        super.allocateStateArrayElement(indexes, sourceIndex);
        for (int index : indexes) {
            targetV.set(index, targetV.get(sourceIndex));
        }
    }

    @Override
    protected String getTapChangerAttribute() {
        return parent.getTapChangerAttribute();
    }
}
