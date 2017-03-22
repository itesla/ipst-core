/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.security.json;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStreamReader;
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
    
    private SecurityAnalysisResult result;
    private String jsonResult;
    
    @Before
    public void setUp() throws JsonParseException, JsonMappingException, IOException {
        LimitViolation violation1 = new LimitViolation("NHV1_NHV2_1", LimitViolationType.CURRENT, 100f, "limit", .95f, 110f, Country.FR, 380f);
        LimitViolation violation2 = new LimitViolation("NHV1_NHV2_2", LimitViolationType.CURRENT, 100f, null, 110f);
        
        List<ContingencyElement> elements = Arrays.asList(
                new BranchContingency("NHV1_NHV2_2", "P1"),
                new BranchContingency("NHV1_NHV2_1"),
                new GeneratorContingency("GEN")
                );
        Contingency contingency = new ContingencyImpl("contingency", elements);
        
        PreContingencyResult preContingencyResult = new PreContingencyResult(true, Arrays.asList(violation1));        
        
        PostContingencyResult postContingencyResult = new PostContingencyResult(contingency, true, Arrays.asList(violation1, violation2), Arrays.asList("action1","action2"));
        
        result = new SecurityAnalysisResult(preContingencyResult, Arrays.asList(postContingencyResult));
        
        ObjectMapper mapper = new ObjectMapper();
        Object json = mapper.readValue(getClass().getResourceAsStream("/SecurityAnalysisResult.json"), Object.class);
        ObjectWriter jsonWriter = mapper.writer(new DefaultPrettyPrinter());
        jsonResult = jsonWriter.writeValueAsString(json);
    }
       
    @Test
    public void testExportSecurityAnalysisResult() throws JsonGenerationException, JsonMappingException, IOException {        
        StringWriter resultWriter = new StringWriter();
        JsonConverter.exportSecurityAnalysisResult(result, resultWriter);
        assertEquals(jsonResult, resultWriter.toString());
    }
    
    @Test
    public void testImportSecurityAnalysisResult() throws JsonParseException, JsonMappingException, IOException {
        SecurityAnalysisResult importedResult = JsonConverter.importSecurityAnalysisResult(new InputStreamReader(getClass().getResourceAsStream("/SecurityAnalysisResult.json")));
        StringWriter resultWriter = new StringWriter();
        JsonConverter.exportSecurityAnalysisResult(importedResult, resultWriter);
        assertEquals(jsonResult, resultWriter.toString());
        
        importedResult = JsonConverter.importSecurityAnalysisResult(new StringReader(jsonResult));
        resultWriter = new StringWriter();
        JsonConverter.exportSecurityAnalysisResult(importedResult, resultWriter);
        assertEquals(jsonResult, resultWriter.toString());
    }

}
