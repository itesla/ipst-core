/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.afs.storage.timeseries;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import eu.itesla_project.afs.storage.util.AfsStorageException;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.GZIPOutputStream;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public class TimeSeries {

    private final String name;

    private final Map<String, String> tags;

    private final TimeSeriesIndex index;

    public TimeSeries(String name, Map<String, String> tags, TimeSeriesIndex index) {
        this.name = Objects.requireNonNull(name);
        this.tags = Collections.unmodifiableMap(Objects.requireNonNull(tags));
        this.index = Objects.requireNonNull(index);
    }

    public String getName() {
        return name;
    }

    public Map<String, String> getTags() {
        return tags;
    }

    public TimeSeriesIndex getIndex() {
        return index;
    }

    public Stream<Point> stream(List<ArrayChunk> chunks) {
        // check chunk consistency
        //   - all included in index range
        //   - no chunk overlap
        //   - no missing points
        List<ArrayChunk> sortedChunks = chunks.stream().sorted(Comparator.comparing(ArrayChunk::getOffset)).collect(Collectors.toList());
        int pointCount = index.getPointCount();
        int i = 0;
        for (ArrayChunk chunk : sortedChunks) {
            if (chunk.getOffset() != i) {
                throw new AfsStorageException("No value found in range [" + i + ", " + chunk.getOffset() + "]");
            }
            if (i + chunk.getLength() > pointCount - 1) {
                throw new AfsStorageException("Value(s) found out of index range [" + (pointCount - 1) + ", " + (i + chunk.getLength()) + "]");
            }
            i += chunk.getLength();
        }
        if (i < pointCount - 1) {
            throw new AfsStorageException("No value found in range [" + i + ", " + (pointCount - 1) + "]");
        }

        return sortedChunks.stream().flatMap(chunk -> chunk.stream(index));
    }

    public static void writeJson(List<TimeSeries> timeSeriesList, Path file) {
        try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            writeJson(timeSeriesList, writer);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void writeJsonGz(List<TimeSeries> timeSeriesList, Path file) {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(Files.newOutputStream(file)), StandardCharsets.UTF_8))) {
            writeJson(timeSeriesList, writer);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void writeJson(List<TimeSeries> timeSeriesList, BufferedWriter writer) throws IOException {
        JsonFactory factory = new JsonFactory();
        try (JsonGenerator generator = factory.createGenerator(writer)) {
            generator.useDefaultPrettyPrinter();
            generator.writeStartArray();
            for (TimeSeries timeSeries : timeSeriesList) {
                timeSeries.writeJson(generator);
            }
            generator.writeEndArray();
        }
    }

    public void writeJson(JsonGenerator generator) throws IOException {
        generator.writeStartObject();
        generator.writeStringField("name", name);
        generator.writeFieldName("index");
        index.writeJson(generator);
        generator.writeEndObject();
    }
}
