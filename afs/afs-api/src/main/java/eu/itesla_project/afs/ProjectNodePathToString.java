/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.afs;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public enum ProjectNodePathToString implements Function<List<String>, String> {

    INSTANCE;

    @Override
    public String apply(List<String> path) {
        StringBuilder builder = new StringBuilder();
        Iterator<String> it = path.iterator();
        it.next(); // skip project node
        while (it.hasNext()) {
            builder.append(it.next());
            if (it.hasNext()) {
                builder.append(AppFileSystem.PATH_SEPARATOR);
            }
        }
        return builder.toString();
    }
}
