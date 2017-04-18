/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.afs.nio2;

import eu.itesla_project.commons.jaxb.JaxbUtil;

import javax.xml.bind.annotation.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
@XmlRootElement(name = "metadata")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
public class Metadata {

    public static final String XML_FILE_NAME = "metadata.xml";

    public static Metadata create(String nodeType) {
        return new Metadata(UUID.randomUUID().toString(), nodeType);
    }

    public static Metadata read(Path dir) {
        return JaxbUtil.unmarchallFile(Metadata.class, dir.resolve(XML_FILE_NAME));
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class Dependency {

        @XmlAttribute(name = "id", required = true)
        private String id;

        @XmlAttribute(name = "type", required = true)
        private String type;

        public Dependency() {
        }

        public Dependency(String id, String type) {
            this.id = Objects.requireNonNull(id);
            this.type = Objects.requireNonNull(type);
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = Objects.requireNonNull(id);
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = Objects.requireNonNull(type);
        }
    }


    @XmlAttribute(name = "id", required = true)
    private String id;

    @XmlAttribute(name = "nodeClass", required = true)
    private String nodeClass;

    @XmlElement(required = true)
    private final List<String> backwardDependencies = new ArrayList<>();

    @XmlElement(required = true)
    private final List<Dependency> dependencies = new ArrayList<>();

    public Metadata() {
        this("", "");
    }

    public Metadata(String id, String nodeClass) {
        this.id = Objects.requireNonNull(id);
        this.nodeClass = Objects.requireNonNull(nodeClass);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = Objects.requireNonNull(id);
    }

    public String getNodeClass() {
        return nodeClass;
    }

    public void setNodeClass(String nodeClass) {
        this.nodeClass = Objects.requireNonNull(nodeClass);
    }

    public List<String> getBackwardDependencies() {
        return backwardDependencies;
    }

    public List<Dependency> getDependencies() {
        return dependencies;
    }

    public void addDependency(String id, String type) {
        dependencies.add(new Dependency(id, type));
    }

    public Dependency findDependencyByType(String type) {
        for (Dependency dependency : dependencies) {
            if (dependency.getType().equals(type)) {
                return dependency;
            }
        }
        return null;
    }

    public void save(Path dir) {
        JaxbUtil.marshallElement(Metadata.class, this, dir.resolve(XML_FILE_NAME));
    }
}

