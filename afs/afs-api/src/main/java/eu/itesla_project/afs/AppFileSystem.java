/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.afs;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import eu.itesla_project.computation.ComputationManager;

import java.util.*;

/**
 *
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public abstract class AppFileSystem {

    public static final String FS_SEPARATOR = ":";
    public static final String PATH_SEPARATOR = "/";

    private static final Supplier<Map<String, AppFileSystem>> FILE_SYSTEMS = Suppliers.memoize(() -> {
        Map<String, AppFileSystem> fileSystems = new HashMap<>();
        for (AppFileSystemProvider provider : ServiceLoader.load(AppFileSystemProvider.class)) {
            for (AppFileSystem fileSystem : provider.getFileSystems()) {
                if (fileSystems.containsKey(fileSystem.getName())) {
                    throw new RuntimeException("A file system with the same name '" + fileSystem.getName() + "' already exists");
                }
                fileSystems.put(fileSystem.getName(), fileSystem);
            }
        }
        return fileSystems;
    });

    public static Collection<AppFileSystem> findAll() {
        return FILE_SYSTEMS.get().values();
    }

    public static AppFileSystem find(String name) {
        Objects.requireNonNull(name);
        AppFileSystem fileSystem = FILE_SYSTEMS.get().get(name);
        if (fileSystem == null) {
            throw new RuntimeException("File system '" + name + "' not found");
        }
        return fileSystem;
    }

    public static Node getNode(String pathStr) {
        Objects.requireNonNull(pathStr);
        String[] path = pathStr.split(FS_SEPARATOR + PATH_SEPARATOR);
        if (path.length == 0) { // wrong file system name
            return null;
        }
        String fileSystemName = path[0];
        AppFileSystem fileSystem = FILE_SYSTEMS.get().get(fileSystemName);
        if (fileSystem == null) {
            return null;
        }
        return path.length == 1 ? fileSystem.getRootFolder() : fileSystem.getRootFolder().getChild(path[1], path.length > 2 ? Arrays.copyOfRange(path, 2, path.length - 1) : new String[] {});
    }

    public abstract String getName();

    public abstract Folder getRootFolder();

    public abstract Collection<Class<? extends ProjectFile>> getProjectFileTypes();

    public abstract ComputationManager getComputationManager();

}
