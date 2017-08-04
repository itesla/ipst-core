/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.iidm.network.impl;

import eu.itesla_project.iidm.network.Network;
import eu.itesla_project.iidm.network.test.EurostagTutorialExample1Factory;
import org.junit.Test;

import static org.junit.Assert.*;

public class LineTest {

    @Test
    public void testSetterGetter() {
        LineImpl line = createLineImpl();
        float r = 10.0f;
        float x = 20.0f;
        float g1 = 30.0f;
        float g2 = 35.0f;
        float b1 = 40.0f;
        float b2 = 45.0f;
        float delta = 0.0f;
        line.setR(r);
        assertEquals(r, line.getR(), delta);
        line.setX(x);
        assertEquals(x, line.getX(), delta);
        line.setG1(g1);
        assertEquals(g1, line.getG1(), delta);
        line.setG2(g2);
        assertEquals(g2, line.getG2(), delta);
        line.setB1(b1);
        assertEquals(b1, line.getB1(), delta);
        line.setB2(b2);
        assertEquals(b2, line.getB2(), delta);
    }

    private LineImpl createLineImpl() {
        Network network = EurostagTutorialExample1Factory.create();
        LineImpl lineImpl = (LineImpl) network.getLine("NHV1_NHV2_1");
        return lineImpl;
    }

    @Test
    public void testTieLineSetterGetter() {
        Network network = EurostagTutorialExample1Factory.create();
        TieLineAdderImpl tieLineAdder = new TieLineAdderImpl((NetworkImpl) network);
        float r = 10.0f;
        float r2 = 1.0f;
        float x = 20.0f;
        float x2 = 2.0f;
        float hl1g1 = 30.0f;
        float hl1g2 = 35.0f;
        float hl1b1 = 40.0f;
        float hl1b2 = 45.0f;
        float hl2g1 = 130.0f;
        float hl2g2 = 135.0f;
        float hl2b1 = 140.0f;
        float hl2b2 = 145.0f;
        float xnodeP = 50.0f;
        float xnodeQ = 60.0f;
        float delta = 0.0f;
        TieLineImpl tieLine = tieLineAdder.setId("testTie")
                            .setName("testNameTie")
                            .setVoltageLevel1("VLHV1")
                            .setBus1("NHV1")
                            .setConnectableBus1("NHV1")
                            .setVoltageLevel2("VLHV2")
                            .setBus2("NHV2")
                            .setConnectableBus2("NHV2")
                            .setUcteXnodeCode("ucte")
                            .line1()
                                .setId("hl1")
                                .setName("half1_name")
                                .setR(r)
                                .setX(x)
                                .setB1(hl1b1)
                                .setB2(hl1b2)
                                .setG1(hl1g1)
                                .setG2(hl1g2)
                                .setXnodeQ(xnodeQ)
                                .setXnodeP(xnodeP)
                            .line2()
                                .setId("hl2")
                                .setR(r2)
                                .setX(x2)
                                .setB1(hl2b1)
                                .setB2(hl2b2)
                                .setG1(hl2g1)
                                .setG2(hl2g2)
                                .setXnodeP(xnodeP)
                                .setXnodeQ(xnodeQ)
                            .add();
        assertTrue(tieLine.isTieLine());
        assertEquals("ucte", tieLine.getUcteXnodeCode());
        assertEquals("half1_name", tieLine.getHalf1().getName());
        assertEquals("hl2", tieLine.getHalf2().getId());
        assertEquals(r + r2, tieLine.getR(), delta);
        assertEquals(x + x2, tieLine.getX(), delta);
        assertEquals(hl1g1 + hl2g1, tieLine.getG1(), delta);
        assertEquals(hl1g2 + hl2g2, tieLine.getG2(), delta);
        assertEquals(hl1b1 + hl2b1, tieLine.getB1(), delta);
        assertEquals(hl1b2 + hl2b2, tieLine.getB2(), delta);

        boolean thrownBySetR = false;
        try {
            tieLine.setR(1.0f);
        } catch (ValidationException e) {
            thrownBySetR = true;
        }
        assertTrue(thrownBySetR);

        boolean thrownBySetX = false;
        try {
            tieLine.setX(1.0f);
        } catch (ValidationException e) {
            thrownBySetX = true;
        }
        assertTrue(thrownBySetX);

        boolean thrownBySetB1 = false;
        try {
            tieLine.setB1(1.0f);
        } catch (ValidationException e) {
            thrownBySetB1 = true;
        }
        assertTrue(thrownBySetB1);

        boolean thrownBySetB2 = false;
        try {
            tieLine.setB2(1.0f);
        } catch (ValidationException e) {
            thrownBySetB2 = true;
        }
        assertTrue(thrownBySetB2);

        boolean thrownBySetG1 = false;
        try {
            tieLine.setG1(1.0f);
        } catch (ValidationException e) {
            thrownBySetG1 = true;
        }
        assertTrue(thrownBySetG1);

        boolean thrownBySetG2 = false;
        try {
            tieLine.setG2(1.0f);
        } catch (ValidationException e) {
            thrownBySetG2 = true;
        }
        assertTrue(thrownBySetG2);

        TieLineImpl.HalfLineImpl h1 = tieLine.getHalf1();
        assertEquals(xnodeP, h1.getXnodeP(), delta);
        assertEquals(xnodeQ, h1.getXnodeQ(), delta);
    }
}