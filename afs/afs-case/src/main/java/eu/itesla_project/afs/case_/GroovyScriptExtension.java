/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.afs.case_;

import com.google.auto.service.AutoService;
import eu.itesla_project.afs.core.AppFileSystem;
import eu.itesla_project.afs.core.ProjectFileExtension;
import eu.itesla_project.afs.storage.AppFileSystemStorage;
import eu.itesla_project.afs.storage.NodeId;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
@AutoService(ProjectFileExtension.class)
public class GroovyScriptExtension implements ProjectFileExtension {
    @Override
    public Class<GroovyScript> getProjectFileClass() {
        return GroovyScript.class;
    }

    @Override
    public String getProjectFilePseudoClass() {
        return GroovyScript.PSEUDO_CLASS;
    }

    @Override
    public Class<GroovyScriptBuilder> getProjectFileBuilderClass() {
        return GroovyScriptBuilder.class;
    }

    @Override
    public GroovyScript createProjectFile(NodeId id, AppFileSystemStorage storage, NodeId projectId, AppFileSystem fileSystem) {
        return new GroovyScript(id, storage, projectId, fileSystem);
    }

    @Override
    public GroovyScriptBuilder createProjectFileBuilder(NodeId folderId, AppFileSystemStorage storage, NodeId projectId, AppFileSystem fileSystem) {
        return new GroovyScriptBuilder(folderId, storage, projectId, fileSystem);
    }
}
