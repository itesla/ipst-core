/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.afs;

import java.util.List;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public abstract class ProjectFolder extends ProjectNode {

    @Override
    public boolean isFolder() {
        return true;
    }

    public abstract ProjectFolder createFolder(String name);

    public abstract List<ProjectNode> getChildren();

    public abstract ProjectNode getChild(String path);

    public abstract <F extends ProjectFile, B extends ProjectFileBuilder<F>> B fileBuilder(Class<B> clazz);
}
