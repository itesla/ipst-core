/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.afs.nio2.ext;

import eu.itesla_project.afs.FileIcon;
import eu.itesla_project.afs.ProjectFile;
import eu.itesla_project.afs.ext.ImportedCase;
import eu.itesla_project.afs.nio2.Metadata;
import eu.itesla_project.afs.nio2.Nio2Project;
import eu.itesla_project.afs.nio2.Nio2ProjectFolder;
import eu.itesla_project.afs.nio2.Nio2ProjectNode;
import eu.itesla_project.commons.jaxb.JaxbUtil;
import eu.itesla_project.iidm.datasource.GenericReadOnlyDataSource;
import eu.itesla_project.iidm.datasource.ReadOnlyDataSource;
import eu.itesla_project.iidm.import_.ImportConfig;
import eu.itesla_project.iidm.import_.Importer;
import eu.itesla_project.iidm.import_.Importers;

import javax.xml.bind.annotation.*;
import java.nio.file.Path;
import java.util.*;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public class Nio2ImportedCase extends Nio2ProjectNode implements ImportedCase {

    @XmlRootElement(name = "importConfiguration")
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class ImportConfiguration {

        public static final String XML_FILE_NAME = "importConfiguration.xml";

        public static ImportConfiguration read(Path dir) {
            return JaxbUtil.unmarchallFile(ImportConfiguration.class, dir.resolve(XML_FILE_NAME));
        }

        public ImportConfiguration() {
        }

        @XmlElement(required = true)
        private List<Parameter> parameter = new ArrayList<>();

        @XmlAttribute(name = "format", required = true)
        protected String format;

        public List<Parameter> getParameter() {
            return this.parameter;
        }

        public String getFormat() {
            return format;
        }

        public void setFormat(String format) {
            this.format = format;
        }

        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = { "value" })
        public static class Parameter {

            @XmlAttribute(name = "name", required = true)
            private String name;

            @XmlValue
            private String value;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }
        }

        public void save(Path dir) {
            JaxbUtil.marshallElement(ImportConfiguration.class, this, dir.resolve(XML_FILE_NAME));
        }
    }

    private final Nio2ProjectFolder parent;

    Nio2ImportedCase(Path dir, Nio2ProjectFolder parent) {
        super(dir, parent.getProject().getCentralDirectory());
        this.parent = Objects.requireNonNull(parent);
    }

    @Override
    public Nio2ProjectFolder getParent() {
        checkNotDeleted();
        return parent;
    }

    @Override
    public Nio2Project getProject() {
        return parent.getProject();
    }

    @Override
    public boolean isFolder() {
        checkNotDeleted();
        return false;
    }

    @Override
    public FileIcon getIcon() {
        checkNotDeleted();
        return CaseIconCache.INSTANCE.get(getProject().getFileSystem().getImportersLoader(),
                                          getProject().getFileSystem().getComputationManager(),
                                          ImportConfiguration.read(dir).getFormat());
    }

    @Override
    public ReadOnlyDataSource getDataSource() {
        return new GenericReadOnlyDataSource(dir, getName());
    }

    private Importer getImporter(Metadata metadata) {
        return Importers.getImporter(getProject().getFileSystem().getImportersLoader(), ImportConfiguration.read(dir).getFormat(),
                                     getProject().getFileSystem().getComputationManager(), new ImportConfig());
    }

    @Override
    public Importer getImporter() {
        return getImporter(readMetadata());
    }

    private Properties getParameters(Metadata metadata) {
        Properties parameters = new Properties();
        for (ImportConfiguration.Parameter parameter : ImportConfiguration.read(dir).getParameter()) {
            parameters.setProperty(parameter.getName(), parameter.getValue());
        }
        return parameters;
    }

    @Override
    public Properties getParameters() {
        return getParameters(readMetadata());
    }

    @Override
    public List<ProjectFile> getDependencies() {
        return Collections.emptyList();
    }
}
