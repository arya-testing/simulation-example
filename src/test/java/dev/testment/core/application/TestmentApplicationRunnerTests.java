package dev.testment.core.application;

import dev.testment.core.logger.LoggerConfigurator;
import dev.testment.core.simulation.AbstractSimulation;
import dev.testment.core.simulation.SimulationExecutor;
import dev.testment.core.simulation.SimulationRunOptions;
import dev.testment.core.simulation.SimulationScanner;
import dev.testment.core.simulation.param.ParamInjector;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TestmentApplicationRunnerTests {

    @Mock
    private LoggerConfigurator loggerConfiguratorMock;

    @Mock
    private ParamInjector paramInjectorMock;

    @Mock
    private SimulationExecutor simulationExecutorMock;

    @Mock
    private SimulationScanner simulationScannerMock;

    @InjectMocks
    private TestmentApplicationRunner applicationRunner;

    @Test
    public void testRun() {
        TestSimulation simulation = new TestSimulation();
        when(this.simulationScannerMock.scan(anyString(), anyString(), anyBoolean())).thenReturn(simulation);

        // Method under test.
        String[] args = new String[]{"--simulation", "test", "--auto-screenshots", "--auto-wait", "3"};
        this.applicationRunner.run(TestSimulation.class, args);

        ArgumentCaptor<SimulationRunOptions> optionsCaptor = ArgumentCaptor.forClass(SimulationRunOptions.class);
        verify(this.loggerConfiguratorMock).configure(args);
        verify(this.simulationScannerMock).scan(TestSimulation.class.getPackage().getName(), "TEST", true);
        verify(this.paramInjectorMock).inject(args, simulation);
        verify(this.simulationExecutorMock).execute(eq("TEST"), eq(simulation), optionsCaptor.capture());
        assertThat(optionsCaptor.getValue().isAutoScreenshots()).isTrue();
        assertThat(optionsCaptor.getValue().getAutoWaitTime()).isEqualTo(3);
    }

    @Test
    public void testRunWithoutAutoScreenshotsAndAutoWait() {
        TestSimulation simulation = new TestSimulation();
        when(this.simulationScannerMock.scan(anyString(), anyString(), anyBoolean())).thenReturn(simulation);

        // Method under test.
        String[] args = new String[]{"--simulation", "test"};
        this.applicationRunner.run(TestSimulation.class, args);

        ArgumentCaptor<SimulationRunOptions> optionsCaptor = ArgumentCaptor.forClass(SimulationRunOptions.class);
        verify(this.loggerConfiguratorMock).configure(args);
        verify(this.simulationScannerMock).scan(TestSimulation.class.getPackage().getName(), "TEST", true);
        verify(this.paramInjectorMock).inject(args, simulation);
        verify(this.simulationExecutorMock).execute(eq("TEST"), eq(simulation), optionsCaptor.capture());
        assertThat(optionsCaptor.getValue().isAutoScreenshots()).isFalse();
        assertThat(optionsCaptor.getValue().getAutoWaitTime()).isEqualTo(0);
    }

    class TestSimulation extends AbstractSimulation {
        @Override
        public Path saveHar() {
            return null;
        }

        @Override
        public Path takeScreenshot(String name) {
            return null;
        }
    }

}
