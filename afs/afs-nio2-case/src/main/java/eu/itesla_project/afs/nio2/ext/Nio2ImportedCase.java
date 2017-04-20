/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.afs.nio2.ext;

import eu.itesla_project.afs.FileIcon;
import eu.itesla_project.afs.NodePath;
import eu.itesla_project.afs.ProjectFile;
import eu.itesla_project.afs.ext.ImportedCase;
import eu.itesla_project.afs.nio2.*;
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
public class Nio2ImportedCase implements ImportedCase, Nio2ProjectNode {

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

    private final Nio2Impl impl;

    Nio2ImportedCase(Path dir, Nio2ProjectFolder parent) {
        impl = new Nio2Impl(dir);
        this.parent = Objects.requireNonNull(parent);
    }

    @Override
    public Nio2Impl getImpl() {
        return impl;
    }

    @Override
    public Nio2ProjectFolder getParent() {
        impl.checkNotDeleted();
        return parent;
    }

    @Override
    public Nio2Project getProject() {
        impl.checkNotDeleted();
        return parent.getProject();
    }

    @Override
    public boolean isFolder() {
        impl.checkNotDeleted();
        return false;
    }

    public NodePath getPath() {
        impl.checkNotDeleted();
        return NodePath.getPath(this, Nio2ProjectNodePathToString.INSTANCE);
    }

    @Override
    public FileIcon getIcon() {
        return CaseIconCache.INSTANCE.get(getProject().getFileSystem().getImportersLoader(),
                                          getProject().getFileSystem().getComputationManager(),
                                          ImportConfiguration.read(impl.getDir()).getFormat());
    }

    @Override
    public ReadOnlyDataSource getDataSource() {
        return new GenericReadOnlyDataSource(impl.getDir(), getName());
    }

    @Override
    public Importer getImporter() {
        return Importers.getImporter(getProject().getFileSystem().getImportersLoader(), ImportConfiguration.read(impl.getDir()).getFormat(),
                                     getProject().getFileSystem().getComputationManager(), new ImportConfig());
    }

    @Override
    public Properties getParameters() {
        Properties parameters = new Properties();
        for (ImportConfiguration.Parameter parameter : ImportConfiguration.read(impl.getDir()).getParameter()) {
            parameters.setProperty(parameter.getName(), parameter.getValue());
        }
        return parameters;
    }

    @Override
    public void delete() {
        impl.delete(getProject());
    }

    @Override
    public List<ProjectFile> getDependencies() {
        return impl.getDependencies(getProject());
    }

    @Override
    public List<ProjectFile> getBackwardDependencies() {
        return impl.getBackwardDependencies(getProject());
    }

    @Override
    public void onCacheInvalidation() {
    }

    @Override
    public String getName() {
        return impl.getName();
    }
}
