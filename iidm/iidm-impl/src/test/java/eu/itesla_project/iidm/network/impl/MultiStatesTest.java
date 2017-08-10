/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.iidm.network.impl;

import eu.itesla_project.iidm.network.*;
import eu.itesla_project.iidm.network.test.FictitiousSwitchFactory;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class MultiStatesTest {
    @Test
    public void test() {
        Network network = createNetworkWithAllTypesOfStatefulEquipments();
        List<String> statesToAdd = new ArrayList<>();
        statesToAdd.add("s1");
        statesToAdd.add("s2");
        statesToAdd.add("s3");
        statesToAdd.add("s4");
        StateManager stateManager = network.getStateManager();

        // get equipment value from init state
        TwoWindingsTransformer twoWindingsTransformer = network.getTwoWindingsTransformer("CI");
        PhaseTapChanger phaseTapChanger = twoWindingsTransformer.getPhaseTapChanger();
        float phaseTapChangerV0 = phaseTapChanger.getRegulationValue();
        RatioTapChanger ratioTapChanger = twoWindingsTransformer.getRatioTapChanger();
        float ratioTapChangerV0 = ratioTapChanger.getTargetV();

        // extend 4 more states
        stateManager.cloneState(StateManager.INITIAL_STATE_ID, statesToAdd);
        assertEquals(5, stateManager.getStateIds().size());

        // change working state to s4
        stateManager.setWorkingState("s4");
        float phaseTapChangerV4 = phaseTapChanger.getRegulationValue();
        float ratioTapChangerV4 = ratioTapChanger.getTargetV();
        // check cloned by extend
        assertEquals(phaseTapChangerV0, phaseTapChangerV4, 0.0f);
        assertEquals(ratioTapChangerV0, ratioTapChangerV4, 0.0f);

        // delete s2
        stateManager.removeState("s2");
        try {
            stateManager.setWorkingState("s2");
            fail();
        } catch (Exception ingored) {
        }
        assertEquals(4, stateManager.getStateIds().size());

        // allocate s4 to s22
        stateManager.cloneState("s4", "s22");

        // change working state to s22
        stateManager.setWorkingState("s22");
        float phaseTapChangerV22 = phaseTapChanger.getRegulationValue();
        float ratioTapChangerV22 = ratioTapChanger.getTargetV();
        // check cloned by allocate
        assertEquals(phaseTapChangerV4, phaseTapChangerV22, 0.0f);
        assertEquals(ratioTapChangerV4, ratioTapChangerV22, 0.0f);

        // reduce s4
        stateManager.removeState("s4");
        try {
            stateManager.setWorkingState("s4");
            fail();
        } catch (Exception ingored) {
        }
        assertEquals(4, stateManager.getStateIds().size());
    }

    private Network createNetworkWithAllTypesOfStatefulEquipments() {
        Network network = FictitiousSwitchFactory.create();
        TwoWindingsTransformer twoWindingsTransformer = network.getTwoWindingsTransformer("CI");
        twoWindingsTransformer.newRatioTapChanger()
                .setTargetV(200)
                .setLoadTapChangingCapabilities(false)
                .setLowTapPosition(0)
                .setTapPosition(0)
                .setRegulating(false)
                .setRegulationTerminal(twoWindingsTransformer.getTerminal(TwoTerminalsConnectable.Side.ONE))
                .beginStep().setR(39.78473f).setX(39.784725f).setG(0.0f).setB(0.0f).setRho(1.0f).endStep()
                .add();
        return network;
    }

}
