/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.action.simulator.loadflow;

import eu.itesla_project.commons.config.ModuleConfig;
import eu.itesla_project.commons.config.PlatformConfig;
import eu.itesla_project.loadflow.api.LoadFlowFactory;

import java.util.Objects;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian@rte-france.com>
 */
public class LoadFlowActionSimulatorConfig {

    public static LoadFlowActionSimulatorConfig load() {
        ModuleConfig config = PlatformConfig.defaultConfig().getModuleConfig("load-flow-action-simulator");
        Class<? extends LoadFlowFactory> loadFlowFactoryClass = config.getClassProperty("load-flow-factory", LoadFlowFactory.class);
        int maxIterations = config.getIntProperty("max-iterations");
        boolean ignorePreContingencyViolations = config.getBooleanProperty("ignore-pre-contingency-violations", false);
        return new LoadFlowActionSimulatorConfig(loadFlowFactoryClass, maxIterations, ignorePreContingencyViolations);
    }

    private Class<? extends LoadFlowFactory> loadFlowFactoryClass;

    private int maxIterations;

    private boolean ignorePreContingencyViolations;

    public LoadFlowActionSimulatorConfig(Class<? extends LoadFlowFactory> loadFlowFactoryClass, int maxIterations, boolean ignorePreContingencyViolations) {
        this.loadFlowFactoryClass = Objects.requireNonNull(loadFlowFactoryClass);
        this.maxIterations = maxIterations;
        this.ignorePreContingencyViolations = ignorePreContingencyViolations;
    }

    public Class<? extends LoadFlowFactory> getLoadFlowFactoryClass() {
        return loadFlowFactoryClass;
    }

    public void setLoadFlowFactoryClass(Class<? extends LoadFlowFactory> loadFlowFactoryClass) {
        this.loadFlowFactoryClass = Objects.requireNonNull(loadFlowFactoryClass);
    }

    public int getMaxIterations() {
        return maxIterations;
    }

    public void setMaxIterations(int maxIterations) {
        this.maxIterations = maxIterations;
    }

    public boolean isIgnorePreContingencyViolations() {
        return ignorePreContingencyViolations;
    }

    public void setIgnorePreContingencyViolations(boolean ignorePreContingencyViolations) {
        this.ignorePreContingencyViolations = ignorePreContingencyViolations;
    }
}
