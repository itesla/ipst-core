/**
 * Copyright (c) 2016, All partners of the iTesla project (http://www.itesla-project.eu/consortium)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.iidm.network.impl;

import eu.itesla_project.iidm.network.ConnectableType;
import eu.itesla_project.iidm.network.Identifiable;
import eu.itesla_project.iidm.network.TwoWindingsTransformer;

/**
 *
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
class TwoWindingsTransformerImpl extends AbstractBranch<TwoWindingsTransformer> implements TwoWindingsTransformer, RatioTapChangerParent {

    private final SubstationImpl substation;

    private float r;

    private float x;

    private float g;

    private float b;

    private float ratedU1;

    private float ratedU2;

    private RatioTapChangerImpl ratioTapChanger;

    private PhaseTapChangerImpl phaseTapChanger;

    TwoWindingsTransformerImpl(String id, String name,
            SubstationImpl substation,
            float r, float x, float g, float b, float ratedU1, float ratedU2) {
        super(id, name);
        this.substation = substation;
        this.r = r;
        this.x = x;
        this.g = g;
        this.b = b;
        this.ratedU1 = ratedU1;
        this.ratedU2 = ratedU2;
    }

    @Override
    public ConnectableType getType() {
        return ConnectableType.TWO_WINDINGS_TRANSFORMER;
    }

    @Override
    public SubstationImpl getSubstation() {
        return substation;
    }

    @Override
    public float getR() {
        return r;
    }

    @Override
    public TwoWindingsTransformerImpl setR(float r) {
        ValidationUtil.checkR(this, r);
        float oldValue = this.r;
        this.r = r;
        notifyUpdate("r", oldValue, r);
        return this;
    }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public TwoWindingsTransformerImpl setX(float x) {
        ValidationUtil.checkX(this, x);
        float oldValue = this.x;
        this.x = x;
        notifyUpdate("x", oldValue, x);
        return this;
    }

    @Override
    public float getG() {
        return g;
    }

    @Override
    public TwoWindingsTransformerImpl setG(float g) {
        ValidationUtil.checkG(this, g);
        float oldValue = this.g;
        this.g = g;
        notifyUpdate("g", oldValue, g);
        return this;
    }

    @Override
    public float getB() {
        return b;
    }

    @Override
    public TwoWindingsTransformerImpl setB(float b) {
        ValidationUtil.checkB(this, b);
        float oldValue = this.b;
        this.b = b;
        notifyUpdate("b", oldValue, b);
        return this;
    }

    @Override
    public float getRatedU1() {
        return ratedU1;
    }

    @Override
    public TwoWindingsTransformerImpl setRatedU1(float ratedU1) {
        ValidationUtil.checkRatedU1(this, ratedU1);
        float oldValue = this.ratedU1;
        this.ratedU1 = ratedU1;
        notifyUpdate("ratedU1", oldValue, ratedU1);
        return this;
    }

    @Override
    public float getRatedU2() {
        return ratedU2;
    }

    @Override
    public TwoWindingsTransformerImpl setRatedU2(float ratedU2) {
        ValidationUtil.checkRatedU2(this, ratedU2);
        float oldValue = this.ratedU2;
        this.ratedU2 = ratedU2;
        notifyUpdate("ratedU2", oldValue, ratedU2);
        return this;
    }

    @Override
    public RatioTapChangerAdderImpl newRatioTapChanger() {
        return new RatioTapChangerAdderImpl(this);
    }

    @Override
    public RatioTapChangerImpl getRatioTapChanger() {
        return ratioTapChanger;
    }

    @Override
    public PhaseTapChangerAdderImpl newPhaseTapChanger() {
        return new PhaseTapChangerAdderImpl(this);
    }

    @Override
    public PhaseTapChangerImpl getPhaseTapChanger() {
        return phaseTapChanger;
    }

    @Override
    public NetworkImpl getNetwork() {
        return substation.getNetwork();
    }

    @Override
    public void setRatioTapChanger(RatioTapChangerImpl ratioTapChanger) {
        this.ratioTapChanger = ratioTapChanger;
    }

    void setPhaseTapChanger(PhaseTapChangerImpl phaseTapChanger) {
        this.phaseTapChanger = phaseTapChanger;
    }

    @Override
    public void extendStateArraySize(int initStateArraySize, int number, int sourceIndex) {
        super.extendStateArraySize(initStateArraySize, number, sourceIndex);
        if (ratioTapChanger != null) {
            ratioTapChanger.extendStateArraySize(initStateArraySize, number, sourceIndex);
        }
        if (phaseTapChanger != null) {
            phaseTapChanger.extendStateArraySize(initStateArraySize, number, sourceIndex);
        }
    }

    @Override
    public void reduceStateArraySize(int number) {
        super.reduceStateArraySize(number);
        if (ratioTapChanger != null) {
            ratioTapChanger.reduceStateArraySize(number);
        }
        if (phaseTapChanger != null) {
            phaseTapChanger.reduceStateArraySize(number);
        }
    }

    @Override
    public void deleteStateArrayElement(int index) {
        super.deleteStateArrayElement(index);
        if (ratioTapChanger != null) {
            ratioTapChanger.deleteStateArrayElement(index);
        }
        if (phaseTapChanger != null) {
            phaseTapChanger.deleteStateArrayElement(index);
        }
    }

    @Override
    public void allocateStateArrayElement(int[] indexes, int sourceIndex) {
        super.allocateStateArrayElement(indexes, sourceIndex);
        if (ratioTapChanger != null) {
            ratioTapChanger.allocateStateArrayElement(indexes, sourceIndex);
        }
        if (phaseTapChanger != null) {
            phaseTapChanger.allocateStateArrayElement(indexes, sourceIndex);
        }
    }

    public Identifiable getTransformer() {
        return this;
    }

    @Override
    public String getTapChangerAttribute() {
        return "ratioTapChanger";
    }

    @Override
    protected String getTypeDescription() {
        return "2 windings transformer";
    }
}
