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
import org.junit.Test;

import static org.junit.Assert.*;

public class LoadTest {

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
    public void invalidArguments() {
        try {
            voltageLevel.newLoad().setId("invalid").setP0(Float.NaN).setQ0(1.0f).setNode(1).add();
            fail();
        } catch (ValidationException ignored) {
        }
        try {
            voltageLevel.newLoad().setId("invalid").setP0(2.0f).setQ0(Float.NaN).setNode(1).add();
            fail();
        } catch (ValidationException ignored) {
        }
    }

    @Test
    public void duplicateEquipment() {
        voltageLevel.newLoad().setId("duplicate").setP0(2.0f).setQ0(1.0f).setNode(1).add();
        try {
            voltageLevel.newLoad().setId("duplicate").setP0(2.0f).setQ0(1.0f).setNode(1).add();
        } catch (ITeslaException ignored) {
        }
        try {
            voltageLevel.newLoad().setId("duplicate").setP0(2.0f).setQ0(1.0f).setNode(1).add();
        } catch (ITeslaException ignored) {
        }
        // "C" id of voltageLevel
        try {
            voltageLevel.newLoad().setId("C").setP0(2.0f).setQ0(1.0f).setNode(1).setConnectableBus("a").add();
            fail();
        } catch (ITeslaException ignored) {
        }
    }

    @Test
    public void testAdder() {
        Load load = voltageLevel.newLoad().setId("testAdder").setP0(2.0f).setQ0(1.0f).setLoadType(LoadType.AUXILIARY).setNode(1).add();
        assertEquals(2.0f, load.getP0(), 0.0f);
        assertEquals(1.0f, load.getQ0(), 0.0f);
        assertEquals("testAdder", load.getId());
        assertEquals(LoadType.AUXILIARY, load.getLoadType());
    }

    @Test
    public void testRemove() {
        Load load = voltageLevel.newLoad().setId("toRemove").setP0(2.0f).setQ0(1.0f).setLoadType(LoadType.AUXILIARY).setNode(1).add();
        int loadCounts = network.getLoadCount();
        load.remove();
        assertNull(network.getLoad("toRemove"));
        assertEquals(--loadCounts, network.getLoadCount());
    }

}
