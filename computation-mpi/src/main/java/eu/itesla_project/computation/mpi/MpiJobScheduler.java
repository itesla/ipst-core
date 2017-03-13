/**
 * Copyright (c) 2016, All partners of the iTesla project (http://www.itesla-project.eu/consortium)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.computation.mpi;

import eu.itesla_project.computation.CommandExecution;
import eu.itesla_project.computation.ExecutionListener;
import eu.itesla_project.computation.ExecutionReport;

import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public interface MpiJobScheduler {

    MpiResources getResources();

    String getVersion();

    int getStartedTasksAndReset();

    void sendCommonFile(CommonFile commonFile);

    CompletableFuture<ExecutionReport> execute(CommandExecution execution, Path workingDir, Map<String, String> variables, ExecutionListener listener);

    void shutdown() throws Exception;

}
