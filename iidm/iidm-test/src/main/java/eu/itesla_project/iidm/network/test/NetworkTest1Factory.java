/**
 * Copyright (c) 2016, All partners of the iTesla project (http://www.itesla-project.eu/consortium)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.iidm.network.test;

import eu.itesla_project.iidm.network.Network;
import eu.itesla_project.iidm.network.Switch;
import eu.itesla_project.iidm.network.TopologyKind;
import eu.itesla_project.iidm.network.Load;
import eu.itesla_project.iidm.network.Country;
import eu.itesla_project.iidm.network.Substation;
import eu.itesla_project.iidm.network.NetworkFactory;
import eu.itesla_project.iidm.network.BusbarSection;
import eu.itesla_project.iidm.network.VoltageLevel;
import eu.itesla_project.iidm.network.Generator;
import eu.itesla_project.iidm.network.EnergySource;

/**
 *
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public final class NetworkTest1Factory {

    private NetworkTest1Factory() {
    }

    public static Network create() {
        Network network = NetworkFactory.create("network1", "test");
        Substation substation1 = network.newSubstation()
                .setId("substation1")
                .setCountry(Country.FR)
                .setTso("TSO1")
                .setGeographicalTags("region1")
                .add();
        VoltageLevel voltageLevel1 = substation1.newVoltageLevel()
                .setId("voltageLevel1")
                .setNominalV(400)
                .setTopologyKind(TopologyKind.NODE_BREAKER)
                .add();
        VoltageLevel.NodeBreakerView topology1 = voltageLevel1.getNodeBreakerView();
        topology1.setNodeCount(10);
        BusbarSection voltageLevel1BusbarSection1 = topology1.newBusbarSection()
                .setId("voltageLevel1BusbarSection1")
                .setNode(0)
                .add();
        BusbarSection voltageLevel1BusbarSection2 = topology1.newBusbarSection()
                .setId("voltageLevel1BusbarSection2")
                .setNode(1)
                .add();
        Switch voltageLevel1Breaker1 = topology1.newBreaker()
                .setId("voltageLevel1Breaker1")
                .setRetained(true)
                .setOpen(false)
                .setNode1(voltageLevel1BusbarSection1.getTerminal().getNodeBreakerView().getNode())
                .setNode2(voltageLevel1BusbarSection2.getTerminal().getNodeBreakerView().getNode())
                .add();
        Load load1 = voltageLevel1.newLoad()
                .setId("load1")
                .setNode(2)
                .setP0(10)
                .setQ0(3)
                .add();
        Switch load1Disconnector1 = topology1.newDisconnector()
                .setId("load1Disconnector1")
                .setOpen(false)
                .setNode1(load1.getTerminal().getNodeBreakerView().getNode())
                .setNode2(3)
                .add();
        Switch load1Breaker1 = topology1.newDisconnector()
                .setId("load1Breaker1")
                .setOpen(false)
                .setNode1(3)
                .setNode2(voltageLevel1BusbarSection1.getTerminal().getNodeBreakerView().getNode())
                .add();
        Generator generator1 = voltageLevel1.newGenerator()
                .setId("generator1")
                .setEnergySource(EnergySource.NUCLEAR)
                .setMinP(200)
                .setMaxP(900)
                .setVoltageRegulatorOn(true)
                .setTargetP(900)
                .setTargetV(380)
                .setNode(5)
                .add();
        generator1.newReactiveCapabilityCurve()
                .beginPoint().setP(200).setMinQ(300).setMaxQ(500).endPoint()
                .beginPoint().setP(900).setMinQ(300).setMaxQ(500).endPoint()
                .add();
        Switch generator1Disconnector1 = topology1.newDisconnector()
                .setId("generator1Disconnector1")
                .setOpen(false)
                .setNode1(generator1.getTerminal().getNodeBreakerView().getNode())
                .setNode2(6)
                .add();
        Switch generator1Breaker1 = topology1.newDisconnector()
                .setId("generator1Breaker1")
                .setOpen(false)
                .setNode1(6)
                .setNode2(voltageLevel1BusbarSection2.getTerminal().getNodeBreakerView().getNode())
                .add();
        return network;
    }

}
