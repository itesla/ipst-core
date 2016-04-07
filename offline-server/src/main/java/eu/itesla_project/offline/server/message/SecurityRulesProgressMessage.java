/**
 * Copyright (c) 2016, All partners of the iTesla project (http://www.itesla-project.eu/consortium)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.offline.server.message;

import javax.json.stream.JsonGenerator;

/**
 *
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public class SecurityRulesProgressMessage extends Message {

    private final String workflowId;
    private final Float progress;

    public SecurityRulesProgressMessage(String workflowId, Float progress) {
        this.workflowId = workflowId;
        this.progress = progress;
    }

    @Override
    protected String getType() {
        return "securityRulesProgress";
    }

    @Override
    public void toJson(JsonGenerator generator) {
        generator.write("workflowId", workflowId);
        generator.write("progress", progress);
    }

}
