/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.action.dsl;

import eu.itesla_project.action.dsl.ast.EvaluationContext;
import eu.itesla_project.action.dsl.ast.ExpressionEvaluator;
import eu.itesla_project.action.dsl.ast.ExpressionNode;
import eu.itesla_project.action.dsl.ast.ExpressionPrinter;
import eu.itesla_project.contingency.Contingency;
import eu.itesla_project.iidm.network.Line;
import eu.itesla_project.iidm.network.Network;
import eu.itesla_project.iidm.network.test.EurostagTutorialExample1Factory;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian@rte-france.com>
 */
public class ConditionDslLoaderTest {

    private Network network;
    private Line line1;
    private Line line2;

    @Before
    public void setUp() throws Exception {
        network = EurostagTutorialExample1Factory.create();
        network.getVoltageLevel("VLHV1").getBusBreakerView().getBus("NHV1").setV(380).setAngle(0);
        network.getVoltageLevel("VLHV2").getBusBreakerView().getBus("NHV2").setV(380).setAngle(0);
        line1 = network.getLine("NHV1_NHV2_1");
        line2 = network.getLine("NHV1_NHV2_2");

    }

    private void loadAndAssert(String expected, String script) throws IOException {
        ExpressionNode node = (ExpressionNode) new ConditionDslLoader(script).load(network);
        assertNotNull(node);
        assertEquals(expected, ExpressionPrinter.toString(node));
    }

    private void evalAndAssert(Object expected, String script) throws IOException {
        ExpressionNode node = (ExpressionNode) new ConditionDslLoader(script).load(network);
        assertNotNull(node);
        assertEquals(expected, ExpressionEvaluator.evaluate(node, new EvaluationContext() {
            @Override
            public Network getNetwork() {
                return network;
            }

            @Override
            public Contingency getContingency() {
                throw new AssertionError();
            }

            @Override
            public boolean isActionTaken(String actionId) {
                throw new AssertionError();
            }
        }));
    }

    @Test
    public void testCondition() throws IOException {
        loadAndAssert("line('NHV1_NHV2_1')", "line('NHV1_NHV2_1')");
        loadAndAssert("line('NHV1_NHV2_1').terminal1.p", "line('NHV1_NHV2_1').terminal1.p");
        loadAndAssert("transformer('NGEN_NHV1')", "transformer('NGEN_NHV1')");
        loadAndAssert("load('LOAD')", "load('LOAD')");

        loadAndAssert("1", "1"); // integer
        loadAndAssert("1.0", "1f"); // float
        loadAndAssert("1.0", "1d"); // double
        loadAndAssert("1.0", "1.0"); // big decimal
        for (String op : Arrays.asList("+", "-", "*", "/", "==", "<", ">", ">=", "<=", "!=")) {
            // integer
            loadAndAssert("(line('NHV1_NHV2_1').terminal1.p " + op + " 1)", "line('NHV1_NHV2_1').terminal1.p " + op + " 1");
            loadAndAssert("(1 " + op + " line('NHV1_NHV2_1').terminal1.p)", "1 " + op + " line('NHV1_NHV2_1').terminal1.p");

            // float
            loadAndAssert("(line('NHV1_NHV2_1').terminal1.p " + op + " 1.0)", "line('NHV1_NHV2_1').terminal1.p " + op + " 1f");
            loadAndAssert("(1.0 " + op + " line('NHV1_NHV2_1').terminal1.p)", "1f " + op + " line('NHV1_NHV2_1').terminal1.p");

            // double
            loadAndAssert("(line('NHV1_NHV2_1').terminal1.p " + op + " 1.0)", "line('NHV1_NHV2_1').terminal1.p " + op + " 1d");
            loadAndAssert("(1.0 " + op + " line('NHV1_NHV2_1').terminal1.p)", "1d " + op + " line('NHV1_NHV2_1').terminal1.p");

            // big decimal
            loadAndAssert("(line('NHV1_NHV2_1').terminal1.p " + op + " 1.0)", "line('NHV1_NHV2_1').terminal1.p " + op + " 1.0");
            loadAndAssert("(1.0 " + op + " line('NHV1_NHV2_1').terminal1.p)", "1.0 " + op + " line('NHV1_NHV2_1').terminal1.p");
        }

        loadAndAssert("true", "true");
        loadAndAssert("false", "true && false");
        for (String op : Arrays.asList("&&", "||")) {
             loadAndAssert("(line('NHV1_NHV2_1').overloaded " + op + " true)", "line('NHV1_NHV2_1').overloaded " + op + " true");
             loadAndAssert("(true " + op + " line('NHV1_NHV2_1').overloaded)", "true " + op + " line('NHV1_NHV2_1').overloaded");
        }
        loadAndAssert("false", "!true");
        loadAndAssert("!(line('NHV1_NHV2_1').overloaded)", "!line('NHV1_NHV2_1').overloaded");

        loadAndAssert("actionTaken('action1')", "actionTaken('action1')");
        loadAndAssert("contingencyOccurred('contingency1')", "contingencyOccurred('contingency1')");
        loadAndAssert("contingencyOccurred()", "contingencyOccurred()");
    }

