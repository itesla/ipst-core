/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.afs.nio2;

import java.util.List;
import java.util.function.Function;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public enum Nio2NodePathToString implements Function<List<String>, String> {

    INSTANCE;
    
    @Override
    public String apply(List<String> path) {
        StringBuilder builder = new StringBuilder();
        builder.append(path.get(0))
                .append(Nio2AppFileSystem.FS_SEPARATOR);
        for (int i = 1; i < path.size(); i++) {
            builder.append(Nio2AppFileSystem.PATH_SEPARATOR).append(path.get(i));
        }
        return builder.toString();
    }
}
