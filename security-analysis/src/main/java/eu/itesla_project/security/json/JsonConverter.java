/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.security.json;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Objects;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.module.SimpleModule;

import eu.itesla_project.contingency.Contingency;
import eu.itesla_project.iidm.network.Identifiable;
import eu.itesla_project.iidm.network.Network;
import eu.itesla_project.security.SecurityAnalysisResult;

/**
 *
 * @author Massimo Ferraro <massimo.ferraro@techrain.it>
 */
public final class JsonConverter {

    private JsonConverter() {
    }

    public static void exportSecurityAnalysisResult(SecurityAnalysisResult result, Writer writer) throws JsonGenerationException, JsonMappingException, IOException {
        Objects.requireNonNull(result);
        Objects.requireNonNull(writer);
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(Identifiable.class, new IdentifiableSerializer());
        mapper.registerModule(module);
        ObjectWriter jsonWriter = mapper.writer(new DefaultPrettyPrinter());
        jsonWriter.writeValue(writer, result);
    }

    public static SecurityAnalysisResult importSecurityAnalysisResult(Reader reader, Network network) throws JsonParseException, JsonMappingException, IOException {
        Objects.requireNonNull(reader);
        Objects.requireNonNull(network);
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Identifiable.class, new IdentifiableDeserializer(network));
        module.addDeserializer(Contingency.class, new ContingencyDeserializer());
        mapper.registerModule(module);
        return mapper.readValue(reader, SecurityAnalysisResult.class);
    }

}
