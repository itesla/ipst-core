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

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
@AutoService(Nio2ProjectFileScanner.class)
public class Nio2GroovyScriptScanner implements Nio2ProjectFileScanner {

    @Override
    public Class<? extends ProjectFile> getType() {
        return Nio2GroovyScript.class;
    }

    @Override
    public Collection<ProjectNode> scan(Nio2ProjectFolder parent, Path path) {
        if (Files.isDirectory(path)) {
            Path metadataFile = path.resolve(Nio2GroovyScript.Metadata.XML_FILE_NAME);
            if (Files.exists(metadataFile)) {
                return Collections.singleton(new Nio2GroovyScript(path, parent));
            }
        }
        return Collections.emptyList();
    }
}