/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.action.simulator.tool;

import eu.itesla_project.contingency.Contingency;
import eu.itesla_project.contingency.ContingencyImpl;
import eu.itesla_project.security.*;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

/**
 * @author Mathieu Bague <mathieu.bague@rte-france.com>
 */
public class SecurityAnalysisResultBuilderTest {

    private Contingency createContingency() {
        return new ContingencyImpl("contingency", Collections.emptyList());
    }

    private List<LimitViolation> createPreContingencyViolations() {
        return Collections.singletonList(new LimitViolation("line1", LimitViolationType.CURRENT, 100, "IST", 0.0f, 101, null, 63.0f));
    }

    private List<LimitViolation> createPostContingencyViolations() {
        return Collections.singletonList(new LimitViolation("line2", LimitViolationType.CURRENT, 100, "IST", 0.0f, 110, null, 380.0f));
    }

    private void testLimitViolation(LimitViolationsResult result, boolean convergent, List<String> equipmentsId, List<String> actionsId) {
        assertEquals(convergent, result.isComputationOk());
        assertEquals(actionsId, result.getActionsTaken());
        if (convergent) {
            assertEquals(1, result.getLimitViolations().size());
            assertEquals(equipmentsId, result.getLimitViolations().stream().map(LimitViolation::getSubjectId).collect(Collectors.toList()));
        }
    }

    private void testSARBuilder(final boolean convergent) {
        SecurityAnalysisResultBuilder builder = new SecurityAnalysisResultBuilder() {
            @Override
            public void onFinalStateResult(SecurityAnalysisResult result) {

                System.out.println("test1");
                testLimitViolation(result.getPreContingencyResult(), convergent, Collections.singletonList("line1"), Collections.singletonList("pre-action"));

                List<PostContingencyResult> postContingencyResults = result.getPostContingencyResults();
                assertEquals(1, postContingencyResults.size());

                PostContingencyResult postContingencyResult = postContingencyResults.get(0);
                assertEquals("contingency", postContingencyResult.getContingency().getId());
                assertEquals(0, postContingencyResult.getContingency().getElements().size());

                LimitViolationsResult postContingencyLimitViolationsResult = postContingencyResult.getLimitViolationsResult();
                System.out.println("test2");
                testLimitViolation(postContingencyLimitViolationsResult, convergent, Collections.singletonList("line2"), Arrays.asList("post-action1", "post-action2"));
            }
        };

        builder.beforePreContingencyAnalysis(null);
        builder.afterAction(null, "pre-action");
        if (convergent) {
            builder.loadFlowConverged(null, createPreContingencyViolations());
        } else {
            builder.loadFlowDiverged(null);
        }
        builder.afterPreContingencyAnalysis();

        Contingency contingency = createContingency();
        builder.afterAction(contingency, "post-action1");
        builder.afterAction(contingency, "post-action2");
        if (convergent) {
            builder.loadFlowConverged(contingency, createPostContingencyViolations());
        } else {
            builder.loadFlowDiverged(contingency);
        }

        builder.afterPostContingencyAnalysis();
    }

    @Test
    public void testSARBuilder() {
        testSARBuilder(true);
        testSARBuilder(false);
    }
}
