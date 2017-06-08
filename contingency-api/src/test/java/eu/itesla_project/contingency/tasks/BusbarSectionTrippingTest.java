/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.contingency.tasks;

import eu.itesla_project.commons.ITeslaException;
import eu.itesla_project.contingency.BusbarSectionContingency;
import eu.itesla_project.contingency.ContingencyImpl;
import eu.itesla_project.iidm.network.Network;
import eu.itesla_project.iidm.network.test.FictitiousSwitchFactory;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * @author Mathieu Bague <mathieu.bague@rte-france.com>
 */
public class BusbarSectionTrippingTest {

    @Test
    public void busbarSectionTrippingTest() throws IOException {
        busbarSectionTrippingTest("D", Arrays.asList("BD", "BL"));
        busbarSectionTrippingTest("O", Arrays.asList("BJ", "BT"));
        busbarSectionTrippingTest("P", Arrays.asList("BJ", "BL", "BV", "BX", "BZ"));
    }

    public void busbarSectionTrippingTest(String bbsId, List<String> switchIds) {
        Network network = FictitiousSwitchFactory.create();

        BusbarSectionContingency tripping = new BusbarSectionContingency(bbsId);
        ContingencyImpl contingency = new ContingencyImpl("contingency", tripping);

        ModificationTask task = contingency.toTask();
        task.modify(network, null);

        for (String id : switchIds) {
            assertTrue(network.getSwitch(id).isOpen());
        }
    }

    @Test(expected = ITeslaException.class)
    public void unknownBusbarSectionTrippingTest() {
        Network network = FictitiousSwitchFactory.create();

        BusbarSectionTripping tripping = new BusbarSectionTripping("bbs");
        tripping.modify(network, null);
    }
}
