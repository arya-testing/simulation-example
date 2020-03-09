package dev.testment.core;

import dev.testment.core.fixtures.testmenttests.TestmentTestsSimulation;
import org.junit.Test;

public class TestmentTests {

    @Test
    public void testRunApplication() {
        Testment.runApplication(TestmentTestsSimulation.class, new String[]{ "--simulation", "test"});
    }

}
