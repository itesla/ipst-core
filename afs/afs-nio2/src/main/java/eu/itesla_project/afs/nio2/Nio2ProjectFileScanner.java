/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.afs.nio2;

import eu.itesla_project.afs.ProjectFile;
import eu.itesla_project.afs.ProjectNode;

import java.nio.file.Path;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public interface Nio2ProjectFileScanner {

    Class<? extends ProjectFile> getType();

    ProjectNode scan(Nio2ProjectFolder parent, Path path);
}
