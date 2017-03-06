/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.security.json;

import java.io.IOException;
import java.util.Objects;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import eu.itesla_project.iidm.network.Identifiable;
import eu.itesla_project.iidm.network.Network;

/**
 *
 * @author Massimo Ferraro <massimo.ferraro@techrain.it>
 */
public class IdentifiableDeserializer  extends StdDeserializer<Identifiable> {
    
    Network network;
    
    public IdentifiableDeserializer(Network network) {
        this(null, network);
    }
   
    public IdentifiableDeserializer(Class<Identifiable> t, Network network) {
        super(t);
        this.network = Objects.requireNonNull(network);
    }

    @Override
    public Identifiable deserialize(JsonParser parser, DeserializationContext ctx) throws IOException, JsonProcessingException {
        JsonNode node = parser.getCodec().readTree(parser);
        String identifiableId = node.textValue();
        Identifiable<?> identifiable = network.getIdentifiable(identifiableId);
        if ( identifiable == null ) {
            throw new RuntimeException(identifiableId + " not found in network " + network.getId());
        }
        return identifiable;
    }

}
