/**
 * Copyright (c) 2016, All partners of the iTesla project (http://www.itesla-project.eu/consortium)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.iidm.network.impl;

import eu.itesla_project.iidm.network.ConnectableType;
import eu.itesla_project.iidm.network.Load;
import eu.itesla_project.iidm.network.LoadType;
import eu.itesla_project.iidm.network.impl.util.Ref;
import gnu.trove.list.array.TFloatArrayList;

/**
 *
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
class LoadImpl extends AbstractConnectable<Load> implements Load {

    private final Ref<? extends MultiStateObject> network;

    private LoadType loadType;

    // attributes depending on the state

    private final TFloatArrayList p0;

    private final TFloatArrayList q0;

    LoadImpl(Ref<? extends MultiStateObject> network,
             String id, String name, LoadType loadType, float p0, float q0) {
        super(id, name);
        this.network = network;
        this.loadType = loadType;
        int stateArraySize = network.get().getStateManager().getStateArraySize();
        this.p0 = new TFloatArrayList(stateArraySize);
        this.q0 = new TFloatArrayList(stateArraySize);
        for (int i = 0; i < stateArraySize; i++) {
            this.p0.add(p0);
            this.q0.add(q0);
        }
    }

    @Override
    public ConnectableType getType() {
        return ConnectableType.LOAD;
    }

    @Override
    public TerminalExt getTerminal() {
        return terminals.get(0);
    }

    @Override
    protected String getTypeDescription() {
        return "Load";
    }

    @Override
    public LoadType getLoadType() {
        return loadType;
    }

    @Override
    public Load setLoadType(LoadType loadType) {
        ValidationUtil.checkLoadType(this, loadType);
        LoadType oldValue = this.loadType;
        this.loadType = loadType;
        notifyUpdate("loadType", oldValue.toString(), loadType.toString());
        return this;
    }

    @Override
    public float getP0() {
        return p0.get(network.get().getStateIndex());
    }

    @Override
    public LoadImpl setP0(float p0) {
        ValidationUtil.checkP0(this, p0);
        float oldValue = this.p0.set(network.get().getStateIndex(), p0);
        notifyUpdate("p0", oldValue, p0);
        return this;
    }

    @Override
    public float getQ0() {
        return q0.get(network.get().getStateIndex());
    }

    @Override
    public LoadImpl setQ0(float q0) {
        ValidationUtil.checkQ0(this, q0);
        float oldValue = this.q0.set(network.get().getStateIndex(), q0);
        notifyUpdate("q0", oldValue, q0);
        return this;
    }

    @Override
    public void extendStateArraySize(int initStateArraySize, int number, int sourceIndex) {
        super.extendStateArraySize(initStateArraySize, number, sourceIndex);
        p0.ensureCapacity(p0.size() + number);
        q0.ensureCapacity(q0.size() + number);
        for (int i = 0; i < number; i++) {
            p0.add(p0.get(sourceIndex));
            q0.add(q0.get(sourceIndex));
        }
    }

    @Override
    public void reduceStateArraySize(int number) {
        super.reduceStateArraySize(number);
        p0.remove(p0.size() - number, number);
        q0.remove(q0.size() - number, number);
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
            p0.set(index, p0.get(sourceIndex));
            q0.set(index, q0.get(sourceIndex));
        }
    }

}
