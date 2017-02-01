/**
 * Copyright (c) 2016, All partners of the iTesla project (http://www.itesla-project.eu/consortium)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.contingency;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import eu.itesla_project.contingency.tasks.CompoundModificationTask;
import eu.itesla_project.contingency.tasks.ModificationTask;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public class ContingencyImpl implements Contingency {

    private final String id;

    private final List<ContingencyElement> elements;

    public ContingencyImpl(String id, ContingencyElement elements) {
        this(id, Collections.singletonList(elements));
    }

    public ContingencyImpl(String id, List<ContingencyElement> elements) {
        this.id = Objects.requireNonNull(id);
        this.elements = Objects.requireNonNull(elements);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Collection<ContingencyElement> getElements() {
        return Collections.unmodifiableCollection(elements);
    }

    @Override
    public ModificationTask toTask() {
        List<ModificationTask> subTasks = elements.stream().map(ContingencyElement::toTask).collect(Collectors.toList());

        return new CompoundModificationTask(subTasks);
    }
}
