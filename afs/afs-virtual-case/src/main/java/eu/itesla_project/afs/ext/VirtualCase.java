/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.afs.ext;

import eu.itesla_project.afs.FileIcon;
import eu.itesla_project.computation.script.GroovyScripts;
import eu.itesla_project.iidm.network.Network;
import groovy.lang.Binding;

import java.io.*;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public abstract class VirtualCase extends ProjectCase {

    private static final FileIcon VIRTUAL_CASE_ICON = new FileIcon("virtualCase", VirtualCase.class.getResourceAsStream("/icons/virtualCase16x16.png"));

    @Override
    public FileIcon getIcon() {
        return VIRTUAL_CASE_ICON;
    }

    public abstract ProjectCase getCase();

    public abstract GroovyScript getScript();

    public abstract Writer getOutWriter();

    public abstract Reader getOutReader();

    public abstract Network loadFromCache();

    public abstract void saveToCache(Network network);

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
                    GroovyScripts.run(reader, getProject().getFileSystem().getComputationManager(), binding, out);
                }
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }

            // store network in the cache
            saveToCache(network);
        }

        return network;
    }
}
