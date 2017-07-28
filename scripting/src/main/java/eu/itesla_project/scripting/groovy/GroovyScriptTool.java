/**
 * Copyright (c) 2016, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.scripting.groovy;

import com.google.auto.service.AutoService;
import eu.itesla_project.afs.core.AppData;
import eu.itesla_project.afs.core.AppFileSystemProvider;
import eu.itesla_project.afs.core.FileExtension;
import eu.itesla_project.afs.core.ProjectFileExtension;
import eu.itesla_project.commons.config.ComponentDefaultConfig;
import eu.itesla_project.commons.tools.Command;
import eu.itesla_project.commons.tools.Tool;
import eu.itesla_project.commons.tools.ToolRunningContext;
import eu.itesla_project.commons.util.ServiceLoaderCache;
import groovy.lang.Binding;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
@AutoService(Tool.class)
public class GroovyScriptTool implements Tool {

    private static final Command COMMAND = new Command() {
        @Override
        public String getName() {
            return "groovy-script";
        }

        @Override
        public String getTheme() {
            return "Script";
        }

        @Override
        public String getDescription() {
            return "run groovy script";
        }

        @Override
        public Options getOptions() {
            Options options = new Options();
            options.addOption(Option.builder()
                    .longOpt("script")
                    .desc("the groovy script")
                    .hasArg()
                    .required()
                    .argName("FILE")
                    .build());
            return options;
        }

        @Override
        public String getUsageFooter() {
            return null;
        }
    };

    private final ComponentDefaultConfig componentDefaultConfig;

    private final List<AppFileSystemProvider> fileSystemProviders;

    private final List<FileExtension> fileExtensions;

    private final List<ProjectFileExtension> projectFileExtensions;

    public GroovyScriptTool() {
        this(ComponentDefaultConfig.load(),
                new ServiceLoaderCache<>(AppFileSystemProvider.class).getServices(),
                new ServiceLoaderCache<>(FileExtension.class).getServices(),
                new ServiceLoaderCache<>(ProjectFileExtension.class).getServices());
    }

    public GroovyScriptTool(ComponentDefaultConfig componentDefaultConfig, List<AppFileSystemProvider> fileSystemProviders,
                            List<FileExtension> fileExtensions, List<ProjectFileExtension> projectFileExtensions) {
        this.componentDefaultConfig = Objects.requireNonNull(componentDefaultConfig);
        this.fileSystemProviders = Objects.requireNonNull(fileSystemProviders);
        this.fileExtensions = Objects.requireNonNull(fileExtensions);
        this.projectFileExtensions = Objects.requireNonNull(projectFileExtensions);
    }

    @Override
    public Command getCommand() {
        return COMMAND;
    }

    @Override
    public void run(CommandLine line, ToolRunningContext context) throws Exception {
        Path file = context.getFileSystem().getPath(line.getOptionValue("script"));
        Writer writer = new OutputStreamWriter(context.getOutputStream());
        try {
            Binding binding = new Binding();
            binding.setProperty("args", line.getArgs());
            try (AppData data = new AppData(context.getComputationManager(), componentDefaultConfig, fileSystemProviders,
                    fileExtensions, projectFileExtensions)) {
                GroovyScripts.run(file, data, binding, writer);
            }
        } finally {
            writer.flush();
        }
    }
}
