/**
 * Copyright (c) 2016, All partners of the iTesla project (http://www.itesla-project.eu/consortium)
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.security;

import java.util.Collection;
import java.util.Objects;
import java.util.Properties;

import eu.itesla_project.iidm.network.Country;
import eu.itesla_project.iidm.network.Identifiable;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public class LimitViolation {

    @Deprecated
    private final Identifiable subject;
    
    private final String subjectId;

    private final LimitViolationType limitType;

    private final float limit;

    private final String limitName;

    private final float limitReduction;

    private final float value;

    private final Country country;

    private final float baseVoltage;

    @Deprecated
    public LimitViolation(Identifiable subject, LimitViolationType limitType, float limit, String limitName, float limitReduction, float value, Country country, float baseVoltage) {
        this.subject = Objects.requireNonNull(subject);
        this.subjectId = subject.getId();
        this.limitType = Objects.requireNonNull(limitType);
        this.limit = limit;
        this.limitName = limitName;
        this.limitReduction = limitReduction;
        this.value = value;
        this.country = country;
        this.baseVoltage = baseVoltage;
    }
    
    public LimitViolation(String subjectId, LimitViolationType limitType, float limit, String limitName, float limitReduction, float value, Country country, float baseVoltage) {
        this.subjectId = Objects.requireNonNull(subjectId);
        this.subject = new IdentifiableImpl(subjectId); // to avoid null pointer exceptions on getSubject().getId()
        this.limitType = Objects.requireNonNull(limitType);
        this.limit = limit;
        this.limitName = limitName;
        this.limitReduction = limitReduction;
        this.value = value;
        this.country = country;
        this.baseVoltage = baseVoltage;
    }

    @Deprecated
    public LimitViolation(Identifiable subject, LimitViolationType limitType, float limit, String limitName, float value) {
        this(subject, limitType, limit, limitName, 1, value, null, Float.NaN);
    }
    
    public LimitViolation(String subjectId, LimitViolationType limitType, float limit, String limitName, float value) {
        this(subjectId, limitType, limit, limitName, 1, value, null, Float.NaN);
    }

    @Deprecated
    public Identifiable getSubject() {
        return subject;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public LimitViolationType getLimitType() {
        return limitType;
    }

    public float getLimit() {
        return limit;
    }

    public String getLimitName() {
        return limitName;
    }

    public float getLimitReduction() {
        return limitReduction;
    }

    public float getValue() {
        return value;
    }

    public Country getCountry() {
        return country;
    }

    public float getBaseVoltage() {
        return baseVoltage;
    }
    
    @Deprecated
    class IdentifiableImpl implements Identifiable {
        
        private String id;
        
        IdentifiableImpl(String id) {
            this.id = id;
        }

        @Override
        public String getId() {
            return id;
        }

        @Override
        public String getName() {
            return null;
        }

        @Override
        public boolean hasProperty() {
            return false;
        }

        @Override
        public Properties getProperties() {
            return null;
        }

        @Override
        public void addExtension(Class type, Extension extension) {
        }

        @Override
        public Extension getExtension(Class type) {
            return null;
        }

        @Override
        public boolean removeExtension(Class type) {
            return false;
        }

        @Override
        public Collection getExtensions() {
            return null;
        }
        
    }
}
