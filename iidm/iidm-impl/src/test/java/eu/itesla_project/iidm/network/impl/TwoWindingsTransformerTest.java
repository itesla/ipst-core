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

public class TwoWindingsTransformerTest {
    @Test
    public void test() {
        Network network = EurostagTutorialExample1Factory.create();
        TwoWindingsTransformer tWT = network.getTwoWindingsTransformer("NGEN_NHV1");
        assertNotNull(tWT);
        float r = 0.5f;
        tWT.setR(r);
        assertEquals(r, tWT.getR(), 0.0f);
        float b = 1.0f;
        tWT.setB(b);
        assertEquals(b, tWT.getB(), 0.0f);
        float g = 2.0f;
        tWT.setG(g);
        assertEquals(g, tWT.getG(), 0.0f);
        float x = 4.0f;
        tWT.setX(x);
        assertEquals(x, tWT.getX(), 0.0f);
        float ratedU1 = 8.0f;
        tWT.setRatedU1(ratedU1);
        assertEquals(ratedU1, tWT.getRatedU1(), 0.0f);
        float ratedU2 = 16.0f;
        tWT.setRatedU2(ratedU2);
        assertEquals(ratedU2, tWT.getRatedU2(), 0.0f);
        assertEquals(ConnectableType.TWO_WINDINGS_TRANSFORMER, tWT.getType());
        Substation p1 = network.getSubstation("P1");
        assertEquals(p1, tWT.getSubstation());
    }
}