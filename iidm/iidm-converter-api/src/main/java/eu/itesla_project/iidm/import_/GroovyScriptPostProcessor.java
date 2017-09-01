/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.iidm.import_;

import com.google.auto.service.AutoService;
import eu.itesla_project.commons.config.ModuleConfig;
import eu.itesla_project.commons.config.PlatformConfig;
import eu.itesla_project.computation.ComputationManager;
import eu.itesla_project.iidm.network.Network;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/**
 * @author Mathieu Bague <mathieu.bague@rte-france.com>
 */
@AutoService(ImportPostProcessor.class)
public class GroovyScriptPostProcessor implements ImportPostProcessor {

    public static final String NAME = "groovyScript";

    public static final String DEFAULT_SCRIPT_NAME = "import-post-processor.groovy";

    private static final Logger LOGGER = LoggerFactory.getLogger(GroovyScriptPostProcessor.class);

    private Path script;

    public GroovyScriptPostProcessor() {
        this(PlatformConfig.defaultConfig());
    }

    public GroovyScriptPostProcessor(PlatformConfig platformConfig) {
        Objects.requireNonNull(platformConfig);

        Path defaultScript = platformConfig.getConfigDir().resolve(DEFAULT_SCRIPT_NAME);

        ModuleConfig config = platformConfig.getModuleConfigIfExists("groovy-post-processor");
        if (config != null) {
            script = config.getPathProperty("script", defaultScript);
        } else {
            script = defaultScript;
        }
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void process(Network network, ComputationManager computationManager) throws Exception {
        if (Files.exists(script)) {
            LOGGER.debug("Execute groovy post processor {}", script);
            try (Reader reader = Files.newBufferedReader(script)) {
                CompilerConfiguration conf = new CompilerConfiguration();

                Binding binding = new Binding();
                binding.setVariable("network", network);
                binding.setVariable("computationManager", computationManager);

                GroovyShell shell = new GroovyShell(binding, conf);
                shell.evaluate(reader);
            }
        }
    }
}
