/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.afs.nio2;

import eu.itesla_project.afs.FileIcon;
import eu.itesla_project.afs.Project;

import javax.xml.bind.annotation.*;
import java.nio.file.Path;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public class Nio2Project extends Nio2Node implements Project {

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    @XmlRootElement(name = "projectMetadata")
    public static class Metadata {

        public static final String XML_FILE_NAME = "projectMetadata.xml";

        @XmlAttribute(name = "description", required = true)
        private String description;

        public Metadata() {
            this("");
        }

        public Metadata(String description) {
            this.description = Objects.requireNonNull(description);
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = Objects.requireNonNull(description);
        }
    }

    private static final String PROJECT_LABEL = ResourceBundle.getBundle("lang.Nio2Project").getString("project");

    private static final FileIcon PROJECT_ICON = new FileIcon(PROJECT_LABEL, Nio2Project.class.getResourceAsStream("/icons/project16x16.png"));

    private final String name;

    private final Metadata metadata;

    private final Nio2ProjectFolder rootFolder;

    private final CentralDirectory centralDirectory;

    Nio2Project(Path dir, Nio2Folder parent, Nio2AppFileSystem fileSystem, String name, Metadata metadata) {
        super(dir, parent, fileSystem);
        this.name = Objects.requireNonNull(name);
        this.metadata = Objects.requireNonNull(metadata);
        centralDirectory = new CentralDirectory(dir.resolve("centralDirectory"));
        rootFolder = new Nio2ProjectFolder(dir, null) {
            @Override
            public String getName() {
                impl.checkNotDeleted();
                return PROJECT_LABEL;
            }

            @Override
            public Project getProject() {
                return Nio2Project.this;
            }
        };
    }

    @Override
    public boolean isFolder() {
        return false;
    }

    @Override
    public Nio2ProjectFolder getRootFolder() {
        return rootFolder;
    }

    @Override
    public FileIcon getIcon() {
        return PROJECT_ICON;
    }

    @Override
    public String getDescription() {
        return metadata.getDescription();
    }

    @Override
    public String getName() {
        return name;
    }

    public CentralDirectory getCentralDirectory() {
        return centralDirectory;
    }
}
