/**
 * Copyright (c) 2016, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.commons.config;

import eu.itesla_project.commons.exceptions.UncheckedIllegalAccessException;
import eu.itesla_project.commons.exceptions.UncheckedInstantiationException;

import java.util.Objects;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 * @author Mathieu Bague <mathieu.bague at rte-france.com>
 */
public interface ComponentDefaultConfig {

    static ComponentDefaultConfig load() {
        return load(PlatformConfig.defaultConfig());
    }

    static ComponentDefaultConfig load(PlatformConfig platformConfig) {
        return new Impl(platformConfig.getModuleConfigIfExists("componentDefaultConfig"));
    }

    class Impl implements ComponentDefaultConfig {

        private final ModuleConfig config;

        public Impl(ModuleConfig config) {
            this.config = config;
        }

        @Override
        public <T> Class<? extends T> findFactoryImplClass(Class<T> factoryBaseClass) {
            Objects.requireNonNull(factoryBaseClass);
            String propertyName = factoryBaseClass.getSimpleName();
            if (config == null) {
                throw new RuntimeException("Property " + propertyName + " is not set");
            }
            return config.getClassProperty(propertyName, factoryBaseClass);
        }

        @Override
        public <T, U extends T> Class<? extends T> findFactoryImplClass(Class<T> factoryBaseClass, Class<U> defaultFactoryImplClass) {
            Objects.requireNonNull(factoryBaseClass);
            Objects.requireNonNull(defaultFactoryImplClass);
            String propertyName = factoryBaseClass.getSimpleName();
            return config != null ? config.getClassProperty(propertyName, factoryBaseClass, defaultFactoryImplClass)
                    : defaultFactoryImplClass;
        }

        @Override
        public <T> T newFactoryImpl(Class<T> factoryBaseClass) {
            try {
                return findFactoryImplClass(factoryBaseClass).newInstance();
            } catch (IllegalAccessException e) {
                throw new UncheckedIllegalAccessException(e);
            } catch (InstantiationException e) {
                throw new UncheckedInstantiationException(e);
            }
        }

        @Override
        public <T, U extends T> T newFactoryImpl(Class<T> factoryBaseClass, Class<U> defaultFactoryImplClass) {
            try {
                return findFactoryImplClass(factoryBaseClass, defaultFactoryImplClass).newInstance();
            } catch (IllegalAccessException e) {
                throw new UncheckedIllegalAccessException(e);
            } catch (InstantiationException e) {
                throw new UncheckedInstantiationException(e);
            }
        }
    }

    <T> Class<? extends T> findFactoryImplClass(Class<T> factoryBaseClass);

    <T, U extends T> Class<? extends T> findFactoryImplClass(Class<T> factoryBaseClass, Class<U> defaultFactoryImplClass);

    <T> T newFactoryImpl(Class<T> factoryBaseClass);

    <T, U extends T> T newFactoryImpl(Class<T> factoryBaseClass, Class<U> defaultFactoryImplClass);
}
