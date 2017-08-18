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
        PhaseTapChanger.RegulationMode pTapChangerMode0 = phaseTapChanger.getRegulationMode();
        RatioTapChanger ratioTapChanger = twoWindingsTransformer.getRatioTapChanger();
        float ratioTapChangerV0 = ratioTapChanger.getTargetV();
        Generator generator = fictitiousNetwork.getGenerator("CB");
        float generatorP0 = generator.getTargetP();
        float generatorQ0 = generator.getTargetQ();
        float generatorV0 = generator.getTargetV();
        Load load = fictitiousNetwork.getLoad("CE");
        float loadP0 = load.getP0();
        float loadQ0 = load.getQ0();
        ShuntCompensator shuntCompensator = eurosTagNetwork.getShunt("sc");
        int shuntSectionCount0 = shuntCompensator.getCurrentSectionCount();
        VscConverterStation vscConverterStation = eurosTagNetwork.getVscConverterStation("vsc");
        float vscPoint0 = vscConverterStation.getReactivePowerSetpoint();
        float vscVoltageSetpoint0 = vscConverterStation.getVoltageSetpoint();
        StaticVarCompensator staticVarCompensator = eurosTagNetwork.getStaticVarCompensator("svc");
        float svcPoint0 = staticVarCompensator.getReactivePowerSetPoint();
        float svcVoltagePoint0 = staticVarCompensator.getVoltageSetPoint();
        StaticVarCompensator.RegulationMode svcMode0 = staticVarCompensator.getRegulationMode();
        Terminal terminal = load.getTerminal();
        float terminalP0 = terminal.getP();
        float terminalQ0 = terminal.getQ();
        Switch sw = fictitiousNetwork.getSwitch("R");
        sw.setOpen(false);
        DanglingLine danglingLine = eurosTagNetwork.getDanglingLine("danglingId");
        float dllP0 = danglingLine.getP0();
        float dllQ0 = danglingLine.getQ0();
        HvdcLine hvdcLine = eurosTagNetwork.getHvdcLine("hvdcLine");
        float hvdcPowerSetPoint0 = hvdcLine.getActivePowerSetpoint();
        HvdcLine.ConvertersMode hvdcMode0 = hvdcLine.getConvertersMode();

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
        PhaseTapChanger.RegulationMode pTapChangerMode4 = phaseTapChanger.getRegulationMode();
        float ratioTapChangerV4 = ratioTapChanger.getTargetV();
        float generatorP4 = generator.getTargetP();
        float generatorQ4 = generator.getTargetQ();
        float generatorV4 = generator.getTargetV();
        float loadP4 = load.getP0();
        float loadQ4 = load.getQ0();
        int shuntSectionCount4 = shuntCompensator.getCurrentSectionCount();
        float vscPoint4 = vscConverterStation.getReactivePowerSetpoint();
        float vscVoltageSetpoint4 = vscConverterStation.getVoltageSetpoint();
        float svcPoint4 = staticVarCompensator.getReactivePowerSetPoint();
        float svcVoltagePoint4 = staticVarCompensator.getVoltageSetPoint();
        StaticVarCompensator.RegulationMode svcMode4 = staticVarCompensator.getRegulationMode();
        float terminalP4 = terminal.getP();
        float terminalQ4 = terminal.getQ();
        float dllP4 = danglingLine.getP0();
        float dllQ4 = danglingLine.getQ0();
        float hvdcPowerSetPoint4 = hvdcLine.getActivePowerSetpoint();
        HvdcLine.ConvertersMode hvdcMode4 = hvdcLine.getConvertersMode();

        // check cloned by extend
        assertEquals(phaseTapChangerV0, phaseTapChangerV4, 0.0f);
        assertEquals(pTapChangerMode0, pTapChangerMode4);
        assertEquals(ratioTapChangerV0, ratioTapChangerV4, 0.0f);
        assertEquals(generatorP0, generatorP4, 0.0f);
        assertEquals(generatorQ0, generatorQ4, 0.0f);
        assertEquals(generatorV0, generatorV4, 0.0f);
        assertEquals(loadP0, loadP4, 0.0f);
        assertEquals(loadQ0, loadQ4, 0.0f);
        assertEquals(shuntSectionCount0, shuntSectionCount4);
        assertEquals(vscPoint0, vscPoint4, 0.0f);
        assertEquals(vscVoltageSetpoint0, vscVoltageSetpoint4, 0.0f);
        assertTrue(vscConverterStation.isVoltageRegulatorOn());
        assertEquals(svcPoint0, svcPoint4, 0.0f);
        assertEquals(svcVoltagePoint0, svcVoltagePoint4, 0.0f);
        assertEquals(svcMode0, svcMode4);
        assertEquals(terminalP0, terminalP4, 0.0f);
        assertEquals(terminalQ0, terminalQ4, 0.0f);
        assertFalse(sw.isOpen());
        assertEquals(dllP0, dllP4, 0.0f);
        assertEquals(dllQ0, dllQ4, 0.0f);
        assertEquals(hvdcPowerSetPoint0, hvdcPowerSetPoint4, 0.0f);
        assertEquals(hvdcMode0, hvdcMode4);

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
        } catch (Exception ignored) {
        }
        assertTrue(Sets.newHashSet(StateManager.INITIAL_STATE_ID, "s1", "s3", "s4").equals(fictitiousNetworkStateManager.getStateIds()));
        assertTrue(Sets.newHashSet(StateManager.INITIAL_STATE_ID, "s1", "s3", "s4").equals(eurosTagNetworkStateManager.getStateIds()));

        // change values in s4
        phaseTapChanger.setRegulationValue(3.1f);
        phaseTapChanger.setRegulationMode(PhaseTapChanger.RegulationMode.ACTIVE_POWER_CONTROL);
        ratioTapChanger.setTargetV(1.4f);
        generator.setTargetP(4.1f);
        generator.setTargetQ(4.1f);
        generator.setTargetV(4.1f);
        load.setP0(1.5f);
        load.setQ0(2.5f);
        shuntCompensator.setCurrentSectionCount(8);
        vscConverterStation.setReactivePowerSetpoint(9.2f);
        vscConverterStation.setVoltageSetpoint(2.9f);
        vscConverterStation.setVoltageRegulatorOn(false);
        staticVarCompensator.setReactivePowerSetPoint(2.6f);
        staticVarCompensator.setVoltageSetPoint(6.2f);
        staticVarCompensator.setRegulationMode(StaticVarCompensator.RegulationMode.VOLTAGE);
        terminal.setP(3.1f);
        terminal.setQ(4.1f);
        sw.setOpen(true);
        danglingLine.setP0(9.9f);
        danglingLine.setQ0(8.8f);
        hvdcLine.setActivePowerSetpoint(20.0f);
        hvdcLine.setConvertersMode(HvdcLine.ConvertersMode.SIDE_1_RECTIFIER_SIDE_2_INVERTER);
        // get changed values in s4
        float phaseTapChangerV4Changed = phaseTapChanger.getRegulationValue();
        PhaseTapChanger.RegulationMode pTapChangerMode4Changed = phaseTapChanger.getRegulationMode();
        float ratioTapChangerV4Changed = ratioTapChanger.getTargetV();
        float generatorP4Changed = generator.getTargetP();
        float generatorQ4Changed = generator.getTargetQ();
        float generatorV4Changed = generator.getTargetV();
        float loadP4Changed = load.getP0();
        float loadQ4Changed = load.getQ0();
        int shuntSectionCount4Changed = shuntCompensator.getCurrentSectionCount();
        float vscPoint4Changed = vscConverterStation.getReactivePowerSetpoint();
        float vscVoltageSetpoint4Changed = vscConverterStation.getVoltageSetpoint();
        float svcPoint4Changed = staticVarCompensator.getReactivePowerSetPoint();
        float svcVoltagePoint4Changed = staticVarCompensator.getVoltageSetPoint();
        StaticVarCompensator.RegulationMode svcMode4Changed = staticVarCompensator.getRegulationMode();
        float terminalP4Changed = terminal.getP();
        float terminalQ4Changed = terminal.getQ();
        float dllP4Changed = danglingLine.getP0();
        float dllQ4Changed = danglingLine.getQ0();
        float hvdcPowerSetPoint4Changed = hvdcLine.getActivePowerSetpoint();
        HvdcLine.ConvertersMode hvdcMode4Changed = hvdcLine.getConvertersMode();

        // allocate s4 to s22
        fictitiousNetworkStateManager.cloneState("s4", "s22");
        eurosTagNetworkStateManager.cloneState("s4", "s22");

        // change working state to s22
        fictitiousNetworkStateManager.setWorkingState("s22");
        eurosTagNetworkStateManager.setWorkingState("s22");
        float phaseTapChangerV22 = phaseTapChanger.getRegulationValue();
        PhaseTapChanger.RegulationMode pTapChangerMode22 = phaseTapChanger.getRegulationMode();
        float ratioTapChangerV22 = ratioTapChanger.getTargetV();
        float generatorP22 = generator.getTargetP();
        float generatorQ22 = generator.getTargetQ();
        float generatorV22 = generator.getTargetV();
        float loadP22 = load.getP0();
        float loadQ22 = load.getQ0();
        int shuntSectionCount22 = shuntCompensator.getCurrentSectionCount();
        float vscPoint22 = vscConverterStation.getReactivePowerSetpoint();
        float vscVoltageSetpoint22 = vscConverterStation.getVoltageSetpoint();
        float svcPoint22 = staticVarCompensator.getReactivePowerSetPoint();
        float svcVoltagePoint22 = staticVarCompensator.getVoltageSetPoint();
        StaticVarCompensator.RegulationMode svcMode22 = staticVarCompensator.getRegulationMode();
        float terminalP22 = terminal.getP();
        float terminalQ22 = terminal.getQ();
        float dllP22 = danglingLine.getP0();
        float dllQ22 = danglingLine.getQ0();
        float hvdcPowerSetPoint22 = hvdcLine.getActivePowerSetpoint();
        HvdcLine.ConvertersMode hvdcMode22 = hvdcLine.getConvertersMode();

        // check cloned by allocate
        assertEquals(phaseTapChangerV4Changed, phaseTapChangerV22, 0.0f);
        assertEquals(pTapChangerMode4Changed, pTapChangerMode22);
        assertEquals(ratioTapChangerV4Changed, ratioTapChangerV22, 0.0f);
        assertEquals(generatorP4Changed, generatorP22, 0.0f);
        assertEquals(generatorQ4Changed, generatorQ22, 0.0f);
        assertEquals(generatorV4Changed, generatorV22, 0.0f);
        assertEquals(loadP4Changed, loadP22, 0.0f);
        assertEquals(loadQ4Changed, loadQ22, 0.0f);
        assertEquals(shuntSectionCount4Changed, shuntSectionCount22);
        assertEquals(vscPoint4Changed, vscPoint22, 0.0f);
        assertEquals(vscVoltageSetpoint4Changed, vscVoltageSetpoint22, 0.0f);
        assertFalse(vscConverterStation.isVoltageRegulatorOn());
        assertEquals(svcPoint4Changed, svcPoint22, 0.0f);
        assertEquals(svcVoltagePoint4Changed, svcVoltagePoint22, 0.0f);
        assertEquals(svcMode4Changed, svcMode22);
        assertEquals(terminalP4Changed, terminalP22, 0.0f);
        assertEquals(terminalQ4Changed, terminalQ22, 0.0f);
        assertTrue(sw.isOpen());
        assertEquals(dllP4Changed, dllP22, 0.0f);
        assertEquals(dllQ4Changed, dllQ22, 0.0f);
        assertEquals(hvdcPowerSetPoint4Changed, hvdcPowerSetPoint22, 0.0f);
        assertEquals(hvdcMode4Changed, hvdcMode22);

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
        voltageLevel.newShunt()
                .setName("shunt")
                .setId("sc")
                .setConnectableBus("NHV1")
                .setbPerSection(1.0f)
                .setCurrentSectionCount(3)
                .setMaximumSectionCount(10)
                .add();
        network.getSubstation("P1").newThreeWindingsTransformer()
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
        voltageLevel.newVscConverterStation()
                .setId("vsc")
                .setName("vsc")
                .setReactivePowerSetpoint(1.0f)
                .setVoltageRegulatorOn(true)
                .setVoltageSetpoint(2.0f)
                .setLossFactor(1.0f)
                .setBus("NHV1")
                .setConnectableBus("NHV1")
                .add();
        voltageLevel.newStaticVarCompensator()
                .setId("svc")
                .setName("svc")
                .setBmax(50.0f)
                .setBmin(20.0f)
                .setReactivePowerSetPoint(2.0f)
                .setRegulationMode(StaticVarCompensator.RegulationMode.OFF)
                .setBus("NHV1")
                .setConnectableBus("NHV1")
                .add();
        voltageLevel.newVscConverterStation()
                .setId("vsc2")
                .setName("vsc2")
                .setReactivePowerSetpoint(1.0f)
                .setVoltageRegulatorOn(true)
                .setVoltageSetpoint(2.0f)
                .setLossFactor(1.0f)
                .setBus("NHV1")
                .setConnectableBus("NHV1")
                .add();
        network.newHvdcLine()
                .setId("hvdcLine")
                .setName("hvdc")
                .setConverterStationId1("vsc")
                .setConverterStationId2("vsc2")
                .setActivePowerSetpoint(100.0f)
                .setConvertersMode(HvdcLine.ConvertersMode.SIDE_1_INVERTER_SIDE_2_RECTIFIER)
                .setR(1)
                .setNominalV(400)
                .setMaxP(300)
                .add();
        return network;
    }

}
