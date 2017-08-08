/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.iidm.network.impl;

import eu.itesla_project.iidm.network.*;
import org.junit.Test;

import static org.junit.Assert.*;

public class BusTest {

    @Test
    public void testSetterGetter() {
        Network network = NetworkFactory.create("test", "test");
        Substation substation = network.newSubstation()
                                    .setCountry(Country.AF)
                                    .setTso("tso")
                                    .setName("sub")
                                    .setId("subId")
                                .add();
        VoltageLevel voltageLevel = substation.newVoltageLevel()
                                        .setTopologyKind(TopologyKind.BUS_BREAKER)
                                        .setId("bbVL")
                                        .setName("bbVL_name")
                                        .setNominalV(200.0f)
                                    .add();
        // ConfiguredBus
        Bus bus = voltageLevel.getBusBreakerView()
                    .newBus()
                    .setName("bus1")
                    .setId("bus1")
                .add();
        LccConverterStation lccConverterStation = voltageLevel.newLccConverterStation()
                                                    .setId("lcc")
                                                    .setName("lcc")
                                                    .setBus("bus1")
                                                    .setLossFactor(0.011f)
                                                    .setPowerFactor(0.5f)
                                                    .setConnectableBus("bus1")
                                                .add();
        VscConverterStation vscConverterStation = voltageLevel.newVscConverterStation()
                                                    .setId("vsc")
                                                    .setName("vsc")
                                                    .setBus("bus1")
                                                    .setLossFactor(0.011f)
                                                    .setVoltageRegulatorOn(false)
                                                    .setReactivePowerSetpoint(1.0f)
                                                    .setConnectableBus("bus1")
                                                .add();
        assertEquals(HvdcConverterStation.HvdcType.LCC, lccConverterStation.getHvdcType());
        assertEquals(HvdcConverterStation.HvdcType.VSC, vscConverterStation.getHvdcType());
        float p1 = 1.0f;
        float q1 = 2.0f;
        float p2 = 10.0f;
        float q2 = 20.0f;
        lccConverterStation.getTerminal().setP(p1);
        lccConverterStation.getTerminal().setQ(q1);
        vscConverterStation.getTerminal().setP(p2);
        vscConverterStation.getTerminal().setQ(q2);

        assertSame(voltageLevel, bus.getVoltageLevel());
        try {
            bus.setV(-1.0f);
            fail();
        } catch (ValidationException ignored) {
        }
        bus.setV(200.0f);
        assertEquals(200.0f, bus.getV(), 0.0f);
        bus.setAngle(30.0f);
        assertEquals(30.0f, bus.getAngle(), 0.0f);

        assertEquals(p1 + p2, bus.getP(), 0.0f);
        assertEquals(q1 + q2, bus.getQ(), 0.0f);
    }
}