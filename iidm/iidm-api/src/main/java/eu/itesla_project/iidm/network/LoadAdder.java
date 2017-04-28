/**
 * Copyright (c) 2016, All partners of the iTesla project (http://www.itesla-project.eu/consortium)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.iidm.network;

/**
 * To create a load, from a <code>VoltageLevel</code> instance call
 * the {@link VoltageLevel#newLoad()} method to get a load builder
 * instance.
 * <p>
 * Example:
 *<pre>
 *    VoltageLevel vl = ...
 *    Load l = vl.newLoad()
 *            .setId("l1")
 *            ...
 *        .add();
 *</pre>
 *
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 * @see Load
 * @see VoltageLevel
 */
public interface LoadAdder extends SingleTerminalConnectableAdder<LoadAdder> {

    LoadAdder setLoadType(LoadType loadType);

    LoadAdder setP0(float p0);

    LoadAdder setQ0(float q0);

    Load add();

}
