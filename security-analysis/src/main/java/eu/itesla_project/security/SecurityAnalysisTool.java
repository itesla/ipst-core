/**
 * Copyright (c) 2016, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.security;

import com.google.auto.service.AutoService;
import eu.itesla_project.commons.config.ComponentDefaultConfig;
import eu.itesla_project.commons.io.ForwardingOutputStream;
import eu.itesla_project.commons.io.table.AsciiTableFormatterFactory;
import eu.itesla_project.commons.io.table.CsvTableFormatterFactory;
import eu.itesla_project.commons.tools.Command;
import eu.itesla_project.commons.tools.Tool;
import eu.itesla_project.commons.tools.ToolRunningContext;
import eu.itesla_project.computation.local.LocalComputationManager;
import eu.itesla_project.contingency.ContingenciesProvider;
import eu.itesla_project.contingency.ContingenciesProviderFactory;
import eu.itesla_project.iidm.import_.Importers;
import eu.itesla_project.iidm.network.Network;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
@AutoService(Tool.class)
public class SecurityAnalysisTool implements Tool {

    @Override
    public Command getCommand() {
        return new Command() {
            @Override
            public String getName() {
                return "security-analysis";
            }

            @Override
            public String getTheme() {
                return "Computation";
            }

            @Override
            public String getDescription() {
                return "Run security analysis";
            }

            @Override
            public Options getOptions() {
                Options options = new Options();
                options.addOption(Option.builder().longOpt("case-file")
                        .desc("the case path")
                        .hasArg()
                        .argName("FILE")
                        .required()
                        .build());
                options.addOption(Option.builder().longOpt("limit-types")
                        .desc("limit type filter (all if not set)")
                        .hasArg()
                        .argName("LIMIT-TYPES")
                        .build());
                options.addOption(Option.builder().longOpt("output-csv")
                        .desc("the CSV output path")
                        .hasArg()
                        .argName("FILE")
                        .build());
                return options;
            }

            @Override
            public String getUsageFooter() {
                return "Where LIMIT-TYPES is one of " + Arrays.toString(LimitViolationType.values());
            }
        };
    }

    @Override
    public void run(CommandLine line, ToolRunningContext context) throws Exception {
        Path caseFile = context.getFileSystem().getPath(line.getOptionValue("case-file"));
        Set<LimitViolationType> limitViolationTypes = line.hasOption("limit-types")
                ? Arrays.stream(line.getOptionValue("limit-types").split(",")).map(LimitViolationType::valueOf).collect(Collectors.toSet())
                : EnumSet.allOf(LimitViolationType.class);
        Path csvFile = null;
        if (line.hasOption("output-csv")) {
            csvFile = context.getFileSystem().getPath(line.getOptionValue("output-csv"));
        }

        context.getOutputStream().println("Loading network '" + caseFile + "'");

        // load network
        Network network = Importers.loadNetwork(caseFile);
        if (network == null) {
            throw new RuntimeException("Case '" + caseFile + "' not found");
        }
        network.getStateManager().allowStateMultiThreadAccess(true);

        ComponentDefaultConfig defaultConfig = ComponentDefaultConfig.load();
        SecurityAnalysisFactory securityAnalysisFactory = defaultConfig.newFactoryImpl(SecurityAnalysisFactory.class);
        SecurityAnalysis securityAnalysis = securityAnalysisFactory.create(network, LocalComputationManager.getDefault(), 0);

        ContingenciesProviderFactory contingenciesProviderFactory = defaultConfig.newFactoryImpl(ContingenciesProviderFactory.class);
        ContingenciesProvider contingenciesProvider = contingenciesProviderFactory.create();

        // run security analysis on all N-1 lines
        SecurityAnalysisResult result = securityAnalysis.runAsync(contingenciesProvider)
                        .join();

        if (!result.getPreContingencyResult().isComputationOk()) {
            context.getErrorStream().println("Pre-contingency state divergence");
        }
        LimitViolationFilter limitViolationFilter = new LimitViolationFilter(limitViolationTypes);
        if (csvFile != null) {
            context.getOutputStream().println("Writing results to '" + csvFile + "'");
            CsvTableFormatterFactory csvTableFormatterFactory = new CsvTableFormatterFactory();
            Security.printPreContingencyViolations(result, Files.newBufferedWriter(csvFile, StandardCharsets.UTF_8), csvTableFormatterFactory, limitViolationFilter);
            Security.printPostContingencyViolations(result, Files.newBufferedWriter(csvFile, StandardCharsets.UTF_8, StandardOpenOption.APPEND), csvTableFormatterFactory, limitViolationFilter);
        } else {
            Writer writer = new OutputStreamWriter(context.getOutputStream()) {
                @Override
                public void close() throws IOException {
                    flush();
                }
            };
            AsciiTableFormatterFactory asciiTableFormatterFactory = new AsciiTableFormatterFactory();
            Security.printPreContingencyViolations(result, writer, asciiTableFormatterFactory, limitViolationFilter);
            Security.printPostContingencyViolations(result, writer, asciiTableFormatterFactory, limitViolationFilter);
        }
    }
}
