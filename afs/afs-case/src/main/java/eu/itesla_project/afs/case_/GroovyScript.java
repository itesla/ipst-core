/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.afs.case_;

import eu.itesla_project.afs.core.AppFileSystem;
import eu.itesla_project.afs.core.FileIcon;
import eu.itesla_project.afs.core.ProjectFile;
import eu.itesla_project.afs.storage.AppFileSystemStorage;
import eu.itesla_project.afs.storage.NodeId;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public class GroovyScript extends ProjectFile {

    public static final String PSEUDO_CLASS = "groovy-script";

    private static final FileIcon SCRIPT_ICON = new FileIcon("script", GroovyScript.class.getResourceAsStream("/icons/script16x16.png"));

    public GroovyScript(NodeId id, AppFileSystemStorage storage, NodeId projectId, AppFileSystem fileSystem) {
        super(id, storage, projectId, fileSystem);
    }

    @Override
    public FileIcon getIcon() {
        return SCRIPT_ICON;
    }

    public String read() {
        return storage.getStringAttribute(id, "script");
    }

    public void write(String content) {
        storage.setStringAttribute(id, "script", content);
        storage.commit();
        notifyDependencyChanged();
    }
}
