/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.afs.nio2.ext;

import eu.itesla_project.afs.ext.GroovyScript;
import eu.itesla_project.afs.ext.ProjectCase;
import eu.itesla_project.afs.ext.VirtualCase;
import eu.itesla_project.afs.ext.VirtualCaseBuilder;
import eu.itesla_project.afs.nio2.Metadata;
import eu.itesla_project.afs.nio2.Nio2Project;
import eu.itesla_project.afs.nio2.Nio2ProjectFolder;
import eu.itesla_project.afs.nio2.Nio2ProjectNode;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public class Nio2VirtualCaseBuilder implements VirtualCaseBuilder {

    private final Nio2ProjectFolder folder;

    private String name;

    private String casePath;

    private String scriptPath;

    public Nio2VirtualCaseBuilder(Nio2ProjectFolder folder) {
        this.folder = Objects.requireNonNull(folder);
    }

    @Override
    public Class<VirtualCase> getProjectFileClass() {
        return VirtualCase.class;
    }

    @Override
    public Nio2VirtualCaseBuilder withName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public Nio2VirtualCaseBuilder withCase(String casePath) {
        this.casePath = casePath;
        return this;
    }

    @Override
    public Nio2VirtualCaseBuilder withScript(String scriptPath) {
        this.scriptPath = scriptPath;
        return this;
    }

    @Override
    public Nio2VirtualCase build() {
        // check parameters
        if (name == null) {
            throw new RuntimeException("Name is not set");
        }
        if (casePath == null) {
            throw new RuntimeException("Case path is not set");
        }
        if (scriptPath == null) {
            throw new RuntimeException("GroovyScripts path is not set");
        }

        // create the directory
        Path dir = folder.getImpl().getDir().resolve(name);
        try {
            Files.createDirectories(dir);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        String caseId = ((Nio2Project) folder.getProject()).getCentralDirectory().getId(casePath);
        if (caseId == null) {
            throw new RuntimeException("Case '" + casePath + "' not found");
        }
        String scriptId = ((Nio2Project) folder.getProject()).getCentralDirectory().getId(scriptPath);
        if (scriptId == null) {
            throw new RuntimeException("Script '" + scriptPath + "' not found");
        }

        // create metadata
        Metadata metadata = Metadata.create(Nio2VirtualCase.class.getName().toString());
        metadata.addDependency(caseId, "case");
        metadata.addDependency(scriptId, "script");
        metadata.save(dir);

        // create project node
        Nio2VirtualCase virtualCase = new Nio2VirtualCase(dir, folder);

        // create backward dependencies
        Nio2ProjectNode _case = (Nio2ProjectNode) folder.getProject().getRootFolder().getChild(casePath);
        if (!(_case instanceof ProjectCase)) {
            throw new RuntimeException("'" + casePath + "' is not a case");
        }
        Nio2ProjectNode script = (Nio2ProjectNode) folder.getProject().getRootFolder().getChild(scriptPath);
        if (!(script instanceof GroovyScript)) {
            throw new RuntimeException("'" + scriptPath + "' is not a groovy script");
        }
        _case.getImpl().addBackwardDependency(virtualCase.getImpl().getId());
        script.getImpl().addBackwardDependency(virtualCase.getImpl().getId());

        // put id in the central directory
        ((Nio2Project) folder.getProject()).getCentralDirectory().add(metadata.getId(), virtualCase.getPath().toString());

        return virtualCase;
    }
}
