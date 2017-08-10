/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.iidm.network.impl;

import com.google.common.collect.Sets;
import eu.itesla_project.iidm.network.*;
import eu.itesla_project.iidm.network.test.EurostagTutorialExample1Factory;
import eu.itesla_project.iidm.network.test.FictitiousSwitchFactory;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class MultiStatesTest {
    @Test
    public void test1() {
        Network networkFictious = addMoreEquipmentsToFictitiousSwitchNetwork();
        Network networkEurosTag = addMoreEquipmentsToEurosTagNetwork();
        List<String> statesToAdd = new ArrayList<>();
        statesToAdd.add("s1");
        statesToAdd.add("s2");
        statesToAdd.add("s3");
        statesToAdd.add("s4");
        StateManager stateManagerFictious = networkFictious.getStateManager();
        StateManager stateManagerEurosTag = networkEurosTag.getStateManager();

        // get equipment value from init state
        TwoWindingsTransformer twoWindingsTransformer = networkFictious.getTwoWindingsTransformer("CI");
        PhaseTapChanger phaseTapChanger = twoWindingsTransformer.getPhaseTapChanger();
        float phaseTapChangerV0 = phaseTapChanger.getRegulationValue();
        RatioTapChanger ratioTapChanger = twoWindingsTransformer.getRatioTapChanger();
        float ratioTapChangerV0 = ratioTapChanger.getTargetV();
        Generator generator = networkFictious.getGenerator("CB");
        float generatorP0 = generator.getTargetP();
        Load load = networkFictious.getLoad("CE");
        float loadP0 = load.getP0();
        // extend 4 more states
        stateManagerFictious.cloneState(StateManager.INITIAL_STATE_ID, statesToAdd);
        stateManagerEurosTag.cloneState(StateManager.INITIAL_STATE_ID, statesToAdd);
        assertEquals(5, stateManagerFictious.getStateIds().size());
        assertTrue(Sets.newHashSet(StateManager.INITIAL_STATE_ID, "s1", "s2", "s3", "s4").equals(stateManagerFictious.getStateIds()));
        assertTrue(Sets.newHashSet(StateManager.INITIAL_STATE_ID, "s1", "s2", "s3", "s4").equals(stateManagerEurosTag.getStateIds()));

        // change working state to s4
        stateManagerFictious.setWorkingState("s4");
        stateManagerEurosTag.setWorkingState("s4");
        float phaseTapChangerV4 = phaseTapChanger.getRegulationValue();
        float ratioTapChangerV4 = ratioTapChanger.getTargetV();
        float generatorP4 = generator.getTargetP();
        float loadP4 = load.getP0();
        // check cloned by extend
        assertEquals(phaseTapChangerV0, phaseTapChangerV4, 0.0f);
        assertEquals(ratioTapChangerV0, ratioTapChangerV4, 0.0f);
        assertEquals(generatorP0, generatorP4, 0.0f);
        assertEquals(loadP0, loadP4, 0.0f);

        // delete s2
        stateManagerFictious.removeState("s2");
        stateManagerEurosTag.removeState("s2");
        try {
            stateManagerFictious.setWorkingState("s2");
            fail();
        } catch (Exception ingored) {
        }
        try {
            stateManagerEurosTag.setWorkingState("s2");
            fail();
        } catch (Exception ignored){

        }
        assertTrue(Sets.newHashSet(StateManager.INITIAL_STATE_ID, "s1", "s3", "s4").equals(stateManagerFictious.getStateIds()));
        assertTrue(Sets.newHashSet(StateManager.INITIAL_STATE_ID, "s1", "s3", "s4").equals(stateManagerEurosTag.getStateIds()));


        // allocate s4 to s22
        stateManagerFictious.cloneState("s4", "s22");
        stateManagerEurosTag.cloneState("s4", "s22");

        // change working state to s22
        stateManagerFictious.setWorkingState("s22");
        stateManagerEurosTag.setWorkingState("s22");
        float phaseTapChangerV22 = phaseTapChanger.getRegulationValue();
        float ratioTapChangerV22 = ratioTapChanger.getTargetV();
        float generatorP22 = generator.getTargetP();
        float loadP22 = load.getP0();
        // check cloned by allocate
        assertEquals(phaseTapChangerV4, phaseTapChangerV22, 0.0f);
        assertEquals(ratioTapChangerV4, ratioTapChangerV22, 0.0f);
        assertEquals(generatorP4, generatorP22, 0.0f);
        assertEquals(loadP4, loadP22, 0.0f);

        // reduce s4
        stateManagerFictious.removeState("s4");
        stateManagerEurosTag.removeState("s4");
        try {
            stateManagerFictious.setWorkingState("s4");
            fail();
        } catch (Exception ingored) {
        }
        try {
            stateManagerEurosTag.setWorkingState("s4");
            fail();
        } catch (Exception ingored) {
        }
        assertTrue(Sets.newHashSet(StateManager.INITIAL_STATE_ID, "s1", "s22", "s3").equals(stateManagerFictious.getStateIds()));
        assertTrue(Sets.newHashSet(StateManager.INITIAL_STATE_ID, "s1", "s22", "s3").equals(stateManagerEurosTag.getStateIds()));
    }

    private Network addMoreEquipmentsToFictitiousSwitchNetwork() {
        Network network = FictitiousSwitchFactory.create();
        TwoWindingsTransformer twoWindingsTransformer = network.getTwoWindingsTransformer("CI");
        twoWindingsTransformer
                .newRatioTapChanger()
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

    private Network addMoreEquipmentsToEurosTagNetwork() {
        Network network = EurostagTutorialExample1Factory.create();
        network.getVoltageLevel("VLHV1")
                .newDanglingLine()
                    .setBus("NHV1")
                    .setConnectableBus("NHV1")
                    .setId("danglingId")
                    .setR(1.0f)
                    .setX(1.0f)
                    .setG(1.0f)
                    .setB(1.0f)
                    .setP0(1.0f)
                    .setQ0(1.0f)
                    .setName("danglingName")
                    .setUcteXnodeCode("code")
                .add();
        return network;
    }

}
