/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.afs.case_;

import eu.itesla_project.afs.core.AppFileSystem;
import eu.itesla_project.afs.storage.AppFileSystemStorage;
import eu.itesla_project.afs.storage.NodeId;
import eu.itesla_project.afs.core.FileIcon;
import eu.itesla_project.computation.script.GroovyScripts;
import eu.itesla_project.iidm.network.Network;
import eu.itesla_project.iidm.xml.NetworkXml;
import groovy.lang.Binding;

import java.io.*;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public class VirtualCase extends ProjectCase {

    public static final String PSEUDO_CLASS = "virtual-case";

    private static final FileIcon VIRTUAL_CASE_ICON = new FileIcon("virtualCase", VirtualCase.class.getResourceAsStream("/icons/virtualCase16x16.png"));

    private static final String NETWORK_CACHE_KEY = "network";
    private static final String OUT_ATTRIBUTE = "out";

    public VirtualCase(NodeId id, AppFileSystemStorage storage, NodeId projectId, AppFileSystem fileSystem) {
        super(id, storage, projectId, fileSystem);
    }

    @Override
    public FileIcon getIcon() {
        return VIRTUAL_CASE_ICON;
    }

    public ProjectCase getCase() {
        return (ProjectCase) findProjectFile(storage.getDependency(id, "case"));
    }

    public GroovyScript getScript() {
        return (GroovyScript) findProjectFile(storage.getDependency(id, "script"));
    }

    public Writer getOutWriter() {
        return storage.writeStringAttribute(id, OUT_ATTRIBUTE);
    }

    public Reader getOutReader() {
        return storage.readStringAttribute(id, OUT_ATTRIBUTE);
    }

    public Network loadFromCache() {
        InputStream is = storage.readFromCache(id, NETWORK_CACHE_KEY);
        if (is != null) {
            try {
                return NetworkXml.read(is);
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
        }
        return null;
    }

    public void saveToCache(Network network) {
        try (OutputStream os = storage.writeToCache(id, NETWORK_CACHE_KEY)) {
            NetworkXml.write(network, os);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        storage.commit();
    }

    @Override
    public Network loadNetwork() {
        // load network from the cache
        Network network = loadFromCache();

        // if no network cached, recreate it
        if (network == null) {
            // load network
            network = getCase().loadNetwork();

            // load groovy script
            GroovyScript script = getScript();

            try (Reader reader = new StringReader(script.read())) {
                // put network in the binding so that it is accessible from the script
                Binding binding = new Binding();
                binding.setProperty("network", network);

                // run groovy script
                try (Writer out = getOutWriter()) {
                    GroovyScripts.run(reader, getProject().getFileSystem().getData().getComputationManager(), binding, out);
                }
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }

            // store network in the cache
            saveToCache(network);
        }

        return network;
    }

    @Override
    public void onDependencyChanged() {
        storage.setStringAttribute(id, OUT_ATTRIBUTE, null);
        storage.invalidateCache(id, NETWORK_CACHE_KEY);
        storage.commit();
    }
}
