/**
 * Copyright (c) 2016, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.loadflow.tools;

import com.google.auto.service.AutoService;
import eu.itesla_project.commons.ITeslaException;
import eu.itesla_project.commons.config.ComponentDefaultConfig;
import eu.itesla_project.commons.io.table.*;
import eu.itesla_project.commons.tools.Command;
import eu.itesla_project.commons.tools.Tool;
import eu.itesla_project.commons.tools.ToolRunningContext;
import eu.itesla_project.computation.ComputationManager;
import eu.itesla_project.computation.local.LocalComputationManager;
import eu.itesla_project.iidm.datasource.FileDataSource;
import eu.itesla_project.iidm.export.Exporter;
import eu.itesla_project.iidm.export.Exporters;
import eu.itesla_project.iidm.import_.ImportConfig;
import eu.itesla_project.iidm.import_.Importers;
import eu.itesla_project.iidm.network.Network;
import eu.itesla_project.loadflow.api.LoadFlow;
import eu.itesla_project.loadflow.api.LoadFlowFactory;
import eu.itesla_project.loadflow.api.LoadFlowResult;
import eu.itesla_project.loadflow.json.LoadFlowResultSerializer;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Properties;

/**
 * @author Christian Biasuzzi <christian.biasuzzi@techrain.it>
 */
@AutoService(Tool.class)
public class RunLoadFlowTool implements Tool {

    private enum Format {
        CSV,
        JSON
    }

    @Override
    public Command getCommand() {
        return new Command() {
            @Override
            public String getName() {
                return "loadflow";
            }

            @Override
            public String getTheme() {
                return "Computation";
            }

            @Override
            public String getDescription() {
                return "Run loadflow";
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
                options.addOption(Option.builder().longOpt("output-file")
                        .desc("loadflow results output path")
                        .hasArg()
                        .argName("FILE")
                        .build());
                options.addOption(Option.builder().longOpt("output-format")
                        .desc("loadflow results output format " + Arrays.toString(Format.values()))
                        .hasArg()
                        .argName("FORMAT")
                        .build());
                options.addOption(Option.builder().longOpt("skip-postproc")
                        .desc("skip network importer post processors (when configured)")
                        .build());
                options.addOption(Option.builder().longOpt("output-case-format")
                        .desc("modified network output format " + Exporters.getFormats())
                        .hasArg()
                        .argName("CASEFORMAT")
                        .build());
                options.addOption(Option.builder().longOpt("output-case-dir")
                        .desc("modified network output directory")
                        .hasArg()
                        .argName("DIR")
                        .build());
                options.addOption(Option.builder().longOpt("output-case-basename")
                        .desc("modified network base name")
                        .hasArg()
                        .argName("NAME")
                        .build());
                return options;
            }

            @Override
            public String getUsageFooter() {
                return null;
            }
        };
    }

    @Override
    public void run(CommandLine line, ToolRunningContext context) throws Exception {
        Path caseFile = context.getFileSystem().getPath(line.getOptionValue("case-file"));
        boolean skipPostProc = line.hasOption("skip-postproc");
        Path outputFile = null;
        Format format = null;
        ComponentDefaultConfig defaultConfig = ComponentDefaultConfig.load();

        try (ComputationManager computationManager = new LocalComputationManager()) {
            ImportConfig importConfig = (!skipPostProc) ? ImportConfig.load() : new ImportConfig();
            // process a single network: output-file/output-format options available
            if (line.hasOption("output-file")) {
                outputFile = context.getFileSystem().getPath(line.getOptionValue("output-file"));
                if (!line.hasOption("output-format")) {
                    throw new ParseException("Missing required option: output-format");
                }
                format = Format.valueOf(line.getOptionValue("output-format"));
            }

            Exporter modifiedNetworkExporter = null;
            if (line.hasOption("output-case-format") || line.hasOption("output-case-dir") || (line.hasOption("output-case-basename"))) {
                Arrays.asList("output-case-format", "output-case-dir", "output-case-basename")
                        .forEach(paramName -> {
                            if (!line.hasOption(paramName)) {
                                throw new RuntimeException("Missing required option:" + paramName);
                            }
                        });
                String outputCaseFormat = line.getOptionValue("output-case-format");
                modifiedNetworkExporter = Exporters.getExporter(outputCaseFormat);
                if (modifiedNetworkExporter == null) {
                    throw new ITeslaException("Target format " + outputCaseFormat + " not supported");
                }
            }

            context.getOutputStream().println("Loading network '" + caseFile + "'");
            Network network = Importers.loadNetwork(caseFile, computationManager, importConfig, null);
            if (network == null) {
                throw new RuntimeException("Case '" + caseFile + "' not found");
            }
            LoadFlow loadFlow = defaultConfig.newFactoryImpl(LoadFlowFactory.class).create(network, computationManager, 0);
            LoadFlowResult result = loadFlow.run();

            if (outputFile != null) {
                exportResult(result, context, outputFile, format);
            } else {
                printResult(result, context);
            }

            // exports the modified network to the filesystem, if requested
            if (modifiedNetworkExporter != null) {
                modifiedNetworkExporter.export(network,
                        new Properties(),
                        new FileDataSource(context.getFileSystem().getPath(line.getOptionValue("output-case-dir")),
                                line.getOptionValue("output-case-basename")));
            }
        }
    }


    private TableFormatter getAsciiFormatter(TableFormatterConfig formatterConfig, ToolRunningContext context) {
        Writer writer = new OutputStreamWriter(context.getOutputStream()) {
            @Override
            public void close() throws IOException {
                flush();
            }
        };
        AsciiTableFormatterFactory asciiTableFormatterFactory = new AsciiTableFormatterFactory();
        return asciiTableFormatterFactory.create(writer,
                "loadflow results",
                formatterConfig,
                new Column("Network"),
                new Column("Result"),
                new Column("Metrics"));
    }

    private void printTableEntry(TableFormatter formatter, Network network, LoadFlowResult result) {
        try {
            formatter.writeCell(network.getId());
            formatter.writeCell(result.isOk());
            formatter.writeCell(result.getMetrics().toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void printLoadFlowResult(LoadFlowResult result, Writer writer, TableFormatterFactory formatterFactory,
                                     TableFormatterConfig formatterConfig) {
        try (TableFormatter formatter = formatterFactory.create(writer,
                "loadflow results",
                formatterConfig,
                new Column("Result"),
                new Column("Metrics"))) {
            formatter.writeCell(result.isOk());
            formatter.writeCell(result.getMetrics().toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void printResult(LoadFlowResult result, ToolRunningContext context) {
        Writer writer = new OutputStreamWriter(context.getOutputStream()) {
            @Override
            public void close() throws IOException {
                flush();
            }
        };
        AsciiTableFormatterFactory asciiTableFormatterFactory = new AsciiTableFormatterFactory();
        printLoadFlowResult(result, writer, asciiTableFormatterFactory, TableFormatterConfig.load());
    }


    private void exportResult(LoadFlowResult result, ToolRunningContext context, Path outputFile, Format format) throws IOException {
        context.getOutputStream().println("Writing results to '" + outputFile + "'");
        switch (format) {
            case CSV:
                CsvTableFormatterFactory csvTableFormatterFactory = new CsvTableFormatterFactory();
                printLoadFlowResult(result, Files.newBufferedWriter(outputFile, StandardCharsets.UTF_8), csvTableFormatterFactory, TableFormatterConfig.load());
                break;

            case JSON:
                LoadFlowResultSerializer.write(result, outputFile);
                break;
        }
    }
}
