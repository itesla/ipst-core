/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.afs.nio2.ext;

import eu.itesla_project.afs.ext.Case;
import eu.itesla_project.afs.ext.ImportedCase;
import eu.itesla_project.afs.ext.ImportedCaseBuilder;
import eu.itesla_project.afs.nio2.Nio2ProjectFolder;
import eu.itesla_project.iidm.datasource.FileDataSource;
import eu.itesla_project.iidm.parameters.Parameter;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public class Nio2ImportedCaseBuilder implements ImportedCaseBuilder {

    private final Nio2ProjectFolder folder;

    private Case aCase;

    private final Map<String, String> parameterValues = new HashMap<>();

    public Nio2ImportedCaseBuilder(Nio2ProjectFolder folder) {
        this.folder = Objects.requireNonNull(folder);
    }

    @Override
    public Class<ImportedCase> getProjectFileClass() {
        return ImportedCase.class;
    }

    @Override
    public Nio2ImportedCaseBuilder withCase(Case aCase) {
        this.aCase = Objects.requireNonNull(aCase);
        return this;
    }

    @Override
    public ImportedCaseBuilder withParameter(String name, String value) {
        parameterValues.put(Objects.requireNonNull(name), Objects.requireNonNull(value));
        return this;
    }

    @Override
    public ImportedCaseBuilder withParameters(Map<String, String> parameters) {
        parameterValues.putAll(Objects.requireNonNull(parameters));
        return this;
    }

    @Override
    public Nio2ImportedCase build() {
        if (aCase == null) {
            throw new RuntimeException("Case is not set");
        }

        // create the directory
        Path importedCaseDir = folder.getDir().resolve(aCase.getDataSource().getBaseName());
        try {
            Files.createDirectories(importedCaseDir);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        // create metadata
        Nio2ImportedCase.Metadata metadata = Nio2ImportedCase.Metadata.create();
        metadata.save(importedCaseDir);

        // create import configuration file
        Nio2ImportedCase.ImportConfiguration importConfiguration = new Nio2ImportedCase.ImportConfiguration();
        importConfiguration.setFormat(aCase.getImporter().getFormat());
        Map<String, Parameter> parameters = aCase.getImporter().getParameters().stream()
                .collect(Collectors.toMap(parameter -> parameter.getName(), Function.identity())); // index importer parameters by name
        parameterValues.forEach((name, value) -> {
            Parameter parameter = parameters.get(name);
            if (parameter == null) {
                throw new RuntimeException("Parameter '" + name + "' not found");
            }
            Nio2ImportedCase.ImportConfiguration.Parameter p = new Nio2ImportedCase.ImportConfiguration.Parameter();
            p.setName(name);
            p.setValue(value);
            importConfiguration.getParameter().add(p);
        });
        importConfiguration.save(importedCaseDir);

        // copy case data
        aCase.getImporter().copy(aCase.getDataSource(), new FileDataSource(importedCaseDir, aCase.getDataSource().getBaseName()));

        // create project node
        Nio2ImportedCase importedCase = new Nio2ImportedCase(importedCaseDir, folder);

        // put id in the central directory
        folder.getProject().getCentralDirectory().add(metadata.getId(), importedCase.getPath().toString());

        return importedCase;
    }

}
