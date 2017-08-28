/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.iidm.network.impl;

import eu.itesla_project.commons.ITeslaException;
import eu.itesla_project.iidm.network.*;
import eu.itesla_project.iidm.network.test.FictitiousSwitchFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

public class LoadTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    Network network;
    VoltageLevel voltageLevel;

    @Before
    public void initNetwork() {
        network = FictitiousSwitchFactory.create();
        voltageLevel = network.getVoltageLevel("C");
    }

    @Test
    public void testSetterGetter() {
        Load load = network.getLoad("CE");
        load.setP0(-1.0f);
        assertEquals(-1.0f, load.getP0(), 0.0f);
        load.setQ0(-2.0f);
        assertEquals(-2.0f, load.getQ0(), 0.0f);
        load.setP0(1.0f);
        assertEquals(1.0f, load.getP0(), 0.0f);
        load.setQ0(0.0f);
        assertEquals(0.0f, load.getQ0(), 0.0f);
        load.setLoadType(LoadType.AUXILIARY);
        assertEquals(LoadType.AUXILIARY, load.getLoadType());
    }

    @Test
    public void invalidP0() {
        thrown.expect(ValidationException.class);
        thrown.expectMessage("p0 is invalid");
        createLoad("invalid", Float.NaN, 1.0f);
    }

    @Test
    public void invalidQ0() {
        thrown.expect(ValidationException.class);
        thrown.expectMessage("q0 is invalid");
        createLoad("invalid", 20.0f, Float.NaN);
    }

    @Test
    public void duplicateEquipment() {
        voltageLevel.newLoad()
                        .setId("duplicate")
                        .setP0(2.0f)
                        .setQ0(1.0f)
                        .setNode(1)
                    .add();
        thrown.expect(ITeslaException.class);
        thrown.expectMessage("with the id 'duplicate'");
        createLoad("duplicate", 2.0f, 1.0f);
    }

    @Test
    public void duplicateId() {
        // "C" id of voltageLevel
        thrown.expect(ITeslaException.class);
        thrown.expectMessage("with the id 'C'");
        createLoad("C", 2.0f, 1.0f);
    }

    @Test
    public void testAdder() {
        Load load = voltageLevel.newLoad()
                        .setId("testAdder")
                        .setP0(2.0f)
                        .setQ0(1.0f)
                        .setLoadType(LoadType.AUXILIARY)
                        .setNode(1)
                    .add();
        assertEquals(2.0f, load.getP0(), 0.0f);
        assertEquals(1.0f, load.getQ0(), 0.0f);
        assertEquals("testAdder", load.getId());
        assertEquals(LoadType.AUXILIARY, load.getLoadType());
    }

    @Test
    public void testRemove() {
        createLoad("toRemove", 2.0f, 1.0f);
        Load load = network.getLoad("toRemove");
        int loadCounts = network.getLoadCount();
        assertNotNull(load);
        load.remove();
        assertNotNull(load);
        assertNull(network.getLoad("toRemove"));
        assertEquals(loadCounts - 1, network.getLoadCount());
    }

    private void createLoad(String id, float p0, float q0) {
        voltageLevel.newLoad()
                        .setId(id)
                        .setP0(p0)
                        .setQ0(q0)
                        .setNode(1)
                    .add();
    }

}
