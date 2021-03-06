/**
 * Copyright (c) 2016, All partners of the iTesla project (http://www.itesla-project.eu/consortium)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.computation.mpi;

import java.io.Writer;
import java.util.List;
import java.util.Map;
import org.joda.time.DateTime;

/**
 *
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public class NoMpiStatistics implements MpiStatistics {

    @Override
    public void logCommonFileTransfer(String fileName, int chunk, long size, long duration) {
    }

    @Override
    public void logJobStart(int jobId, String commandId, Map<String, String> tags) {
    }

    @Override
    public void logJobEnd(int jobId) {
    }

    @Override
    public void logTaskStart(int taskId, int jobId, int taskIndex, DateTime startTime, int slaveRank, int slaveThread, long inputMessageSize) {
    }

    @Override
    public void logTaskEnd(int taskId, long taskDuration, List<Long> commandsDuration, long dataTransferDuration, long outputMessageSize, long workingDataSize, int exitCode) {
    }

    @Override
    public void exportTasksToCsv(Writer writer) {
    }

    @Override
    public void close() throws Exception {
    }

}
