package eu.itesla_project.action.simulator;

import eu.itesla_project.iidm.network.Network;
import org.junit.Test;

public class PreDefinedMethodsTest extends AbstractLoadFlowRulesEngineTest {
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
        return "/transform_in_methods.groovy";
    }

    @Test
    public void test() throws Exception {
        engine.start(actionDb);
    }
}
