package eu.itesla_project.iidm.network.impl;

import eu.itesla_project.iidm.network.*;
import eu.itesla_project.iidm.network.test.FictitiousSwitchFactory;
import org.junit.Test;

public class NotifyTest {

    @Test
    public void testNotify() {
        Network network = FictitiousSwitchFactory.create();
        network.addListener(new MockNetworkListenerImpl());
        TwoWindingsTransformer twoWindingsTransformer = network.getTwoWindingsTransformer("CI");
        PhaseTapChanger phaseTapChanger = twoWindingsTransformer.getPhaseTapChanger();
        phaseTapChanger.setRegulationValue(1.0f);

    }

    private static class MockNetworkListenerImpl implements NetworkListener {

        @Override
        public void onCreation(Identifiable identifiable) {
            System.out.println(identifiable + " created");
        }

        @Override
        public void onRemoval(Identifiable identifiable) {
            System.out.println(identifiable + " removed");
        }

        @Override
        public void onUpdate(Identifiable identifiable, String attribute, Object oldValue, Object newValue) {
            System.out.println(identifiable + "_" + attribute + ":" + newValue + "(" + oldValue + ")");
        }
    }
}
