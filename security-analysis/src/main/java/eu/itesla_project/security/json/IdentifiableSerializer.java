/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.security.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import eu.itesla_project.iidm.network.Identifiable;

/**
 *
 * @author Massimo Ferraro <massimo.ferraro@techrain.it>
 */
public class IdentifiableSerializer  extends StdSerializer<Identifiable> {
    
    public IdentifiableSerializer() {
        this(null);
    }
   
    public IdentifiableSerializer(Class<Identifiable> t) {
        super(t);
    }

    @Override
    public void serialize(Identifiable identifiable, JsonGenerator generator, SerializerProvider provider) throws IOException, JsonGenerationException {
        generator.writeString(identifiable.getId());
    }

}
