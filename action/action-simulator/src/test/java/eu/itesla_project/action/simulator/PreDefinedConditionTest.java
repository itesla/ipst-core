/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.action.simulator;

import eu.itesla_project.iidm.network.Network;
import org.junit.Test;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian@rte-france.com>
 */
public class PreDefinedConditionTest extends AbstractLoadFlowRulesEngineTest {
    @Override
    protected Network createNetwork() {
        Network network = EurostagTutorialExample1WithTemporaryLimitFactory.create();

        // overload the line
        network.getVoltageLevel("VLHV1").getBusBreakerView().getBus("NHV1").setV(380).setAngle(0);
        network.getLine("NHV1_NHV2_2").getTerminal1().setP(300).setQ(100);

        return network;
    }

    @Override
    protected String getDslFile() {
        return "/pre-defined-condition.groovy";
    }

    @Test
    public void test() {
        engine.start(actionDb);
    }
}
