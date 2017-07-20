/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.iidm.import_;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import eu.itesla_project.commons.config.InMemoryPlatformConfig;
import eu.itesla_project.commons.config.MapModuleConfig;
import eu.itesla_project.commons.config.PlatformConfig;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Mathieu Bague <mathieu.bague@rte-france.com>
 */
public class JavaScriptPostProcessorTest {

    private FileSystem fileSystem;

    private PlatformConfig platformConfig;

    @Before
    public void setUp() throws IOException {
        fileSystem = Jimfs.newFileSystem(Configuration.unix());

        InMemoryPlatformConfig platformConfig = new InMemoryPlatformConfig(fileSystem);
        MapModuleConfig moduleConfig = platformConfig.createModuleConfig("javaScriptPostProcessor");
        moduleConfig.setStringProperty("printToStdOut", "false");
        this.platformConfig = platformConfig;

        Path script = platformConfig.getConfigDir().resolve(JavaScriptPostProcessor.SCRIPT_NAME);
        Files.copy(getClass().getResourceAsStream("/import-post-processor.js"), script);
    }

    @After
    public void tearDown() throws IOException {
        fileSystem.close();
    }

    @Test
    public void test() throws Exception {
        JavaScriptPostProcessor processor = new JavaScriptPostProcessor(platformConfig);
        Assert.assertEquals("javaScript", processor.getName());

        try {
            processor.process(null, null);
            Assert.fail();
        } catch (Exception ignored) {
        }
    }


}