    @Test
    public void testNetworkAccess() throws IOException {
        // add temporary limits
        line1.newCurrentLimits1()
                .setPermanentLimit(400)
                .beginTemporaryLimit()
                .setName("20")
                .setAcceptableDuration(20 * 60)
                .setValue(800)
                .endTemporaryLimit()
                .add();

        // IIDM method call
        evalAndAssert(800f, "line('NHV1_NHV2_1').currentLimits1.getTemporaryLimitValue(1200)");
        evalAndAssert(false, "line('NHV1_NHV2_1').overloaded");
    }

    @Test
    public void testLoadingRank() throws IOException {
        // add temporary limits
        line1.newCurrentLimits1()
                .setPermanentLimit(400)
                .beginTemporaryLimit()
                    .setName("20")
                    .setAcceptableDuration(20 * 60)
                    .setValue(800)
                .endTemporaryLimit()
                .add();
        line2.newCurrentLimits1()
                .setPermanentLimit(400)
                .beginTemporaryLimit()
                    .setName("20")
                    .setAcceptableDuration(20 * 60)
                    .setValue(800)
                .endTemporaryLimit()
                .add();

        // both are 20' overloaded, but line2 is more overloaded than line1
        line1.getTerminal1().setP(300).setQ(100); // line1.i1 = 480
        line2.getTerminal1().setP(400).setQ(100); // line2.i1 = 626
        evalAndAssert(2, "loadingRank('NHV1_NHV2_1', ['NHV1_NHV2_1', 'NHV1_NHV2_2'])");
        evalAndAssert(1, "loadingRank('NHV1_NHV2_2', ['NHV1_NHV2_1', 'NHV1_NHV2_2'])");

        // both are 20' overloaded, but line1 is more overloaded than line2
        line1.getTerminal1().setP(400); // line1.i1 = 626
        line2.getTerminal1().setP(300); // line2.i1 = 480
        evalAndAssert(1, "loadingRank('NHV1_NHV2_1', ['NHV1_NHV2_1', 'NHV1_NHV2_2'])");
        evalAndAssert(2, "loadingRank('NHV1_NHV2_2', ['NHV1_NHV2_1', 'NHV1_NHV2_2'])");

        // none are overloaded, but line2 % of permanent limit is greater than line1 one
        line1.getTerminal1().setP(100); // line1.i1 = 214
        line2.getTerminal1().setP(150); // line2.i1 = 273
        evalAndAssert(2, "loadingRank('NHV1_NHV2_1', ['NHV1_NHV2_1', 'NHV1_NHV2_2'])");
        evalAndAssert(1, "loadingRank('NHV1_NHV2_2', ['NHV1_NHV2_1', 'NHV1_NHV2_2'])");

        // none are overloaded, but line1 % of permanent limit is greater than line2 one
        line1.getTerminal1().setP(150); // line1.i1 = 273
        line2.getTerminal1().setP(100); // line2.i1 = 214
        evalAndAssert(1, "loadingRank('NHV1_NHV2_1', ['NHV1_NHV2_1', 'NHV1_NHV2_2'])");
        evalAndAssert(2, "loadingRank('NHV1_NHV2_2', ['NHV1_NHV2_1', 'NHV1_NHV2_2'])");
    }

    @Test
    public void testLoadingRankWithDifferentAcceptableDuration() throws IOException {
        // add temporary limits
        line1.newCurrentLimits1()
                .setPermanentLimit(400)
                .beginTemporaryLimit()
                    .setName("20")
                    .setAcceptableDuration(20 * 60)
                    .setValue(800)
                .endTemporaryLimit()
                .add();
        line2.newCurrentLimits1()
                .setPermanentLimit(400)
                .beginTemporaryLimit()
                    .setName("20")
                    .setAcceptableDuration(20 * 60)
                    .setValue(600)
                .endTemporaryLimit()
                .beginTemporaryLimit()
                    .setName("5")
                    .setAcceptableDuration(5 * 60)
                    .setValue(800)
                .endTemporaryLimit()
                .add();

        // line1 current is greater than line2 one but acceptable duration of line2 is less than line1
        line1.getTerminal1().setP(410).setQ(100); // line1.i1 = 641
        line2.getTerminal1().setP(400).setQ(100); // line2.i1 = 626
        evalAndAssert(2, "loadingRank('NHV1_NHV2_1', ['NHV1_NHV2_1', 'NHV1_NHV2_2'])");
        evalAndAssert(1, "loadingRank('NHV1_NHV2_2', ['NHV1_NHV2_1', 'NHV1_NHV2_2'])");
    }

