/**
 * Copyright (c) 2016, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.security;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Properties;

import org.apache.commons.compress.utils.IOUtils;

import com.google.common.io.Files;

import eu.itesla_project.commons.config.ComponentDefaultConfig;
import eu.itesla_project.computation.ComputationManager;
import eu.itesla_project.contingency.ContingenciesProvider;
import eu.itesla_project.contingency.ContingenciesProviderFactory;
import eu.itesla_project.iidm.datasource.MemDataSource;
import eu.itesla_project.iidm.datasource.ReadOnlyDataSource;
import eu.itesla_project.iidm.import_.Importers;
import eu.itesla_project.iidm.network.Network;

/**
 * @author Giovanni Ferrari <giovanni.ferrari@techrain.it>
 */
public class SecurityAnalyzer {
    
    public enum Format {
        CSV,
        JSON
    }

    private final ComputationManager computationManager;
    private final int priority;
    private final SecurityAnalysisFactory securityAnalysisFactory;
    private final ContingenciesProviderFactory contingenciesProviderFactory;

    public SecurityAnalyzer(ComputationManager computationManager, int priority){
        this.computationManager = Objects.requireNonNull(computationManager);
        this.priority = priority;
        ComponentDefaultConfig defaultConfig = ComponentDefaultConfig.load();
        securityAnalysisFactory = defaultConfig.newFactoryImpl(SecurityAnalysisFactory.class);
        contingenciesProviderFactory = defaultConfig.newFactoryImpl(ContingenciesProviderFactory.class);
    }
    
    public SecurityAnalyzer(ComputationManager computationManager, int priority, SecurityAnalysisFactory securityAnalysisFactory, ContingenciesProviderFactory contingenciesProviderFactory){
        this.computationManager = Objects.requireNonNull(computationManager);
        this.priority = priority;
        this.securityAnalysisFactory = Objects.requireNonNull(securityAnalysisFactory);
        this.contingenciesProviderFactory = Objects.requireNonNull(contingenciesProviderFactory);
    }


    public SecurityAnalysisResult analyze(Path caseFile, Path contingenciesFile) {
        Objects.requireNonNull(caseFile);

        Network network = Importers.loadNetwork(caseFile);
        if (network == null) {
            throw new RuntimeException("Case '" + caseFile + "' not found");
        }
        network.getStateManager().allowStateMultiThreadAccess(true);

        SecurityAnalysis securityAnalysis = securityAnalysisFactory.create(network, computationManager, priority);

        ContingenciesProvider contingenciesProvider = contingenciesFile != null
                ? contingenciesProviderFactory.create(contingenciesFile) : contingenciesProviderFactory.create();

        // run security analysis on all N-1 lines
        return securityAnalysis.runAsync(contingenciesProvider).join();

    }
    
    public SecurityAnalysisResult analyze(byte[] networkData, String filename, byte[] contingencies) {
        Objects.requireNonNull(networkData);
        Objects.requireNonNull(filename);

        Network network = Importers.loadNetwork(networkData, filename);
        if (network == null) {
            throw new RuntimeException("Error loading network");
        }
        network.getStateManager().allowStateMultiThreadAccess(true);

        SecurityAnalysis securityAnalysis = securityAnalysisFactory.create(network, computationManager, priority);

        ContingenciesProvider contingenciesProvider = contingencies != null
                ? contingenciesProviderFactory.create(contingencies) : contingenciesProviderFactory.create();

        // run security analysis on all N-1 lines
        return securityAnalysis.runAsync(contingenciesProvider).join();

    }

}