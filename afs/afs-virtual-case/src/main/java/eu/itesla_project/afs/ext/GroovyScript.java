/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.afs.ext;

import eu.itesla_project.afs.FileIcon;
import eu.itesla_project.afs.ProjectFile;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public abstract class GroovyScript extends ProjectFile {

    private static final FileIcon SCRIPT_ICON = new FileIcon("script", GroovyScript.class.getResourceAsStream("/icons/script16x16.png"));

    @Override
    public FileIcon getIcon() {
        return SCRIPT_ICON;
    }

    public abstract String read();

    public abstract void write(String content);
}
