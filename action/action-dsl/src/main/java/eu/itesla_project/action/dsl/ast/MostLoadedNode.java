/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.action.dsl.ast;

import java.util.List;
import java.util.Objects;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian@rte-france.com>
 */
public class MostLoadedNode implements ExpressionNode {

    private final List<String> branchIds;

    public MostLoadedNode(List<String> branchIds) {
        this.branchIds = Objects.requireNonNull(branchIds);
        if (branchIds.isEmpty()) {
            throw new RuntimeException("List of branch to compare has to be greater or equal to one");
        }
    }

    public List<String> getBranchIds() {
        return branchIds;
    }

    @Override
    public <R, A> R accept(ExpressionVisitor<R, A> visitor, A arg) {
        return visitor.visitMostLoaded(this, arg);
    }
}
