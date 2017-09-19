/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.afs.storage.timeseries;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public interface ArrayChunk {

    int getOffset();

    int getLength();

    /**
     * Get estimated size in bytes.
     * @return estimated size in bytes
     */
    int getEstimatedSize();

    double getCompressionFactor();

    boolean isCompressed();

    UncompressedArrayChunk toUncompressed();

    Stream<Point> stream(TimeSeriesIndex index);

    List<ArrayChunk> split(int chunk);

    void writeJson(JsonGenerator generator) throws IOException;

    public static void writeJson(Map<String, Collection<ArrayChunk>> data, Path file) {
        try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            writeJson(data, writer);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void writeJson(Map<String, Collection<ArrayChunk>> data, BufferedWriter writer) throws IOException {
        JsonFactory factory = new JsonFactory();
        try (JsonGenerator generator = factory.createGenerator(writer)) {
            generator.useDefaultPrettyPrinter();
            generator.writeStartArray();
            for (Map.Entry<String, Collection<ArrayChunk>> e : data.entrySet()) {
                generator.writeStartObject();
                generator.writeStringField("name", e.getKey());
                generator.writeFieldName("chunks");
                generator.writeStartArray();
                for (ArrayChunk chunk : e.getValue()) {
                    chunk.writeJson(generator);
                }
                generator.writeEndArray();
                generator.writeEndObject();
            }
            generator.writeEndArray();
        }
    }
}
