/**
 * Copyright (c) 2016, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.computation.script

import eu.itesla_project.computation.ComputationManager
import org.codehaus.groovy.control.CompilerConfiguration

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
class GroovyScript {

    static void run(File file, ComputationManager computationManager) throws IOException {
        CompilerConfiguration conf = new CompilerConfiguration();
        Binding binding = new Binding();

        // load extensions
        ServiceLoader.load(GroovyExtension.class).forEach({
            it.load(binding, computationManager)
        });

        GroovyShell shell = new GroovyShell(binding, conf);
        shell.evaluate(file);
    }
}
