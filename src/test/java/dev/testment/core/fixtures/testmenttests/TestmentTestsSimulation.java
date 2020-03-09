package dev.testment.core.fixtures.testmenttests;

import dev.testment.core.simulation.AbstractSimulation;
import dev.testment.core.simulation.Simulation;

import java.nio.file.Path;

@Simulation("test")
public class TestmentTestsSimulation extends AbstractSimulation {
    @Override
    public Path saveHar() {
        return null;
    }

    @Override
    public Path takeScreenshot(String name) {
        return null;
    }
}
