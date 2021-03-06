/**
 * Copyright (c) 2016, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.commons.datasource;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public class ZipFileDataSourceTest extends AbstractDataSourceTest {

    @Override
    protected boolean appendTest() {
        return false;
    }

    @Override
    protected DataSource createDataSource() {
        return new ZipFileDataSource(testDir, getBaseName());
    }
}
