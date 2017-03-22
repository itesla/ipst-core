/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.security.json;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import eu.itesla_project.contingency.BranchContingency;
import eu.itesla_project.contingency.Contingency;
import eu.itesla_project.contingency.ContingencyElement;
import eu.itesla_project.contingency.ContingencyImpl;
import eu.itesla_project.contingency.GeneratorContingency;
import eu.itesla_project.iidm.network.Country;
import eu.itesla_project.iidm.network.Generator;
import eu.itesla_project.iidm.network.Line;
import eu.itesla_project.iidm.network.Network;
import eu.itesla_project.iidm.network.test.EurostagTutorialExample1Factory;
import eu.itesla_project.security.LimitViolation;
import eu.itesla_project.security.LimitViolationType;
import eu.itesla_project.security.PostContingencyResult;
import eu.itesla_project.security.PreContingencyResult;
import eu.itesla_project.security.SecurityAnalysisResult;

/**
 *
 * @author Massimo Ferraro <massimo.ferraro@techrain.it>
 */
public class JsonConverterTest {
    
    private Network network;
    private SecurityAnalysisResult result;
    private String jsonResult;
    private String ppJsonResult;
    
    @Before
    public void setUp() throws JsonParseException, JsonMappingException, IOException {
        network = EurostagTutorialExample1Factory.create();
        Line line1 = network.getLine("NHV1_NHV2_1");
        Line line2 = network.getLine("NHV1_NHV2_2");
        Generator generator = network.getGenerator("GEN");
        
        LimitViolation violation1 = new LimitViolation(line1.getId(), LimitViolationType.CURRENT, 100f, "limit", .95f, 110f, Country.FR, 380f);
        LimitViolation violation2 = new LimitViolation(line2.getId(), LimitViolationType.CURRENT, 100f, null, 110f);
        
        List<ContingencyElement> elements = Arrays.asList(
                new BranchContingency(line2.getId(), line2.getTerminal1().getVoltageLevel().getSubstation().getId()),
                new BranchContingency(line1.getId()),
                new GeneratorContingency(generator.getId())
                );
        Contingency contingency = new ContingencyImpl("contingency", elements);
        
        PreContingencyResult preContingencyResult = new PreContingencyResult(true, Arrays.asList(violation1));        
        
        PostContingencyResult postContingencyResult = new PostContingencyResult(contingency, true, Arrays.asList(violation1, violation2), Arrays.asList("action1","action2"));
        
        result = new SecurityAnalysisResult(preContingencyResult, Arrays.asList(postContingencyResult));
        
        jsonResult = "{\"preContingencyResult\":{\"computationOk\":true,\"limitViolations\":[{\"subjectId\":\"NHV1_NHV2_1\",\"limitType\":\"CURRENT\"," + 
                     "\"limit\":100.0,\"limitName\":\"limit\",\"limitReduction\":0.95,\"value\":110.0,\"country\":\"FR\",\"baseVoltage\":380.0}]," + 
                     "\"actionsTaken\":[]},\"postContingencyResults\":[{\"contingency\":{\"id\":\"contingency\",\"elements\":[{\"id\":\"NHV1_NHV2_2\"," + 
                     "\"substationId\":\"P1\",\"type\":\"BRANCH\"},{\"id\":\"NHV1_NHV2_1\",\"substationId\":null,\"type\":\"BRANCH\"},{\"id\":\"GEN\"," + 
                     "\"type\":\"GENERATOR\"}]},\"computationOk\":true,\"limitViolations\":[{\"subjectId\":\"NHV1_NHV2_1\",\"limitType\":\"CURRENT\"," + 
                     "\"limit\":100.0,\"limitName\":\"limit\",\"limitReduction\":0.95,\"value\":110.0,\"country\":\"FR\",\"baseVoltage\":380.0}," + 
                     "{\"subjectId\":\"NHV1_NHV2_2\",\"limitType\":\"CURRENT\",\"limit\":100.0,\"limitName\":null,\"limitReduction\":1.0,\"value\":110.0," + 
                     "\"country\":null,\"baseVoltage\":\"NaN\"}],\"actionsTaken\":[\"action1\",\"action2\"]}]}";
        
        ObjectMapper mapper = new ObjectMapper();
        Object json = mapper.readValue(jsonResult, Object.class);
        ObjectWriter jsonWriter = mapper.writer(new DefaultPrettyPrinter());
        ppJsonResult = jsonWriter.writeValueAsString(json);
    }
       
    @Test
    public void testExportSecurityAnalysisResult() throws JsonGenerationException, JsonMappingException, IOException {        
        StringWriter resultWriter = new StringWriter();
        JsonConverter.exportSecurityAnalysisResult(result, resultWriter);
        assertEquals(ppJsonResult, resultWriter.toString());
    }
    
    @Test
    public void testImportSecurityAnalysisResult() throws JsonParseException, JsonMappingException, IOException {
        SecurityAnalysisResult importedResult = JsonConverter.importSecurityAnalysisResult(new StringReader(jsonResult), network);
        StringWriter resultWriter = new StringWriter();
        JsonConverter.exportSecurityAnalysisResult(importedResult, resultWriter);
        assertEquals(ppJsonResult, resultWriter.toString());
        
        importedResult = JsonConverter.importSecurityAnalysisResult(new StringReader(ppJsonResult), network);
        resultWriter = new StringWriter();
        JsonConverter.exportSecurityAnalysisResult(importedResult, resultWriter);
        assertEquals(ppJsonResult, resultWriter.toString());
    }

}
