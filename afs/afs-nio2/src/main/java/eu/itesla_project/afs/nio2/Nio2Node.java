/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.afs.nio2;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public abstract class Nio2Node {

    protected final Path path;

    protected final Nio2Folder parent;

    protected final Nio2AppFileSystem fileSystem;

    protected Nio2Node(Path path, Nio2Folder parent, Nio2AppFileSystem fileSystem) {
        this.path = Objects.requireNonNull(path);
        this.parent = parent;
        this.fileSystem = Objects.requireNonNull(fileSystem);
    }

    public abstract String getName();

    public Nio2Folder getParent() {
        return parent;
    }

    protected void addPath(List<String> path) {
        if (parent != null) {
            parent.addPath(path);
        }
        path.add(getName());
    }

    public List<String> getPathList() {
        List<String> path = new ArrayList<>(1);
        addPath(path);
        return path;
    }

    public String getPath() {
        List<String> pathList = getPathList();
        StringBuilder builder = new StringBuilder();
        builder.append(pathList.get(0))
                .append(Nio2AppFileSystem.FS_SEPARATOR);
        for (int i = 1; i < pathList.size(); i++) {
            builder.append(Nio2AppFileSystem.PATH_SEPARATOR).append(pathList.get(i));
        }
        return builder.toString();
    }

    public Nio2AppFileSystem getFileSystem() {
        return fileSystem;
    }

    @Override
    public String toString() {
        return getName();
    }

}