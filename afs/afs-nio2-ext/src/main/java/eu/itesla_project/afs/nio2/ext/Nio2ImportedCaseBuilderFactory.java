/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.afs.nio2.ext;

import com.google.auto.service.AutoService;
import eu.itesla_project.afs.ProjectFile;
import eu.itesla_project.afs.ProjectFileBuilder;
import eu.itesla_project.afs.ext.ImportedCaseBuilder;
import eu.itesla_project.afs.nio2.Nio2ProjectFileBuilderFactory;
import eu.itesla_project.afs.nio2.Nio2ProjectFolder;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
@AutoService(Nio2ProjectFileBuilderFactory.class)
public class Nio2ImportedCaseBuilderFactory implements Nio2ProjectFileBuilderFactory {

    @Override
    public Class<? extends ProjectFileBuilder<? extends ProjectFile>> getProjectFileBuilderClass() {
        return ImportedCaseBuilder.class;
    }

    @Override
    public ProjectFileBuilder<? extends ProjectFile> create(Nio2ProjectFolder folder) {
        return new Nio2ImportedCaseBuilder(folder);
    }
}
