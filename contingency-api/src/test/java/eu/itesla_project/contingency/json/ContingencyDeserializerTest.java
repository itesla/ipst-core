package eu.itesla_project.contingency.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import eu.itesla_project.contingency.Contingency;
import eu.itesla_project.contingency.ContingencyElementType;

public class ContingencyDeserializerTest {
    
    @Test
    public void test() throws JsonParseException, JsonMappingException, IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Contingency.class, new ContingencyDeserializer());
        objectMapper.registerModule(module);        
        Contingency contingency = objectMapper.readValue(getClass().getResourceAsStream("/contingency.json"), Contingency.class);
        
        assertEquals("contingency", contingency.getId());
        assertEquals(3, contingency.getElements().size());
        contingency.getElements().forEach( element -> 
        {
            assertTrue(Arrays.asList("NHV1_NHV2_2", "NHV1_NHV2_1", "GEN").contains(element.getId()));
            if ( "GEN".equals(element.getId()) ) {
                assertEquals(ContingencyElementType.GENERATOR, element.getType());
            } else {
                assertEquals(ContingencyElementType.BRANCH, element.getType());
            }
        });
    }

}
