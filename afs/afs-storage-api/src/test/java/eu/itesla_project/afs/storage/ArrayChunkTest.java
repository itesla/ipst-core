/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.afs.storage;

import eu.itesla_project.afs.storage.timeseries.*;
import org.junit.Test;
import org.threeten.extra.Interval;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public class ArrayChunkTest {

    @Test
    public void test() {
        double[] values = new double[] {1d, 2d, 2d, 2d, 2d, 2d, 3d, 3d, 3d, 4d};
        UncompressedArrayChunk chunk = new UncompressedArrayChunk(0, values);
        assertEquals(0, chunk.getOffset());
        assertEquals(10, chunk.getLength());
        assertArrayEquals(values, chunk.getValues(), 0d);
        ArrayChunk compressedChunk = chunk.tryToCompress();
        assertTrue(compressedChunk instanceof CompressedArrayChunk);
 //       assertArrayEquals(values, compressedChunk.toUncompressed().getValues(), 0d);
    }

    @Test
    public void uncompressedStreamTest() {
        RegularTimeSeriesIndex index = new RegularTimeSeriesIndex(Interval.parse("2015-01-01T00:00:00Z/2015-01-01T00:30:00Z"),
                                                                  Duration.ofMinutes(15),
                                                                  1, 1);
        assertEquals(Arrays.asList(new Point(0, Instant.parse("2015-01-01T00:00:00Z"), 1d),
                                   new Point(1, Instant.parse("2015-01-01T00:15:00Z"), 2d),
                                   new Point(2, Instant.parse("2015-01-01T00:30:00Z"), 3d)),
                     new UncompressedArrayChunk(0, new double[] {1d, 2d, 3d}).stream(index).collect(Collectors.toList()));
    }

    @Test
    public void compressedStreamTest() {
        RegularTimeSeriesIndex index = new RegularTimeSeriesIndex(Interval.parse("2015-01-01T00:00:00Z/2015-01-01T00:30:00Z"),
                                                                  Duration.ofMinutes(15),
                                                                  1, 1);
        assertEquals(Arrays.asList(new Point(0, Instant.parse("2015-01-01T00:00:00Z"), 1d),
                                   new Point(2, Instant.parse("2015-01-01T00:30:00Z"), 2d)),
                     new CompressedArrayChunk(0, 3, new double[] {1d, 2d}, new int[] {2, 1}).stream(index).collect(Collectors.toList()));
    }
}
