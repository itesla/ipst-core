/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.afs.nio2.ext;

import eu.itesla_project.afs.ProjectFile;
import eu.itesla_project.afs.ProjectNode;
import eu.itesla_project.afs.ext.GroovyScript;
import eu.itesla_project.afs.ext.ProjectCase;
import eu.itesla_project.afs.ext.VirtualCase;
import eu.itesla_project.afs.nio2.Nio2Impl;
import eu.itesla_project.afs.nio2.Nio2Project;
import eu.itesla_project.afs.nio2.Nio2ProjectFolder;
import eu.itesla_project.afs.nio2.Nio2ProjectNode;
import eu.itesla_project.iidm.network.Network;
import eu.itesla_project.iidm.xml.NetworkXml;

import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public class Nio2VirtualCase extends VirtualCase implements Nio2ProjectNode {

    private static final String CACHE_XIIDM = "cache.xiidm";
    private static final String OUT_FILE_NAME = "script.out";

    private final Nio2ProjectFolder parent;

    private final Nio2Impl impl;

    Nio2VirtualCase(Path dir, Nio2ProjectFolder parent) {
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

    private Path getCacheFile() {
        return impl.getDir().resolve(CACHE_XIIDM);
    }

    private Path getOutFile() {
        return impl.getDir().resolve(OUT_FILE_NAME);
    }

    @Override
    public Reader getOutReader() {
        Path outFile = getOutFile();
        if (Files.exists(outFile)) {
            try {
                return Files.newBufferedReader(outFile, StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
        return null;
    }

    @Override
    public Writer getOutWriter() {
        try {
            return Files.newBufferedWriter(getOutFile(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public Network loadFromCache() {
        // load network from the cache
        Path cacheFile = getCacheFile();
        if (Files.exists(cacheFile)) {
            return NetworkXml.read(cacheFile);
        }
        return null;
    }

    @Override
    public void saveToCache(Network network) {
        // store network in the cache
        Path cacheFile = getCacheFile();
        NetworkXml.write(network, cacheFile);
    }

    @Override
    public ProjectCase getCase() {
        String casePath = impl.getDependencyPath("case", (Nio2Project) getProject());
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
        String scriptPath = impl.getDependencyPath("script", (Nio2Project) getProject());
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
        return impl.getDependencies((Nio2Project) getProject());
    }

    @Override
    public List<ProjectFile> getBackwardDependencies() {
        return impl.getBackwardDependencies((Nio2Project) getProject());
    }

    @Override
    public void onCacheInvalidation() {
        try {
            Files.deleteIfExists(getCacheFile());
            Files.deleteIfExists(getOutFile());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        // propagate invalidation
        impl.invalidateBackwardDependenciesCache((Nio2Project) getProject());
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
