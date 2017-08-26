/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.afs.storage;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public class NodeEvent implements Serializable {

    private static final long serialVersionUID = -4617843206213760193L;

    public enum Type {
        NODE_CREATED,
        NODE_REMOVED,
        NODE_MOVED,
        NODE_ATTRIBUTE_CHANGED,
        NODE_DEPENDENCY_CHANGED
    }

    private final NodeId id;

    private final Type type;

    public NodeEvent(NodeId nodeId, Type type) {
        this.id = Objects.requireNonNull(nodeId);
        this.type = Objects.requireNonNull(type);
    }

    public NodeId getId() {
        return id;
    }

    public Type getType() {
        return type;
    }
}
