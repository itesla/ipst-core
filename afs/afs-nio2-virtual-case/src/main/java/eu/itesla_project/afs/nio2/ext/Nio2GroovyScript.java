/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.afs.nio2.ext;

import eu.itesla_project.afs.ProjectFile;
import eu.itesla_project.afs.ext.GroovyScript;
import eu.itesla_project.afs.nio2.Nio2Impl;
import eu.itesla_project.afs.nio2.Nio2Project;
import eu.itesla_project.afs.nio2.Nio2ProjectFolder;
import eu.itesla_project.afs.nio2.Nio2ProjectNode;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public class Nio2GroovyScript extends GroovyScript implements Nio2ProjectNode {

    static final String SCRIPT_FILE_NAME = "script.groovy";

    private final Nio2ProjectFolder parent;

    private final Nio2Impl impl;

    Nio2GroovyScript(Path dir, Nio2ProjectFolder parent) {
        impl = new Nio2Impl(dir);
        this.parent = Objects.requireNonNull(parent);
    }

    @Override
    public Nio2Impl getImpl() {
        return impl;
    }

    @Override
    public Nio2ProjectFolder getParent() {
        impl.checkNotDeleted();
        return parent;
    }

    @Override
    public String read() {
        try {
            return new String(Files.readAllBytes(impl.getDir().resolve(SCRIPT_FILE_NAME)), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void write(String content) {
        try {
            Files.write(impl.getDir().resolve(SCRIPT_FILE_NAME), content.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        impl.invalidateBackwardDependenciesCache((Nio2Project) getProject());
    }

    @Override
    public List<ProjectFile> getDependencies() {
        return impl.getDependencies((Nio2Project) getProject());
    }

    @Override
    public List<ProjectFile> getBackwardDependencies() {
        return impl.getBackwardDependencies((Nio2Project) getProject());
    }

    @Override
    public void onCacheInvalidation() {
    }

    @Override
    public void delete() {
        impl.delete((Nio2Project) getProject());
    }

    @Override
    public String getName() {
        return impl.getName();
    }
}
