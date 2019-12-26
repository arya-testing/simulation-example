package com.arya.simulation;

import java.nio.file.Path;

public interface AryaSimulation {
    void setUp();
    void tearDown();
    void recordStartAction(String name);
    void recordEndAction();
    Path saveHar();
    Path takeScreenshot(String name);
    void wait(int seconds);
    Path saveRecordedActions();
}
