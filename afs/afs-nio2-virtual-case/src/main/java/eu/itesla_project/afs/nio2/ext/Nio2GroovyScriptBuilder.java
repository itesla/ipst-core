/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.afs.nio2.ext;

import eu.itesla_project.afs.ext.GroovyScript;
import eu.itesla_project.afs.ext.GroovyScriptBuilder;
import eu.itesla_project.afs.nio2.Metadata;
import eu.itesla_project.afs.nio2.Nio2ProjectFolder;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public class Nio2GroovyScriptBuilder implements GroovyScriptBuilder {

    private final Nio2ProjectFolder folder;

    private String name;

    private String content;

    public Nio2GroovyScriptBuilder(Nio2ProjectFolder folder) {
        this.folder = Objects.requireNonNull(folder);
    }

    @Override
    public Class<GroovyScript> getProjectFileClass() {
        return GroovyScript.class;
    }

    @Override
    public Nio2GroovyScriptBuilder withName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public Nio2GroovyScriptBuilder withContent(String content) {
        this.content = content;
        return this;
    }

    @Override
    public Nio2GroovyScript build() {
        // check parameters
        if (name == null) {
            throw new RuntimeException("Name is not set");
        }
        if (content == null) {
            throw new RuntimeException("Content is not set");
        }

        // create the directory
        Path dir = folder.getImpl().getDir().resolve(name);
        try {
            Files.createDirectories(dir);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        // create metadata
        Metadata metadata = Metadata.create(Nio2GroovyScript.class.getName().toString());
        metadata.save(dir);

        // create the script file
        try {
            Files.write(dir.resolve(Nio2GroovyScript.SCRIPT_FILE_NAME), content.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        // create project node
        Nio2GroovyScript groovyScript = new Nio2GroovyScript(dir, folder);

        // put id in the central directory
        folder.getProject().getCentralDirectory().add(metadata.getId(), groovyScript.getPath().toString());

        return groovyScript;
    }
}
