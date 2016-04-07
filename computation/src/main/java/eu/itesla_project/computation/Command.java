/**
 * Copyright (c) 2016, All partners of the iTesla project (http://www.itesla-project.eu/consortium)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.computation;

import java.util.List;

/**
 *
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public interface Command {

    public static final String EXECUTION_NUMBER_PATTERN = "${EXEC_NUM}";

    String getId();

    CommandType getType();

    List<InputFile> getInputFiles();

    List<InputFile> getInputFiles(String executionNumber);

    List<OutputFile> getOutputFiles();

    List<OutputFile> getOutputFiles(String executionNumber);

    // only used for display
    String toString(String executionNumber);

}
