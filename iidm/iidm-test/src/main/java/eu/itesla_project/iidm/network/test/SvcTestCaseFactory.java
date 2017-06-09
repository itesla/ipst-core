/**
 * Copyright (c) 2016, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.iidm.network.test;

import eu.itesla_project.iidm.network.*;
import org.joda.time.DateTime;

/**
 * A very small network to test SVC modeling. 2 buses B1 and B2. A generator G1 regulating voltage is connected to B1.
 * B1 and B2 are connected by a line with a high reactance to cause an important voltage drop.
 * A SVC is connected to B2 to compensate the voltage drop.
 *
 *     G1                L2
 *     |                 |
 *     B1 ---------------B2
 *                       |
 *                       SVC2
 *
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public class SvcTestCaseFactory {

    public static Network create() {
        Network network = NetworkFactory.create("svcTestCase", "code");
        network.setCaseDate(DateTime.parse("2016-06-29T14:54:03.427+02:00"));
        Substation s1 = network.newSubstation()
                .setId("S1")
                .setCountry(Country.FR)
                .add();
        VoltageLevel vl1 = s1.newVoltageLevel()
                .setId("VL1")
                .setNominalV(380f)
                .setTopologyKind(TopologyKind.BUS_BREAKER)
                .add();
        vl1.getBusBreakerView().newBus()
                .setId("B1")
                .add();
        vl1.newGenerator()
                .setId("G1")
                .setConnectableBus("B1")
                .setBus("B1")
                .setVoltageRegulatorOn(true)
                .setTargetP(100f)
                .setTargetV(400f)
                .setMinP(50)
                .setMaxP(150)
                .add();
        Substation s2 = network.newSubstation()
                .setId("S2")
                .setCountry(Country.FR)
                .add();
        VoltageLevel vl2 = s2.newVoltageLevel()
                .setId("VL2")
                .setNominalV(380f)
                .setTopologyKind(TopologyKind.BUS_BREAKER)
                .add();
        vl2.getBusBreakerView().newBus()
                .setId("B2")
                .add();
        vl2.newLoad()
                .setId("L2")
                .setConnectableBus("B2")
                .setBus("B2")
                .setP0(100f)
                .setQ0(50f)
                .add();
        vl2.newStaticVarCompensator()
                .setId("SVC2")
                .setConnectableBus("B2")
                .setBus("B2")
                .setBmin(0.0002f)
                .setBmax(0.0008f)
                .setRegulationMode(StaticVarCompensator.RegulationMode.VOLTAGE)
                .setVoltageSetPoint(390f)
                .add();
        network.newLine()
                .setId("L1")
                .setVoltageLevel1("VL1")
                .setConnectableBus1("B1")
                .setBus1("B1")
                .setVoltageLevel2("VL2")
                .setConnectableBus2("B2")
                .setBus2("B2")
                .setR(4f)
                .setX(200f)
                .setG1(0f)
                .setB1(0f)
                .setG2(0f)
                .setB2(0f)
                .add();
        return network;
    }
}
