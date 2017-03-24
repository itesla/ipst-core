/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.afs.nio2;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import eu.itesla_project.afs.AppFileSystem;
import eu.itesla_project.afs.nio2.Nio2AppFileSystem;
import eu.itesla_project.afs.nio2.Nio2FileScanner;
import eu.itesla_project.afs.nio2.Nio2ProjectFileBuilderFactory;
import eu.itesla_project.afs.nio2.Nio2ProjectFileScanner;
import eu.itesla_project.computation.ComputationManager;
import eu.itesla_project.iidm.import_.Importer;
import eu.itesla_project.iidm.import_.ImportersLoader;
import eu.itesla_project.iidm.import_.ImportersLoaderList;
import eu.itesla_project.iidm.network.Network;
import eu.itesla_project.iidm.network.NetworkFactory;
import org.junit.After;
import org.junit.Before;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public abstract class Nio2AbstractProjectFileTest {

    protected FileSystem fs;

    protected Path dataDir;

    protected AppFileSystem afs;

    protected Network network;

    protected List<Importer> getImporters() {
        return Collections.emptyList();
    }

    protected List<Nio2FileScanner> getFileScanners() {
        return Collections.emptyList();
    }

    protected List<Nio2ProjectFileScanner> getProjectFileScanners() {
        return Collections.emptyList();
    }

    protected List<Nio2ProjectFileBuilderFactory> getProjectFileBuilderFactories() {
        return Collections.emptyList();
    }

    @Before
    public void setup() throws IOException {
        fs = Jimfs.newFileSystem(Configuration.unix());
        dataDir = fs.getPath("/data");
        Files.createDirectories(dataDir);
        network = NetworkFactory.create("test", "test");
        ImportersLoader loader = new ImportersLoaderList(getImporters(), Collections.emptyList());
        ComputationManager computationManager = Mockito.mock(ComputationManager.class);
        afs = new Nio2AppFileSystem(fs,
                                   "mem",
                                   "/data",
                                   getFileScanners(),
                                   getProjectFileScanners(),
                                   getProjectFileBuilderFactories(),
                                   loader, computationManager);
    }

    @After
    public void tearDown() throws IOException {
        fs.close();
    }
}