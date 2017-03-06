/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.security.json;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import eu.itesla_project.contingency.BranchContingency;
import eu.itesla_project.contingency.Contingency;
import eu.itesla_project.contingency.ContingencyElement;
import eu.itesla_project.contingency.ContingencyElementType;
import eu.itesla_project.contingency.ContingencyImpl;
import eu.itesla_project.contingency.GeneratorContingency;

/**
 *
 * @author Massimo Ferraro <massimo.ferraro@techrain.it>
 */
public class ContingencyDeserializer extends StdDeserializer<Contingency> {
    
    public ContingencyDeserializer() {
        this(null);
    }
   
    public ContingencyDeserializer(Class<Contingency> t) {
        super(t);
    }

    @Override
    public Contingency deserialize(JsonParser parser, DeserializationContext ctx) throws IOException, JsonProcessingException {
        JsonNode node = parser.getCodec().readTree(parser);
        String id = node.get("id").asText();
        List<ContingencyElement> elements = new ArrayList<>();
        node.get("elements").forEach( elementNode -> 
        {
            switch (ContingencyElementType.valueOf(elementNode.get("type").asText())) {
                case BRANCH:
                case LINE:
                    elements.add(new BranchContingency(elementNode.get("id").asText(), 
                                                       elementNode.get("substationId").isNull() ? null : elementNode.get("substationId").asText()));
                    break;
                case GENERATOR: 
                    elements.add(new GeneratorContingency(elementNode.get("id").asText()));
                    break;
        }
        });
        return new ContingencyImpl(id, elements);
    }

}
