/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.action.simulator.tool;

import eu.itesla_project.action.simulator.loadflow.AbstractLoadFlowActionSimulatorObserver;
import eu.itesla_project.contingency.Contingency;
import eu.itesla_project.iidm.network.Network;
import eu.itesla_project.security.LimitViolation;
import eu.itesla_project.security.PostContingencyResult;
import eu.itesla_project.security.PreContingencyResult;
import eu.itesla_project.security.SecurityAnalysisResult;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public abstract class SecurityAnalysisResultBuilder extends AbstractLoadFlowActionSimulatorObserver {

    private PreContingencyResult preContingencyResult;

    private final Map<String, PostContingencyResult> postContingencyResults = new HashMap<>();

    private final List<String> preContingencyActions = new ArrayList<>();

    private final Map<String, List<String>> postContingencyActions = new HashMap<>();

    private boolean precontingency;

    @Override
    public void beforePreContingencyAnalysis(Network network) {
        precontingency = true;
        preContingencyResult = null;
    }

    @Override
    public void afterPreContingencyAnalysis() {
        precontingency = false;
        postContingencyResults.clear();
    }

    @Override
    public void loadFlowDiverged(Contingency contingency) {
        if (precontingency) {
            preContingencyResult = new PreContingencyResult(false, Collections.emptyList(), Collections.emptyList());
        } else {
            postContingencyResults.put(contingency.getId(), new PostContingencyResult(contingency, false, Collections.emptyList(), Collections.emptyList()));
        }
    }

    @Override
    public void loadFlowConverged(Contingency contingency, List<LimitViolation> violations) {
        if (precontingency) {
            preContingencyResult = new PreContingencyResult(true, violations, preContingencyActions);
        } else {
            postContingencyResults.put(contingency.getId(), new PostContingencyResult(contingency,
                                                                                      true,
                                                                                      violations,
                                                                                      getPostContingencyActions(contingency)));
        }
    }

    private List<String> getPostContingencyActions(Contingency contingency) {
        List<String> actions = postContingencyActions.get(contingency.getId());
        if (actions == null) {
            actions = new ArrayList<>();
            postContingencyActions.put(contingency.getId(), actions);
        }
        return actions;
    }

    @Override
    public void afterAction(Contingency contingency, String actionId) {
        if (precontingency) {
            preContingencyActions.add(actionId);
        } else {
            getPostContingencyActions(contingency).add(actionId);
        }
    }

    @Override
    public void afterPostContingencyAnalysis() {
        onFinalStateResult(new SecurityAnalysisResult(preContingencyResult,
                                                      postContingencyResults.entrySet().stream().map(e -> e.getValue()).collect(Collectors.toList())));
    }

    public abstract void onFinalStateResult(SecurityAnalysisResult result);
}
