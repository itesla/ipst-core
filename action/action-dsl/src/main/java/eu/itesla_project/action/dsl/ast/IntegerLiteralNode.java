/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.action.dsl.ast;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public class IntegerLiteralNode extends LiteralNode {

    private final int value;

    public IntegerLiteralNode(int value) {
        this.value = value;
    }

    @Override
    public LiteralType getType() {
        return LiteralType.INTEGER;
    }

    @Override
    public Object getValue() {
        return value;
    }
}
