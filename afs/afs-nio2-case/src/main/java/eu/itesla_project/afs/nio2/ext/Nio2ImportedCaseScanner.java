/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.afs.nio2.ext;

import com.google.auto.service.AutoService;
import eu.itesla_project.afs.ProjectFile;
import eu.itesla_project.afs.ProjectNode;
import eu.itesla_project.afs.nio2.Nio2ProjectFileScanner;
import eu.itesla_project.afs.nio2.Nio2ProjectFolder;

import java.nio.file.Path;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
@AutoService(Nio2ProjectFileScanner.class)
public class Nio2ImportedCaseScanner implements Nio2ProjectFileScanner {

    @Override
    public Class<? extends ProjectFile> getType() {
        return Nio2ImportedCase.class;
    }

    @Override
    public ProjectNode load(Nio2ProjectFolder parent, Path path) {
        return new Nio2ImportedCase(path, parent);
    }
}
