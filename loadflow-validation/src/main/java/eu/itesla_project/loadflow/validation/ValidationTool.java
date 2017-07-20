/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.loadflow.validation;

import com.google.auto.service.AutoService;
import eu.itesla_project.commons.tools.Command;
import eu.itesla_project.commons.tools.Tool;
import eu.itesla_project.commons.tools.ToolRunningContext;
import eu.itesla_project.iidm.import_.Importers;
import eu.itesla_project.iidm.network.Network;
import eu.itesla_project.iidm.network.StateManager;
import eu.itesla_project.loadflow.api.LoadFlow;
import eu.itesla_project.loadflow.api.LoadFlowParameters;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 * @author Massimo Ferraro <massimo.ferraro@techrain.it>
 */
@AutoService(Tool.class)
public class ValidationTool implements Tool {
    
    private static Command COMMAND = new Command() {

        @Override
        public String getName() {
            return "loadflow-validation";
        }

        @Override
        public String getTheme() {
            return "Computation";
        }

        @Override
        public String getDescription() {
            return "Validate load-flow results of a network";
        }

        @Override
        public Options getOptions() {
            Options options = new Options();
            options.addOption(Option.builder().longOpt("case-file")
                    .desc("case file path")
                    .hasArg()
                    .argName("FILE")
                    .required()
                    .build());
            options.addOption(Option.builder().longOpt("output-file")
                    .desc("output file path")
                    .hasArg()
                    .argName("FILE")
                    .required()
                    .build());
            options.addOption(Option.builder().longOpt("load-flow")
                    .desc("run loadflow")
                    .build());
            options.addOption(Option.builder().longOpt("verbose")
                    .desc("verbose output")
                    .build());
            options.addOption(Option.builder().longOpt("output-format")
                    .desc("output format (CSV/CSV_MULTILINE)")
                    .hasArg()
                    .argName("VALIDATION_WRITER")
                    .build());
            options.addOption(Option.builder().longOpt("type")
                    .desc("validation type (FLOWS/GENERATORS/...)")
                    .hasArg()
                    .argName("VALIDATION_TYPE")
                    .required()
                    .build());
            return options;
        }

        @Override
        public String getUsageFooter() {
            return null;
        }

    };

    @Override
    public Command getCommand() {
        return COMMAND;
    }

    @Override
    public void run(CommandLine line, ToolRunningContext context) throws Exception {
        Path caseFile = Paths.get(line.getOptionValue("case-file"));
        Path outputFile = Paths.get(line.getOptionValue("output-file"));
        ValidationType validationType = ValidationType.valueOf(line.getOptionValue("type"));
        ValidationConfig config = ValidationConfig.load();
        if (line.hasOption("verbose")) {
            config.setVerbose(true);
        }
        if (line.hasOption("output-format")) {
            config.setValidationOutputWriter(ValidationOutputWriter.valueOf(line.getOptionValue("output-format")));
        }
        context.getOutputStream().println("Loading case " + caseFile);
        Network network = Importers.loadNetwork(caseFile);
        if (network == null) {
            throw new RuntimeException("Case " + caseFile + " not found");
        }
        if (line.hasOption("load-flow")) {
            context.getOutputStream().println("Running loadflow on network " + network.getId());
            LoadFlowParameters parameters = LoadFlowParameters.load();
            LoadFlow loadFlow = config.getLoadFlowFactory().newInstance().create(network, context.getComputationManager(), 0);
            loadFlow.runAsync(StateManager.INITIAL_STATE_ID, parameters)
                    .thenAccept(loadFlowResult -> {
                        if (!loadFlowResult.isOk()) {
                            throw new RuntimeException("Loadflow on network " + network.getId() + " does not converge");
                        }
                    })
                    .join();
        }
        context.getOutputStream().println("Validate load-flow results of network " + network.getId() + " - validation type: " + validationType + " - result: " + (Validation.check(validationType, network, config, outputFile) ? "success" : "fail"));
    }
    
}
