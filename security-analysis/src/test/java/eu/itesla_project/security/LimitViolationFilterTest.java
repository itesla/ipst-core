/**
 * Copyright (c) 2016, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.security;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import eu.itesla_project.commons.config.InMemoryPlatformConfig;
import eu.itesla_project.commons.config.MapModuleConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.EnumSet;

import static org.junit.Assert.*;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public class LimitViolationFilterTest {

    private FileSystem fileSystem;
    private MapModuleConfig moduleConfig;
    private InMemoryPlatformConfig platformConfig;

    @Before
    public void setUp() throws Exception {
        fileSystem = Jimfs.newFileSystem(Configuration.unix());
        platformConfig = new InMemoryPlatformConfig(fileSystem);
        moduleConfig = platformConfig.createModuleConfig("limit-violation-default-filter");
        moduleConfig.setStringListProperty("violationTypes", Arrays.asList("CURRENT", "LOW_VOLTAGE"));
        moduleConfig.setStringProperty("minBaseVoltage", "150");
    }

    @After
    public void tearDown() throws Exception {
        fileSystem.close();
    }
    @Test
    public void load() throws Exception {
        LimitViolationFilter filter = LimitViolationFilter.load(platformConfig);
        assertEquals(filter.getViolationTypes(), EnumSet.of(LimitViolationType.CURRENT, LimitViolationType.LOW_VOLTAGE));
        assertTrue(filter.getMinBaseVoltage() == 150f);
        filter.setViolationTypes(EnumSet.of(LimitViolationType.HIGH_VOLTAGE));
        assertEquals(filter.getViolationTypes(), EnumSet.of(LimitViolationType.HIGH_VOLTAGE));
        filter.setMinBaseVoltage(225f);
        assertTrue(filter.getMinBaseVoltage() == 225f);
        filter.setViolationTypes(null);
        assertNull(filter.getViolationTypes());
        try {
            filter.setViolationTypes(EnumSet.noneOf(LimitViolationType.class));
            fail();
        } catch (Exception ignored) {
        }
        try {
            filter.setMinBaseVoltage(-3f);
            fail();
        } catch (Exception ignored) {
        }
    }

}