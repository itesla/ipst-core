/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.afs.storage;

import eu.itesla_project.afs.storage.timeseries.ArrayChunk;
import eu.itesla_project.afs.storage.timeseries.CompressedArrayChunk;
import eu.itesla_project.afs.storage.timeseries.UncompressedArrayChunk;
import org.junit.Test;

import java.util.List;

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
        assertSame(chunk, chunk.toUncompressed());
        ArrayChunk compressedChunk = chunk.tryToCompress();
        assertTrue(compressedChunk instanceof CompressedArrayChunk);
        assertArrayEquals(values, compressedChunk.toUncompressed().getValues(), 0d);
        List<ArrayChunk> chunks = chunk.split(4);
        assertEquals(4, chunks.size());
        assertEquals(3, chunks.get(0).getLength());
        assertArrayEquals(new double[] {1d, 2d, 2d}, ((UncompressedArrayChunk) chunks.get(0)).getValues(), 0d);
        assertEquals(3, chunks.get(1).getLength());
        assertArrayEquals(new double[] {2d, 2d, 2d}, ((UncompressedArrayChunk) chunks.get(1)).getValues(), 0d);
        assertEquals(3, chunks.get(2).getLength());
        assertArrayEquals(new double[] {3d, 3d, 3d}, ((UncompressedArrayChunk) chunks.get(2)).getValues(), 0d);
        assertEquals(1, chunks.get(3).getLength());
        assertArrayEquals(new double[] {4d}, ((UncompressedArrayChunk) chunks.get(3)).getValues(), 0d);
    }

}
