/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.action.dsl.ast;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian@rte-france.com>
 */
public class LogicalNotOperator extends UnaryOperatorNode {

    public LogicalNotOperator(ExpressionNode child) {
        super(child);
    }


    @Override
    public <R, A> R accept(ExpressionVisitor<R, A> visitor, A arg) {
        return visitor.visitNotOperator(this, arg);
    }
}