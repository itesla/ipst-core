/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.loadflow.validation;

import eu.itesla_project.commons.config.ModuleConfig;
import eu.itesla_project.commons.config.PlatformConfig;
import eu.itesla_project.loadflow.api.LoadFlowFactory;

/**
 *
 * @author Massimo Ferraro <massimo.ferraro@techrain.it>
 */
public class CheckFlowsConfig {
    
    public static final float THRESHOLD_DEFAULT = 0.01f;
    public static final boolean VERBOSE_DEFAULT = false;
    
    private final float threshold;
    private boolean verbose;
    private final Class<? extends LoadFlowFactory> loadFlowFactory;
    
    public static CheckFlowsConfig load() {
        return load(PlatformConfig.defaultConfig());
    }

    public static CheckFlowsConfig load(PlatformConfig platformConfig) {
        float threshold = THRESHOLD_DEFAULT;
        boolean verbose = VERBOSE_DEFAULT;
        Class<? extends LoadFlowFactory> loadFlowFactory = null;
        if (platformConfig.moduleExists("checkFlows")) {
            ModuleConfig config = platformConfig.getModuleConfig("checkFlows");
            threshold = config.getFloatProperty("threshold", THRESHOLD_DEFAULT);
            verbose = config.getBooleanProperty("verbose", VERBOSE_DEFAULT);
            loadFlowFactory = config.getClassProperty("loadFlowFactory", LoadFlowFactory.class, null);
        }
        return new CheckFlowsConfig(threshold, verbose, loadFlowFactory);
    }
    
    public CheckFlowsConfig(float threshold, boolean verbose, Class<? extends LoadFlowFactory> loadFlowFactoryClass) {
        this.threshold = threshold;
        this.verbose = verbose;
        this.loadFlowFactory = loadFlowFactoryClass;
    }

    public float getThreshold() {
        return threshold;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public Class<? extends LoadFlowFactory> getLoadFlowFactory() {
        return loadFlowFactory;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [" + 
                "threshold=" + threshold +
                ", verbose=" + verbose +
                ", loadFlowFactory=" + loadFlowFactory +
                "]";
    }
}
