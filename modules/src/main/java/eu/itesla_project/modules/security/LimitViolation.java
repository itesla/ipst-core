/**
 * Copyright (c) 2016, All partners of the iTesla project (http://www.itesla-project.eu/consortium)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.modules.security;

import eu.itesla_project.iidm.network.Country;
import eu.itesla_project.iidm.network.Identifiable;

import java.util.Objects;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public class LimitViolation implements SecurityIssue {

    private final Identifiable subject;

    private final LimitViolationType limitType;

    private final float limit;

    private final float limitReduction;

    private final float value;

    private final Country country;

    private final float baseVoltage;

    public LimitViolation(Identifiable subject, LimitViolationType limitType, float limit, float limitReduction, float value, Country country, float baseVoltage) {
        this.subject = Objects.requireNonNull(subject);
        this.limitType = Objects.requireNonNull(limitType);
        this.limit = limit;
        this.limitReduction = limitReduction;
        this.value = value;
        this.country = country;
        this.baseVoltage = baseVoltage;
    }

    public LimitViolation(Identifiable subject, LimitViolationType limitType, float limit, float value) {
        this(subject, limitType, limit, 1, value, null, Float.NaN);
    }

    @Override
    public SecurityIssueType getIssueType() {
        return SecurityIssueType.LIMIT;
    }

    public Identifiable getSubject() {
        return subject;
    }

    public LimitViolationType getLimitType() {
        return limitType;
    }

    public float getLimit() {
        return limit;
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
}
