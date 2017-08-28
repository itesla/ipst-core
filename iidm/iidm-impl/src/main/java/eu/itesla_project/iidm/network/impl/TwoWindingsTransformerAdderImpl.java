/**
 * Copyright (c) 2016, All partners of the iTesla project (http://www.itesla-project.eu/consortium)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.iidm.network.impl;

import eu.itesla_project.iidm.network.TwoWindingsTransformer;
import eu.itesla_project.iidm.network.TwoWindingsTransformerAdder;

/**
 *
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
class TwoWindingsTransformerAdderImpl extends AbstractBranchAdder<TwoWindingsTransformerAdderImpl> implements TwoWindingsTransformerAdder {

    private final SubstationImpl substation;

    private float r = Float.NaN;

    private float x = Float.NaN;

    private float g = Float.NaN;

    private float b = Float.NaN;

    private float ratedU1 = Float.NaN;

    private float ratedU2 = Float.NaN;

    TwoWindingsTransformerAdderImpl(SubstationImpl substation) {
        this.substation = substation;
    }

    @Override
    protected NetworkImpl getNetwork() {
        return substation.getNetwork();
    }

    @Override
    protected String getTypeDescription() {
        return "2 windings transformer";
    }

    @Override
    public TwoWindingsTransformerAdder setR(float r) {
        this.r = r;
        return this;
    }

    @Override
    public TwoWindingsTransformerAdder setX(float x) {
        this.x = x;
        return this;
    }

    @Override
    public TwoWindingsTransformerAdder setB(float b) {
        this.b = b;
        return this;
    }

    @Override
    public TwoWindingsTransformerAdder setG(float g) {
        this.g = g;
        return this;
    }

    @Override
    public TwoWindingsTransformerAdder setRatedU1(float ratedU1) {
        this.ratedU1 = ratedU1;
        return this;
    }

    @Override
    public TwoWindingsTransformerAdder setRatedU2(float ratedU2) {
        this.ratedU2 = ratedU2;
        return this;
    }

    @Override
    public TwoWindingsTransformer add() {
        String id = checkAndGetUniqueId();
        VoltageLevelExt voltageLevel1 = checkAndGetVoltageLevel1(id);
        VoltageLevelExt voltageLevel2 = checkAndGetVoltageLevel2(id);
        if (voltageLevel1.getSubstation() != substation || voltageLevel2.getSubstation() != substation) {
            throw new ValidationException(this,
                    "the 2 windings of the transformer shall belong to the substation '"
                    + substation.getId() + "' ('" + voltageLevel1.getSubstation().getId() + "', '"
                    + voltageLevel2.getSubstation().getId() + "')");
        }
        TerminalExt terminal1 = checkAndGetTerminal1(id);
        TerminalExt terminal2 = checkAndGetTerminal2(id);

        ValidationUtil.checkR(this, r);
        ValidationUtil.checkX(this, x);
        ValidationUtil.checkG(this, g);
        ValidationUtil.checkB(this, b);
        ValidationUtil.checkRatedU1(this, ratedU1);
        ValidationUtil.checkRatedU2(this, ratedU2);

        TwoWindingsTransformerImpl transformer
                = new TwoWindingsTransformerImpl(id, getName(),
                                                 voltageLevel1.getSubstation(),
                                                 r, x, g, b,
                                                 ratedU1, ratedU2);
        terminal1.setNum(1);
        terminal2.setNum(2);
        transformer.addTerminal(terminal1);
        transformer.addTerminal(terminal2);

        // check that the two windings transformer is attachable on both side
        voltageLevel1.attach(terminal1, true);
        voltageLevel2.attach(terminal2, true);

        voltageLevel1.attach(terminal1, false);
        voltageLevel2.attach(terminal2, false);
        getNetwork().getObjectStore().checkAndAdd(transformer);
        getNetwork().getListeners().notifyCreation(transformer);
        return transformer;

    }

}
