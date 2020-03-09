package dev.testment.core.simulation;

import dev.testment.core.simulation.exceptions.AmbiguousSimulationException;
import dev.testment.core.simulation.exceptions.IncompatibleSimulationTypeException;
import dev.testment.core.simulation.exceptions.SimulationNotFoundException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class SimulationScannerTests {

    @InjectMocks
    private SimulationScanner simulationScanner;

    @Test
    public void testScanSimulations() {
        TestmentSimulation simulation = this.simulationScanner.scan(this.getClass().getName(), "TEST", false);
        assertThat(simulation).isInstanceOf(TestSimulation.class);
    }

    @Test
    public void testDoNotFindSimulation() {
        TestmentSimulation simulation = this.simulationScanner.scan(this.getClass().getName(), "not-found", false);
        assertThat(simulation).isNull();
    }

    @Test(expected = SimulationNotFoundException.class)
    public void testFailToFindSimulation() {
        this.simulationScanner.scan(this.getClass().getName(), "not-found", true);
    }

    @Test(expected = AmbiguousSimulationException.class)
    public void testScanAmbiguousSimulations() {
        this.simulationScanner.scan(this.getClass().getName(), "ambiguous", false);
    }

    @Test(expected = IncompatibleSimulationTypeException.class)
    public void testScanIncompatibleSimulationTypes() {
        this.simulationScanner.scan(this.getClass().getName(), "incompatible", false);
    }

    // Simulation class definitions
    @Simulation("test")
    public static class TestSimulation extends AbstractSimulation  {
        @Override
        public Path saveHar() {
            return null;
        }

        @Override
        public Path takeScreenshot(String name) {
            return null;
        }
    }

    @Simulation("ambiguous")
    public static class AmbiguousSimulation1 {

    }

    @Simulation("ambiguous")
    public static class AmbiguousSimulation2 {

    }

    @Simulation("incompatible")
    public static class IncompatibleTypeSimulation {

    }
    // End simulation class definitions

}
