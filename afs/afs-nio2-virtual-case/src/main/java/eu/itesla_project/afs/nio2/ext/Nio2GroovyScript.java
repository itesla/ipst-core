/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.afs.nio2.ext;

import eu.itesla_project.afs.FileIcon;
import eu.itesla_project.afs.ProjectFile;
import eu.itesla_project.afs.ext.GroovyScript;
import eu.itesla_project.afs.nio2.Nio2ProjectFolder;
import eu.itesla_project.afs.nio2.Nio2ProjectNode;
import eu.itesla_project.commons.jaxb.JaxbUtil;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public class Nio2GroovyScript extends Nio2ProjectNode<Nio2GroovyScript.Metadata> implements GroovyScript {

    @XmlRootElement(name = "scriptMetadata")
    public static class Metadata extends Nio2ProjectNode.Metadata {

        public static final String XML_FILE_NAME = "scriptMetadata.xml";

        public static Metadata create() {
            return new Metadata(UUID.randomUUID().toString());
        }

        public static Metadata read(Path dir) {
            return JaxbUtil.unmarchallFile(Metadata.class, dir.resolve(XML_FILE_NAME));
        }

        public Metadata() {
        }

        public Metadata(String id) {
            super(id);
        }

        public void save(Path dir) {
            JaxbUtil.marshallElement(Metadata.class, this, dir.resolve(XML_FILE_NAME));
        }
    }

    static final String SCRIPT_FILE_NAME = "script.groovy";

    private static final FileIcon SCRIPT_ICON = new FileIcon("script", Nio2GroovyScript.class.getResourceAsStream("/icons/script16x16.png"));

    Nio2GroovyScript(Path dir, Nio2ProjectFolder folder) {
        super(dir, Objects.requireNonNull(folder));
    }

    @Override
    protected Metadata readMetadata() {
        return Metadata.read(dir);
    }

    @Override
    public boolean isFolder() {
        checkNotDeleted();
        return false;
    }

    @Override
    public FileIcon getIcon() {
        checkNotDeleted();
        return SCRIPT_ICON;
    }

    @Override
    public String read() {
        checkNotDeleted();
        try {
            return new String(Files.readAllBytes(dir.resolve(SCRIPT_FILE_NAME)), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void write(String content) {
        checkNotDeleted();
        try {
            Files.write(dir.resolve(SCRIPT_FILE_NAME), content.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        invalidateCache();
    }

    @Override
    public List<ProjectFile> getDependencies() {
        checkNotDeleted();
        return Collections.emptyList();
    }

    @Override
    public String getName() {
        checkNotDeleted();
        return dir.getFileName().toString();
    }
}
