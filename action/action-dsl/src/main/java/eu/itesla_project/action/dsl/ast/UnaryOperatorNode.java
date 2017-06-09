/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.action.dsl.ast;

import java.util.Objects;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public abstract class UnaryOperatorNode implements ExpressionNode {

    protected final ExpressionNode child;

    protected UnaryOperatorNode(ExpressionNode child) {
        this.child = Objects.requireNonNull(child);
    }

    public ExpressionNode getChild() {
        return child;
    }

}
