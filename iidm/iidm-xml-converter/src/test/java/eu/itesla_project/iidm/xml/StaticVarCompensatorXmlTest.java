/**
 * Copyright (c) 2016, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.iidm.xml;

import eu.itesla_project.commons.ConverterBaseTest;
import eu.itesla_project.iidm.network.test.SvcTestCaseFactory;
import org.junit.Test;

import java.io.IOException;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public class StaticVarCompensatorXmlTest extends ConverterBaseTest {

    @Test
    public void roundTripTest() throws IOException {
        roundTripXmlTest(SvcTestCaseFactory.create(),
                         NetworkXml::writeAndValidate,
                         NetworkXml::read,
                         "/staticVarCompensatorRoundTripRef.xml");
    }
}