    @Test
    public void testLoadingRankWithUndefinedCurrentLimitsForLine2() throws IOException {
        // add temporary limits
        line1.newCurrentLimits1()
                .setPermanentLimit(400)
                .beginTemporaryLimit()
                    .setName("20")
                    .setAcceptableDuration(20 * 60)
                    .setValue(800)
                .endTemporaryLimit()
                .add();

        // line2 current is greater than line1 one but line2 has not temporary limits
        line1.getTerminal1().setP(400).setQ(100); // line1.i1 = 626
        line2.getTerminal1().setP(500).setQ(100); // line2.i1 = 774
        evalAndAssert(1, "loadingRank('NHV1_NHV2_1', ['NHV1_NHV2_1', 'NHV1_NHV2_2'])");
        evalAndAssert(2, "loadingRank('NHV1_NHV2_2', ['NHV1_NHV2_1', 'NHV1_NHV2_2'])");
    }

    @Test
    public void testLoadingRankWithCurrentLimitsAtBothSides() throws IOException {
        // add temporary limits
        line1.newCurrentLimits1()
                .setPermanentLimit(400)
                .beginTemporaryLimit()
                    .setName("20")
                    .setAcceptableDuration(20 * 60)
                    .setValue(800)
                .endTemporaryLimit()
                .add();
        line1.newCurrentLimits2()
                .setPermanentLimit(400)
                .beginTemporaryLimit()
                    .setName("20")
                    .setAcceptableDuration(20 * 60)
                    .setValue(600)
                .endTemporaryLimit()
                .beginTemporaryLimit()
                    .setName("1")
                    .setAcceptableDuration(1 * 60)
                    .setValue(800)
                .endTemporaryLimit()
                .add();
        line2.newCurrentLimits1()
                .setPermanentLimit(400)
                .beginTemporaryLimit()
                    .setName("20")
                    .setAcceptableDuration(20 * 60)
                    .setValue(800)
                .endTemporaryLimit()
                .add();

        // line2 current is greater than line2 one but acceptable duration of side 2 of line1 is less than line2 one
        line1.getTerminal1().setP(400).setQ(100); // line1.i1 = 626
        line1.getTerminal2().setP(400).setQ(100); // line1.i2 = 626
        line2.getTerminal1().setP(410).setQ(100); // line2.i1 = 641
        line2.getTerminal2().setP(410).setQ(100); // line2.i2 = 641
        evalAndAssert(1, "loadingRank('NHV1_NHV2_1', ['NHV1_NHV2_1', 'NHV1_NHV2_2'])");
        evalAndAssert(2, "loadingRank('NHV1_NHV2_2', ['NHV1_NHV2_1', 'NHV1_NHV2_2'])");
    }

    @Test
    public void testMostLoaded() throws IOException {
        // add temporary limits
        line1.newCurrentLimits1()
                .setPermanentLimit(400)
                .beginTemporaryLimit()
                .setName("20")
                .setAcceptableDuration(20 * 60)
                .setValue(800)
                .endTemporaryLimit()
                .add();
        line2.newCurrentLimits1()
                .setPermanentLimit(400)
                .beginTemporaryLimit()
                .setName("20")
                .setAcceptableDuration(20 * 60)
                .setValue(800)
                .endTemporaryLimit()
                .add();

        // line2 is more overloaded than line1
        line1.getTerminal1().setP(300).setQ(100); // line1.i1 = 480
        line2.getTerminal1().setP(400).setQ(100); // line2.i1 = 626
        evalAndAssert("NHV1_NHV2_2", "mostLoaded(['NHV1_NHV2_1', 'NHV1_NHV2_2'])");

        // line1 is more overloaded than line2
        line1.getTerminal1().setP(400).setQ(100); // line1.i1 = 626
        line2.getTerminal1().setP(300).setQ(100); // line2.i1 = 480
        evalAndAssert("NHV1_NHV2_1", "mostLoaded(['NHV1_NHV2_1', 'NHV1_NHV2_2'])");

        // combine with loadingRank
        evalAndAssert(1, "loadingRank(mostLoaded(['NHV1_NHV2_1', 'NHV1_NHV2_2']), ['NHV1_NHV2_1', 'NHV1_NHV2_2'])");
        evalAndAssert(1, "loadingRank('NHV1_NHV2_1', [mostLoaded(['NHV1_NHV2_1', 'NHV1_NHV2_2']), 'NHV1_NHV2_2'])");
    }
}