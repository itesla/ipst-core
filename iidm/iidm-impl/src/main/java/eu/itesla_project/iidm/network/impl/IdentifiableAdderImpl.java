/**
 * Copyright (c) 2016, All partners of the iTesla project (http://www.itesla-project.eu/consortium)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.iidm.network.impl;

import eu.itesla_project.commons.ITeslaException;
import eu.itesla_project.iidm.network.Identifiable;

/**
 *
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
abstract class IdentifiableAdderImpl<T extends IdentifiableAdderImpl<T>> implements Validable {

    private String id;

    private boolean ensureIdUnicity = false;

    private String name;

    IdentifiableAdderImpl() {
    }

    protected abstract NetworkImpl getNetwork();

    protected abstract String getTypeDescription();

    public T setId(String id) {
        this.id = id;
        return (T) this;
    }

    public T setEnsureIdUnicity(boolean ensureIdUnicity) {
        this.ensureIdUnicity = ensureIdUnicity;
        return (T) this;
    }

    public T setName(String name) {
        this.name = name;
        return (T) this;
    }

    protected String checkAndGetUniqueId() {
        if (id == null) {
            throw new ITeslaException(getTypeDescription() + " id is not set");
        }
        String uniqueId;
        if (ensureIdUnicity) {
            uniqueId = getNetwork().getObjectStore().getUniqueId(id);
        } else {
            if (getNetwork().getObjectStore().contains(id)) {
                Identifiable obj = getNetwork().getObjectStore().get(id);
                throw new ITeslaException("The network " + getNetwork().getId()
                        + " already contains an object '" + obj.getClass().getSimpleName()
                        + "' with the id '" + id + "'");
            }
            uniqueId = id;
        }
        return uniqueId;
    }

    protected String getName() {
        return name;
    }

    @Override
    public String getMessageHeader() {
        return getTypeDescription() + " '" + id + "': ";
    }

}
