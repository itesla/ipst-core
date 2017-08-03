package eu.itesla_project.iidm.network.impl;

import eu.itesla_project.iidm.network.HvdcLine;
import eu.itesla_project.iidm.network.Network;
import eu.itesla_project.iidm.network.test.HvdcTestNetwork;
import org.junit.Test;

import static org.junit.Assert.*;

public class HvdcLineImplTest {

    @Test
    public void testHvdcLineOneStateAttributes() {
        Network network = HvdcTestNetwork.createLcc();
        HvdcLine l = network.getHvdcLine("L");
        assertNotNull(l);
        assertEquals(1f, l.getR(), 0.0f);
        assertEquals(400.0f, l.getNominalV(), 0.0f);
        assertEquals(300f, l.getMaxP(), 0.0f);
        l.setR(2f);
        assertEquals(2f, l.getR(), 0.0f);
        l.setNominalV(220.0f);
        assertEquals(220.0f, l.getNominalV(), 0.0f);
        l.setMaxP(1.11f);
        assertEquals(1.11f, l.getMaxP(), 0.0f);
        l.setActivePowerSetpoint(421f);
        assertEquals(421f, l.getActivePowerSetpoint(), 0.0f);
        l.setConvertersMode(HvdcLine.ConvertersMode.SIDE_1_RECTIFIER_SIDE_2_INVERTER);
        assertEquals(HvdcLine.ConvertersMode.SIDE_1_RECTIFIER_SIDE_2_INVERTER, l.getConvertersMode());
    }
}