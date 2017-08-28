/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.iidm.network.impl;

import eu.itesla_project.commons.ITeslaException;
import eu.itesla_project.iidm.network.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

public class DanglingLineTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private Network network;
    private VoltageLevel voltageLevel;

    @Before
    public void initNetwork() {
        network = NetworkFactory.create("test", "test");
        Substation substation = network.newSubstation().setId("sub").setCountry(Country.FR).setTso("frtso").add();
        voltageLevel = substation.newVoltageLevel()
                                    .setId("vl").setName("vl")
                                    .setNominalV(440.0f)
                                    .setHighVoltageLimit(400.0f)
                                    .setLowVoltageLimit(200.0f)
                                    .setTopologyKind(TopologyKind.BUS_BREAKER)
                                .add();
        voltageLevel.getBusBreakerView().newBus().setId("bus_vl").setName("bus_vl").add();
    }

    @Test
    public void baseTests() {
        float r = 10.0f;
        float x = 20.0f;
        float g = 30.0f;
        float b = 40.0f;
        float p0 = 50.0f;
        float q0 = 60.0f;
        String id = "danglingId";
        String name = "danlingName";
        String ucteXnodeCode = "code";
        DanglingLine danglingLine = voltageLevel.newDanglingLine()
                                                    .setId(id)
                                                    .setName(name)
                                                    .setR(r)
                                                    .setX(x)
                                                    .setG(g)
                                                    .setB(b)
                                                    .setP0(p0)
                                                    .setQ0(q0)
                                                    .setUcteXnodeCode(ucteXnodeCode)
                                                    .setBus("bus_vl")
                                                    .setConnectableBus("bus_vl")
                                                .add();
        // adder
        assertEquals(r, danglingLine.getR(), 0.0f);
        assertEquals(x, danglingLine.getX(), 0.0f);
        assertEquals(g, danglingLine.getG(), 0.0f);
        assertEquals(b, danglingLine.getB(), 0.0f);
        assertEquals(p0, danglingLine.getP0(), 0.0f);
        assertEquals(q0, danglingLine.getQ0(), 0.0f);
        assertEquals(id, danglingLine.getId());
        assertEquals(name, danglingLine.getName());
        assertEquals(ucteXnodeCode, danglingLine.getUcteXnodeCode());

        // setter getter
        float r2 = 11.0f;
        float x2 = 21.0f;
        float g2 = 31.0f;
        float b2 = 41.0f;
        float p02 = 51.0f;
        float q02 = 61.0f;
        danglingLine.setR(r2);
        assertEquals(r2, danglingLine.getR(), 0.0f);
        danglingLine.setX(x2);
        assertEquals(x2, danglingLine.getX(), 0.0f);
        danglingLine.setG(g2);
        assertEquals(g2, danglingLine.getG(), 0.0f);
        danglingLine.setB(b2);
        assertEquals(b2, danglingLine.getB(), 0.0f);
        danglingLine.setP0(p02);
        assertEquals(p02, danglingLine.getP0(), 0.0f);
        danglingLine.setQ0(q02);
        assertEquals(q02, danglingLine.getQ0(), 0.0f);

        danglingLine.newCurrentLimits().setPermanentLimit(100.0f).add();
        assertEquals(100.0f, danglingLine.getCurrentLimits().getPermanentLimit(), 0.0f);

        Bus bus = voltageLevel.getBusBreakerView().getBus("bus_vl");
        Bus terminal = danglingLine.getTerminal().getBusBreakerView().getBus();
        assertSame(bus, terminal);
    }

    @Test
    public void testInvalidR() {
        thrown.expect(ValidationException.class);
        thrown.expectMessage("r is invalid");
        createDanglingLine("invalid", "invalid", Float.NaN, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, "code");
    }

    @Test
    public void testInvalidX() {
        thrown.expect(ValidationException.class);
        thrown.expectMessage("x is invalid");
        createDanglingLine("invalid", "invalid", 1.0f, Float.NaN, 1.0f, 1.0f, 1.0f, 1.0f, "code");
    }

    @Test
    public void testInvalidG() {
        thrown.expect(ValidationException.class);
        thrown.expectMessage("g is invalid");
        createDanglingLine("invalid", "invalid", 1.0f, 1.0f, Float.NaN, 1.0f, 1.0f, 1.0f, "code");
    }

    @Test
    public void testInvalidB() {
        thrown.expect(ValidationException.class);
        thrown.expectMessage("b is invalid");
        createDanglingLine("invalid", "invalid", 1.0f, 1.0f, 1.0f, Float.NaN, 1.0f, 1.0f, "code");
    }

    @Test
    public void testInvalidP0() {
        thrown.expect(ValidationException.class);
        thrown.expectMessage("p0 is invalid");
        createDanglingLine("invalid", "invalid", 1.0f, 1.0f, 1.0f, 1.0f, Float.NaN, 1.0f, "code");
    }

    @Test
    public void testInvalidQ0() {
        thrown.expect(ValidationException.class);
        thrown.expectMessage("q0 is invalid");
        createDanglingLine("invalid", "invalid", 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, Float.NaN, "code");
    }

    @Test
    public void duplicateDanglingLine() {
        createDanglingLine("duplicate", "duplicate", 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, "code");
        assertNotNull(network.getDanglingLine("duplicate"));
        thrown.expect(ITeslaException.class);
        createDanglingLine("duplicate", "duplicate", 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, "code");
    }

    @Test
    public void testRemove() {
        createDanglingLine("toRemove", "toRemove", 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, "code");
        DanglingLine danglingLine = network.getDanglingLine("toRemove");
        int count = network.getDanglingLineCount();
        assertNotNull(danglingLine);
        danglingLine.remove();
        assertEquals(count - 1, network.getDanglingLineCount());
        assertNull(network.getDanglingLine("toRemove"));
        assertNotNull(danglingLine);
    }

    private void createDanglingLine(String id, String name, float r, float x, float g, float b,
                                    float p0, float q0, String ucteCode) {
        voltageLevel.newDanglingLine()
                        .setId(id)
                        .setName(name)
                        .setR(r)
                        .setX(x)
                        .setG(g)
                        .setB(b)
                        .setP0(p0)
                        .setQ0(q0)
                        .setUcteXnodeCode(ucteCode)
                        .setBus("bus_vl")
                        .setConnectableBus("bus_vl")
                    .add();
    }

}
