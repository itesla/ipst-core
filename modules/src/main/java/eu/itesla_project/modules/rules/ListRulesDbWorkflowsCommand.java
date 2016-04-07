/**
 * Copyright (c) 2016, All partners of the iTesla project (http://www.itesla-project.eu/consortium)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.modules.rules;

import eu.itesla_project.commons.tools.Command;
import eu.itesla_project.modules.offline.OfflineConfig;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

/**
 *
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public class ListRulesDbWorkflowsCommand implements Command {

    public static final ListRulesDbWorkflowsCommand INSTANCE = new ListRulesDbWorkflowsCommand();

    @Override
    public String getName() {
        return "list-rules-db-workflows";
    }

    @Override
    public String getTheme() {
        return "Security rules DB";
    }

    @Override
    public String getDescription() {
        return "list rules db offline workflows";
    }

    @Override
    @SuppressWarnings("static-access")
    public Options getOptions() {
        Options options = new Options();
        options.addOption(Option.builder().longOpt("rules-db-name")
                .desc("the rules db name (default is " + OfflineConfig.DEFAULT_RULES_DB_NAME + ")")
                .hasArg()
                .argName("NAME")
                .build());
        return options;
    }

    @Override
    public String getUsageFooter() {
        return null;
    }

}
