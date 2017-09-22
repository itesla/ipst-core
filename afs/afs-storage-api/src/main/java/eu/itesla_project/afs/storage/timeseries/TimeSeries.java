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
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public class TimeSeries {

    private final TimeSeriesMetadata metadata;

    private final List<ArrayChunk> chunks;

    public TimeSeries(TimeSeriesMetadata metadata, List<ArrayChunk> chunks) {
        this.metadata = Objects.requireNonNull(metadata);
        this.chunks = Objects.requireNonNull(chunks);
    }

    public TimeSeriesMetadata getMetadata() {
        return metadata;
    }

    public List<ArrayChunk> getChunks() {
        return chunks;
    }

    public Stream<Point> stream() {
        // check chunk consistency
        //   - all included in index range
        //   - no chunk overlap
        //   - no missing points
        List<ArrayChunk> sortedChunks = chunks.stream().sorted(Comparator.comparing(ArrayChunk::getOffset)).collect(Collectors.toList());
        int pointCount = metadata.getIndex().getPointCount();
        int i = 0;
//        for (ArrayChunk chunk : sortedChunks) {
//            if (chunk.getOffset() != i) {
//                throw new AfsStorageException("No value found in range [" + i + ", " + chunk.getOffset() + "]");
//            }
//            if (i + chunk.getLength() > pointCount - 1) {
//                throw new AfsStorageException("Value(s) found out of index range [" + (pointCount - 1) + ", " + (i + chunk.getLength()) + "]");
//            }
//            i += chunk.getLength();
//        }
//        if (i < pointCount - 1) {
//            throw new AfsStorageException("No value found in range [" + i + ", " + (pointCount - 1) + "]");
//        }

        return sortedChunks.stream().flatMap(chunk -> chunk.stream(metadata.getIndex()));
    }

    public double[] toArray() {
        double[] array = new double[metadata.getIndex().getPointCount()];
        Arrays.fill(array, Double.NaN);
        chunks.forEach(chunk -> chunk.fillArray(array));
        return array;
    }

    public void writeJson(JsonGenerator generator) throws IOException {
        generator.useDefaultPrettyPrinter();
        generator.writeStartObject();
        generator.writeStringField("timeSeriesName", metadata.getName());
        generator.writeFieldName("chunks");
        generator.writeStartArray();
        for (ArrayChunk chunk : chunks) {
            chunk.writeJson(generator);
        }
        generator.writeEndArray();
        generator.writeEndObject();
    }

    public void writeJson(BufferedWriter writer) throws IOException {
        JsonFactory factory = new JsonFactory();
        try (JsonGenerator generator = factory.createGenerator(writer)) {
            writeJson(generator);
        }
    }

    public void writeJson(Path file) {
        try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            writeJson(writer);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
