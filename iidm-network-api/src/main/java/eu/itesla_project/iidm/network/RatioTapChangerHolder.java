/**
 * Copyright (c) 2016, All partners of the iTesla project (http://www.itesla-project.eu/consortium)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.iidm.network;

/**
 *
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public interface RatioTapChangerHolder {

    /**
     * Get a builder to create and associate a ratio tap changer to the
     * transformer.
     */
    RatioTapChangerAdder newRatioTapChanger();

    /**
     * Get the ratio tap changer.
     * <p>Could return <code>null</code> if the leg is not associated to a ratio
     * tap changer.
     */
    RatioTapChanger getRatioTapChanger();

}
