/**
 * Copyright (c) 2016, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.scripting.groovy;

import eu.itesla_project.commons.config.ComponentDefaultConfig;
import eu.itesla_project.commons.tools.AbstractToolTest;
import eu.itesla_project.commons.tools.CommandLineTools;
import eu.itesla_project.commons.tools.Tool;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Collections;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public class GroovyScriptToolTest extends AbstractToolTest {

    private ComponentDefaultConfig componentDefaultConfig;

    private GroovyScriptTool tool;

    @Override
    @Before
    public void setUp() throws Exception {
        componentDefaultConfig = Mockito.mock(ComponentDefaultConfig.class);
        tool = new GroovyScriptTool(componentDefaultConfig, Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
        super.setUp();
    }

    @Override
    protected Iterable<Tool> getTools() {
        return Collections.singleton(tool);
    }

    @Override
    public void assertCommand() {
        assertCommand(tool.getCommand(), "groovy-script", 1, 1);
        assertOption(tool.getCommand().getOptions(), "script", true, true);
    }

    @Test
    public void run() throws Exception {
        String helloFile = "/hello.groovy";
        createFile(helloFile, "print 'hello'");

        assertCommand(new String[] {"groovy-script", "--script", helloFile}, CommandLineTools.COMMAND_OK_STATUS, "hello", "");
    }

    @Test
    public void runWithParameters() throws Exception {
        String helloFile = "/hello.groovy";
        createFile(helloFile, "print 'hello ' + args[0]");

        assertCommand(new String[] {"groovy-script", "--script", helloFile, "John Doe"}, CommandLineTools.COMMAND_OK_STATUS, "hello John Doe", "");
    }
}