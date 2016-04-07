/**
 * Copyright (c) 2016, All partners of the iTesla project (http://www.itesla-project.eu/consortium)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.computation;

/**
 *
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public class AbstractExecutionListener implements ExecutionListener {

    @Override
    public void onExecutionStart(int fromExecutionIndex, int toExecutionIndex) {
    }

    @Override
    public void onExecutionCompletion(int executionIndex) {
    }

    @Override
    public void onEnd(ExecutionReport report) {
    }

}
