/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.afs.nio2.ext;

import com.google.common.collect.ImmutableList;
import eu.itesla_project.afs.FileIcon;
import eu.itesla_project.afs.ProjectFile;
import eu.itesla_project.afs.ProjectNode;
import eu.itesla_project.afs.ext.GroovyScript;
import eu.itesla_project.afs.ext.ProjectCase;
import eu.itesla_project.afs.ext.VirtualCase;
import eu.itesla_project.afs.nio2.Nio2ProjectFolder;
import eu.itesla_project.afs.nio2.Nio2ProjectNode;
import eu.itesla_project.commons.jaxb.JaxbUtil;
import eu.itesla_project.iidm.network.Network;
import eu.itesla_project.iidm.xml.NetworkXml;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public class Nio2VirtualCase extends Nio2ProjectNode<Nio2VirtualCase.Metadata> implements VirtualCase {

    private static final FileIcon VIRTUAL_CASE_ICON = new FileIcon("virtualCase", Nio2VirtualCase.class.getResourceAsStream("/icons/virtualCase16x16.png"));

    private static final String CACHE_XIIDM = "cache.xiidm";
    private static final String OUT_FILE_NAME = "script.out";

    @XmlRootElement(name = "virtualCaseMetadata")
    public static class Metadata extends Nio2ProjectNode.Metadata {

        public static final String XML_FILE_NAME = "virtualCaseMetadata.xml";

        public static Metadata create(String caseId, String scriptId) {
            return new Metadata(UUID.randomUUID().toString(), caseId, scriptId);
        }

        public static Metadata read(Path dir) {
            return JaxbUtil.unmarchallFile(Metadata.class, dir.resolve(XML_FILE_NAME));
        }

        @XmlAttribute(name = "caseId", required = true)
        private String caseId;

        @XmlAttribute(name = "scriptId", required = true)
        private String scriptId;

        public Metadata() {
        }

        public Metadata(String id, String caseId, String scriptId) {
            super(id);
            this.caseId = Objects.requireNonNull(caseId);
            this.scriptId = Objects.requireNonNull(scriptId);
        }

        public String getCaseId() {
            return caseId;
        }

        public void setCaseId(String caseId) {
            this.caseId = caseId;
        }

        public String getScriptId() {
            return scriptId;
        }

        public void setScriptId(String scriptId) {
            this.scriptId = scriptId;
        }

        public void save(Path dir) {
            JaxbUtil.marshallElement(Metadata.class, this, dir.resolve(XML_FILE_NAME));
        }
    }

    Nio2VirtualCase(Path dir, Nio2ProjectFolder folder) {
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
        return VIRTUAL_CASE_ICON;
    }

    private Path getCacheFile() {
        return dir.resolve(CACHE_XIIDM);
    }

    private Path getOutFile() {
        return dir.resolve(OUT_FILE_NAME);
    }

    @Override
    public Reader getOutReader() {
        checkNotDeleted();
        Path outFile = getOutFile();
        if (Files.exists(outFile)) {
            try {
                return Files.newBufferedReader(outFile, StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    @Override
    public Writer getOutWriter() {
        try {
            return Files.newBufferedWriter(getOutFile(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Network loadFromCache() {
        checkNotDeleted();
        // load network from the cache
        Path cacheFile = getCacheFile();
        if (Files.exists(cacheFile)) {
            return NetworkXml.read(cacheFile);
        }
        return null;
    }

    @Override
    public void saveToCache(Network network) {
        checkNotDeleted();
        // store network in the cache
        Path cacheFile = getCacheFile();
        NetworkXml.write(network, cacheFile);
    }

    @Override
    public ProjectCase getCase() {
        checkNotDeleted();
        return getCase(readMetadata());
    }

    private ProjectCase getCase(Metadata metadata) {
        String casePath = getProject().getCentralDirectory().getPath(metadata.getCaseId());
        ProjectNode node = getProject().getRootFolder().getChild(casePath);
        if (node == null) {
            throw new RuntimeException("Invalid case path");
        }
        if (!(node instanceof ProjectCase)) {
            throw new RuntimeException(casePath + " is not a case");
        }
        return (ProjectCase) node;
    }

    @Override
    public GroovyScript getScript() {
        checkNotDeleted();
        return getScript(readMetadata());
    }

    private GroovyScript getScript(Metadata metadata) {
        String scriptPath = getProject().getCentralDirectory().getPath(metadata.getScriptId());
        ProjectNode node = getProject().getRootFolder().getChild(scriptPath);
        if (node == null) {
            throw new RuntimeException("Invalid script path");
        }
        if (!(node instanceof GroovyScript)) {
            throw new RuntimeException(scriptPath + " is not a script");
        }
        return (GroovyScript) node;
    }

    @Override
    public List<ProjectFile> getDependencies() {
        checkNotDeleted();
        return ImmutableList.of(getCase(), getScript());
    }

    @Override
    protected void invalidateCache() {
        try {
            Files.deleteIfExists(getCacheFile());
            Files.deleteIfExists(getOutFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        super.invalidateCache();
    }

    @Override
    public String getName() {
        checkNotDeleted();
        return dir.getFileName().toString();
    }
}
