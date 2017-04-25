/**
 * Copyright (c) 2016, All partners of the iTesla project (http://www.itesla-project.eu/consortium)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.iidm.network.impl;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import eu.itesla_project.iidm.network.Country;
import eu.itesla_project.iidm.network.Substation;

/**
 *
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
class Substations {

    private Substations() {
    }

    static Iterable<Substation> filter(Iterable<Substation> substations,
                                       final Country country,
                                       final String tso,
                                       final String... geographicalTags) {
        if (geographicalTags.length == 0) {
            return substations;
        }
        return Iterables.filter(substations, new Predicate<Substation>() {
            @Override
            public boolean apply(Substation substation) {
                if (country != null && country == substation.getCountry()) {
                    return false;
                }
                if (tso != null && !tso.equals(substation.getTso())) {
                    return false;
                }
                for (String tag : geographicalTags) {
                    if (!substation.getGeographicalTags().contains(tag)) {
                        return false;
                    }
                }
                return true;
            }
        });
    }
}
