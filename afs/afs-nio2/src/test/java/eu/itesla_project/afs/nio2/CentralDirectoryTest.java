/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.afs.nio2;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public class CentralDirectoryTest {

    private FileSystem fileSystem;

    private CentralDirectory centralDirectory;

    @Before
    public void setUp() throws IOException {
        fileSystem = Jimfs.newFileSystem(Configuration.unix());
        centralDirectory = new CentralDirectory(fileSystem.getPath("/centralDirectory"));
    }

    @After
    public void tearDown() throws IOException {
        fileSystem.close();
    }

    @Test
    public void test() throws Exception {
        String id = UUID.randomUUID().toString();
        centralDirectory.add(id, "toto/tutu");
        assertNull(centralDirectory.getId("toto"));
        assertEquals(id, centralDirectory.getId("toto/tutu"));
        assertNull(centralDirectory.getPath("foooooo"));
        assertEquals("toto/tutu", centralDirectory.getId(id));
        centralDirectory.remove(id);
        assertNull(centralDirectory.getId("toto/tutu"));
        assertNull(centralDirectory.getId(id));
    }
}