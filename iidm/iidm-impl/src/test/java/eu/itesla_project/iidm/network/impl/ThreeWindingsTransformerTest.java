/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.iidm.network.impl;

import eu.itesla_project.iidm.network.*;
import eu.itesla_project.iidm.network.test.EurostagTutorialExample1Factory;
import org.junit.Test;

import static org.junit.Assert.*;

public class ThreeWindingsTransformerTest {
    @Test
    public void testSetterGetter() {
        String id3WindingsTransformer = "ABCD_ID";
        String name3WindingsTransformer = "XYZ_NAME";

        Network network = EurostagTutorialExample1Factory.create();
        Substation p1 = network.getSubstation("P1");
        assertNotNull(p1);

        ThreeWindingsTransformerAdder transfomerAdder = p1.newThreeWindingsTransformer();
        ThreeWindingsTransformer transformer = transfomerAdder
                .newLeg1().setR(1.3f).setX(1.4f).setRatedU(1.1f)
                .setG(1.6f).setB(1.7f)
                .setVoltageLevel("VLGEN").setConnectableBus("NGEN").add()
                .newLeg2().setR(2.03f).setX(2.04f).setRatedU(2.05f)
                .setVoltageLevel("VLGEN").setConnectableBus("NGEN").add()
                .newLeg3().setR(3.3f).setX(3.4f).setRatedU(3.5f)
                .setVoltageLevel("VLGEN").setConnectableBus("NGEN").add()
                .setId(id3WindingsTransformer)
                .setName(name3WindingsTransformer).add();
        assertEquals(id3WindingsTransformer, transformer.getId());
        assertEquals(name3WindingsTransformer, transformer.getName());
        assertEquals(p1, transformer.getSubstation());
        assertEquals(ConnectableType.THREE_WINDINGS_TRANSFORMER, transformer.getType());

        // leg1 getter
        ThreeWindingsTransformer.Leg1 leg1 = transformer.getLeg1();
        assertEquals(1.3f, leg1.getR(), 0.0f);
        assertEquals(1.4f, leg1.getX(), 0.0f);
        assertEquals(1.1f, leg1.getRatedU(), 0.0f);
        assertEquals(1.6f, leg1.getG(), 0.0f);
        assertEquals(1.7f, leg1.getB(), 0.0f);

        // leg2/3 getter
        ThreeWindingsTransformer.Leg2or3 leg2 = transformer.getLeg2();
        ThreeWindingsTransformer.Leg2or3 leg3 = transformer.getLeg3();
        assertEquals(2.03f, leg2.getR(), 0.0f);
        assertEquals(2.04f, leg2.getX(), 0.0f);
        assertEquals(2.05f, leg2.getRatedU(), 0.0f);
        assertEquals(3.3f, leg3.getR(), 0.0f);
        assertEquals(3.4f, leg3.getX(), 0.0f);
        assertEquals(3.5f, leg3.getRatedU(), 0.0f);

        ThreeWindingsTransformerImpl.Leg1Impl t = new ThreeWindingsTransformerImpl.Leg1Impl(0.1f, 0.2f, 0.3f, 4f, 4f);
        t.setR(2.1f);
        assertEquals(2.1f, t.getR(), 0.0f);
        t.setX(3.1f);
        assertEquals(3.1f, t.getX(), 0.0f);
        t.setRatedU(4.1f);
        assertEquals(4.1f, t.getRatedU(),0.0f);
        t.setG(1.3f);
        assertEquals(1.3f, t.getG(), 0.0f);
        t.setB(1.4f);
        assertEquals(1.4f, t.getB(), 0.0f);
    }
}