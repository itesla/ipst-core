package eu.itesla_project.loadflow.api

import com.google.auto.service.AutoService
import eu.itesla_project.commons.config.ComponentDefaultConfig
import eu.itesla_project.computation.ComputationManager
import eu.itesla_project.computation.script.GroovyExtension
import eu.itesla_project.iidm.network.Network
import eu.itesla_project.loadflow.api.mock.LoadFlowFactoryMock

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
@AutoService(GroovyExtension.class)
class LoadFlowGroovyExtension implements GroovyExtension {

    @Override
    void load(Binding binding, ComputationManager computationManager) {
        LoadFlowFactory loadFlowFactory = ComponentDefaultConfig.load().newFactoryImpl(LoadFlowFactory.class,
                LoadFlowFactoryMock.class)

        binding.runLoadFlow = { Network network, LoadFlowParameters parameters  = LoadFlowParameters.load() ->
            LoadFlow loadFlow = loadFlowFactory.create(network, computationManager, 0);
            loadFlow.run()
        }
    }
}
