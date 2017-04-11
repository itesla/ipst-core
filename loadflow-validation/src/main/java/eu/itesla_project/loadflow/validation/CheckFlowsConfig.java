/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.loadflow.validation;

import java.util.Objects;

import eu.itesla_project.commons.config.ComponentDefaultConfig;
import eu.itesla_project.commons.config.ModuleConfig;
import eu.itesla_project.commons.config.PlatformConfig;
import eu.itesla_project.commons.io.table.CsvTableFormatterFactory;
import eu.itesla_project.commons.io.table.TableFormatterFactory;
import eu.itesla_project.loadflow.api.LoadFlowFactory;

/**
 *
 * @author Massimo Ferraro <massimo.ferraro@techrain.it>
 */
public class CheckFlowsConfig {
    
    public static final float THRESHOLD_DEFAULT = 0.0f;
    public static final boolean VERBOSE_DEFAULT = false;
    public static final Class<? extends TableFormatterFactory> TABLE_FORMATTER_FACTORY_DEFAULT =  CsvTableFormatterFactory.class;
    
    private final float threshold;
    private final boolean verbose;
    private final Class<? extends LoadFlowFactory> loadFlowFactory;
    private final Class<? extends TableFormatterFactory> tableFormatterFactory;

    public static CheckFlowsConfig load() {
        return load(PlatformConfig.defaultConfig());
    }

    public static CheckFlowsConfig load(PlatformConfig platformConfig) {
        float threshold = THRESHOLD_DEFAULT;
        boolean verbose = VERBOSE_DEFAULT;
        ComponentDefaultConfig componentDefaultConfig = ComponentDefaultConfig.load(platformConfig);
        Class<? extends LoadFlowFactory> loadFlowFactory = componentDefaultConfig.findFactoryImplClass(LoadFlowFactory.class);
        Class<? extends TableFormatterFactory> tableFormatterFactory = TABLE_FORMATTER_FACTORY_DEFAULT;
        if (platformConfig.moduleExists("check-flows")) {
            ModuleConfig config = platformConfig.getModuleConfig("check-flows");
            threshold = config.getFloatProperty("threshold", THRESHOLD_DEFAULT);
            verbose = config.getBooleanProperty("verbose", VERBOSE_DEFAULT);
            if ( config.hasProperty("load-flow-factory") ) { 
                loadFlowFactory = config.getClassProperty("load-flow-factory", LoadFlowFactory.class, componentDefaultConfig.findFactoryImplClass(LoadFlowFactory.class));
            }
            tableFormatterFactory = config.getClassProperty("table-formatter-factory", TableFormatterFactory.class, TABLE_FORMATTER_FACTORY_DEFAULT);
        }
        return new CheckFlowsConfig(threshold, verbose, loadFlowFactory, tableFormatterFactory);
    }
    
    public CheckFlowsConfig(float threshold, boolean verbose, Class<? extends LoadFlowFactory> loadFlowFactoryClass, 
                            Class<? extends TableFormatterFactory> tableFormatterFactory) {
        if (threshold < 0) {
           throw new IllegalArgumentException("Negative values for threshold not permitted");
        }
        this.threshold = threshold;
        this.verbose = verbose;
        this.loadFlowFactory = Objects.requireNonNull(loadFlowFactoryClass);
        this.tableFormatterFactory = Objects.requireNonNull(tableFormatterFactory);
    }

    public float getThreshold() {
        return threshold;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public Class<? extends LoadFlowFactory> getLoadFlowFactory() {
        return loadFlowFactory;
    }

    public Class<? extends TableFormatterFactory> getTableFormatterFactory() {
        return tableFormatterFactory;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [" + 
                "threshold=" + threshold +
                ", verbose=" + verbose +
                ", loadFlowFactory=" + loadFlowFactory +
                ", tableFormatterFactory=" + tableFormatterFactory +
                "]";
    }
}
