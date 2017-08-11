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
    public void testChangeStates() {
        Network fictitiousNetwork = addMoreEquipmentsToFictitiousSwitchNetwork();
        Network eurosTagNetwork = addMoreEquipmentsToEurosTagNetwork();
        List<String> statesToAdd = new ArrayList<>();
        statesToAdd.add("s1");
        statesToAdd.add("s2");
        statesToAdd.add("s3");
        statesToAdd.add("s4");
        StateManager fictitiousNetworkStateManager = fictitiousNetwork.getStateManager();
        StateManager eurosTagNetworkStateManager = eurosTagNetwork.getStateManager();

        // get equipment value from init state
        TwoWindingsTransformer twoWindingsTransformer = fictitiousNetwork.getTwoWindingsTransformer("CI");
        PhaseTapChanger phaseTapChanger = twoWindingsTransformer.getPhaseTapChanger();
        float phaseTapChangerV0 = phaseTapChanger.getRegulationValue();
        RatioTapChanger ratioTapChanger = twoWindingsTransformer.getRatioTapChanger();
        float ratioTapChangerV0 = ratioTapChanger.getTargetV();
        Generator generator = fictitiousNetwork.getGenerator("CB");
        float generatorP0 = generator.getTargetP();
        Load load = fictitiousNetwork.getLoad("CE");
        float loadP0 = load.getP0();
        ShuntCompensator shuntCompensator = eurosTagNetwork.getShunt("sc");
        float shuntCurrentB0 = shuntCompensator.getCurrentB();
        VscConverterStation vscConverterStation = eurosTagNetwork.getVscConverterStation("vsc");
        float vscPoint0 = vscConverterStation.getReactivePowerSetpoint();
        StaticVarCompensator staticVarCompensator = eurosTagNetwork.getStaticVarCompensator("svc");
        float svcPoint0 = staticVarCompensator.getReactivePowerSetPoint();

        // extend 4 more states
        fictitiousNetworkStateManager.cloneState(StateManager.INITIAL_STATE_ID, statesToAdd);
        eurosTagNetworkStateManager.cloneState(StateManager.INITIAL_STATE_ID, statesToAdd);
        assertEquals(5, fictitiousNetworkStateManager.getStateIds().size());
        assertTrue(Sets.newHashSet(StateManager.INITIAL_STATE_ID, "s1", "s2", "s3", "s4").equals(fictitiousNetworkStateManager.getStateIds()));
        assertTrue(Sets.newHashSet(StateManager.INITIAL_STATE_ID, "s1", "s2", "s3", "s4").equals(eurosTagNetworkStateManager.getStateIds()));

        // change working state to s4
        fictitiousNetworkStateManager.setWorkingState("s4");
        eurosTagNetworkStateManager.setWorkingState("s4");
        float phaseTapChangerV4 = phaseTapChanger.getRegulationValue();
        float ratioTapChangerV4 = ratioTapChanger.getTargetV();
        float generatorP4 = generator.getTargetP();
        float loadP4 = load.getP0();
        float shuntCurrentB4 = shuntCompensator.getCurrentB();
        float vscPoint4 = vscConverterStation.getReactivePowerSetpoint();
        float svcPoint4 = staticVarCompensator.getReactivePowerSetPoint();

        // check cloned by extend
        assertEquals(phaseTapChangerV0, phaseTapChangerV4, 0.0f);
        assertEquals(ratioTapChangerV0, ratioTapChangerV4, 0.0f);
        assertEquals(generatorP0, generatorP4, 0.0f);
        assertEquals(loadP0, loadP4, 0.0f);
        assertEquals(shuntCurrentB0, shuntCurrentB4, 0.0f);
        assertEquals(vscPoint0, vscPoint4, 0.0f);
        assertEquals(svcPoint0, svcPoint4, 0.0f);

        // delete s2
        fictitiousNetworkStateManager.removeState("s2");
        eurosTagNetworkStateManager.removeState("s2");
        try {
            fictitiousNetworkStateManager.setWorkingState("s2");
            fail();
        } catch (Exception ingored) {
        }
        try {
            eurosTagNetworkStateManager.setWorkingState("s2");
            fail();
        } catch (Exception ignored){

        }
        assertTrue(Sets.newHashSet(StateManager.INITIAL_STATE_ID, "s1", "s3", "s4").equals(fictitiousNetworkStateManager.getStateIds()));
        assertTrue(Sets.newHashSet(StateManager.INITIAL_STATE_ID, "s1", "s3", "s4").equals(eurosTagNetworkStateManager.getStateIds()));


        // allocate s4 to s22
        fictitiousNetworkStateManager.cloneState("s4", "s22");
        eurosTagNetworkStateManager.cloneState("s4", "s22");

        // change working state to s22
        fictitiousNetworkStateManager.setWorkingState("s22");
        eurosTagNetworkStateManager.setWorkingState("s22");
        float phaseTapChangerV22 = phaseTapChanger.getRegulationValue();
        float ratioTapChangerV22 = ratioTapChanger.getTargetV();
        float generatorP22 = generator.getTargetP();
        float loadP22 = load.getP0();
        float shuntCurrentB22 = shuntCompensator.getCurrentB();
        float vscPoint22 = vscConverterStation.getReactivePowerSetpoint();
        float svcPoint22 = staticVarCompensator.getReactivePowerSetPoint();

        // check cloned by allocate
        assertEquals(phaseTapChangerV4, phaseTapChangerV22, 0.0f);
        assertEquals(ratioTapChangerV4, ratioTapChangerV22, 0.0f);
        assertEquals(generatorP4, generatorP22, 0.0f);
        assertEquals(loadP4, loadP22, 0.0f);
        assertEquals(shuntCurrentB4, shuntCurrentB22, 0.0f);
        assertEquals(vscPoint4, vscPoint22, 0.0f);
        assertEquals(svcPoint4, svcPoint22, 0.0f);

        // reduce s4
        fictitiousNetworkStateManager.removeState("s4");
        eurosTagNetworkStateManager.removeState("s4");
        try {
            fictitiousNetworkStateManager.setWorkingState("s4");
            fail();
        } catch (Exception ingored) {
        }
        try {
            eurosTagNetworkStateManager.setWorkingState("s4");
            fail();
        } catch (Exception ingored) {
        }
        assertTrue(Sets.newHashSet(StateManager.INITIAL_STATE_ID, "s1", "s22", "s3").equals(fictitiousNetworkStateManager.getStateIds()));
        assertTrue(Sets.newHashSet(StateManager.INITIAL_STATE_ID, "s1", "s22", "s3").equals(eurosTagNetworkStateManager.getStateIds()));
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
        VoltageLevel voltageLevel = network.getVoltageLevel("VLHV1");
        voltageLevel.newDanglingLine()
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
        ShuntCompensator shuntCompensator = voltageLevel.newShunt()
                                                .setName("shunt")
                                                .setId("sc")
                                                .setConnectableBus("NHV1")
                                                .setbPerSection(1.0f)
                                                .setCurrentSectionCount(3)
                                                .setMaximumSectionCount(10)
                                            .add();
        ThreeWindingsTransformer transformer = network.getSubstation("P1").newThreeWindingsTransformer()
                                                .newLeg1().setR(1.3f).setX(1.4f).setRatedU(1.1f)
                                                .setG(1.6f).setB(1.7f)
                                                .setVoltageLevel("VLGEN").setConnectableBus("NGEN").add()
                                                .newLeg2().setR(2.03f).setX(2.04f).setRatedU(2.05f)
                                                .setVoltageLevel("VLGEN").setConnectableBus("NGEN").add()
                                                .newLeg3().setR(3.3f).setX(3.4f).setRatedU(3.5f)
                                                .setVoltageLevel("VLGEN").setConnectableBus("NGEN").add()
                                                .setId("3wt")
                                                .setName("3wt")
                                            .add();
        VscConverterStation vscConverterStation = voltageLevel.newVscConverterStation()
                                                    .setId("vsc")
                                                    .setName("vsc")
                                                    .setReactivePowerSetpoint(1.0f)
                                                    .setVoltageRegulatorOn(true)
                                                    .setVoltageSetpoint(2.0f)
                                                    .setLossFactor(1.0f)
                                                    .setBus("NHV1")
                                                    .setConnectableBus("NHV1")
                                                .add();
        StaticVarCompensator staticVarCompensator = voltageLevel.newStaticVarCompensator()
                                                    .setId("svc")
                                                    .setName("svc")
                                                    .setBmax(50.0f)
                                                    .setBmin(20.0f)
                                                    .setReactivePowerSetPoint(2.0f)
                                                    .setRegulationMode(StaticVarCompensator.RegulationMode.OFF)
                                                    .setBus("NHV1")
                                                    .setConnectableBus("NHV1")
                                                .add();
        return network;
    }

}
