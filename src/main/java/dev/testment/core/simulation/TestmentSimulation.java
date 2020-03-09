package dev.testment.core.simulation;

import java.nio.file.Path;

public interface TestmentSimulation {
    void setUp();
    void tearDown();
    void recordStartAction(String name);
    void recordEndAction(String name);
    Path saveHar();
    Path takeScreenshot(String name);
    void wait(int seconds);
    Path saveRecordedActions();
    // Method needed to resolve a mockito + javassist conflict during unit tests.
    // By default, the actual class of the instance is returned. Otherwise, this
    // method is mocked to return the intended class of the simulation, not the
    // class of the anonymous proxy that represents the spied/mocked simulation.
    default Class<?> getType() {
        return this.getClass();
    }
}
