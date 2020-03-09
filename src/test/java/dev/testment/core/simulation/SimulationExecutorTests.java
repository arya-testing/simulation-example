package dev.testment.core.simulation;

import dev.testment.core.simulation.action.Action;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SimulationExecutorTests {

    @InjectMocks
    private SimulationExecutor simulationExecutor;

    @Test
    public void testExecuteWithActionPrefixAndCustomActionName() {
        TestSimulation testSimulation = spy(new TestSimulation());
        this.simulationExecutor.execute("test", testSimulation, new SimulationRunOptions());

        InOrder inOrder = inOrder(testSimulation);
        inOrder.verify(testSimulation).setUp();
        inOrder.verify(testSimulation).recordStartAction("TS_01_Action1");
        inOrder.verify(testSimulation).recordEndAction("TS_01_Action1");
        inOrder.verify(testSimulation).recordStartAction("TS_02_Action2");
        inOrder.verify(testSimulation).recordEndAction("TS_02_Action2");
        inOrder.verify(testSimulation).recordStartAction("TS_03_Action3");
        inOrder.verify(testSimulation).recordEndAction("TS_03_Action3");
        inOrder.verify(testSimulation).recordStartAction("TS_04_Action4");
        inOrder.verify(testSimulation).recordEndAction("TS_04_Action4");
        inOrder.verify(testSimulation).recordStartAction("TS_05_Action5");
        inOrder.verify(testSimulation).recordEndAction("TS_05_Action5");
        inOrder.verify(testSimulation).recordStartAction("TS_06_customAction6");
        inOrder.verify(testSimulation).recordEndAction("TS_06_customAction6");
        inOrder.verify(testSimulation).recordStartAction("TS_07_Action7");
        inOrder.verify(testSimulation).recordEndAction("TS_07_Action7");
        inOrder.verify(testSimulation).recordStartAction("TS_08_Action8");
        inOrder.verify(testSimulation).recordEndAction("TS_08_Action8");
        inOrder.verify(testSimulation).recordStartAction("TS_09_Action9");
        inOrder.verify(testSimulation).recordEndAction("TS_09_Action9");
        inOrder.verify(testSimulation).recordStartAction("TS_10_Action10");
        inOrder.verify(testSimulation).recordEndAction("TS_10_Action10");
        inOrder.verify(testSimulation).saveRecordedActions();
        inOrder.verify(testSimulation).saveHar();
        inOrder.verify(testSimulation).tearDown();
    }

    @Test
    public void testExecuteWithAutoWaitAndAutoScreenshots() {
        TestSimulation2 testSimulation2 = spy(new TestSimulation2());
        SimulationRunOptions options = new SimulationRunOptions();
        options.setAutoWaitTime(10);
        options.setAutoScreenshots(true);

        this.simulationExecutor.execute("test", testSimulation2, options);

        InOrder inOrder = inOrder(testSimulation2);
        inOrder.verify(testSimulation2).setUp();
        inOrder.verify(testSimulation2).recordStartAction("01_Action1");
        inOrder.verify(testSimulation2).wait(10);
        inOrder.verify(testSimulation2).takeScreenshot("01_Action1.png");
        inOrder.verify(testSimulation2).recordEndAction("01_Action1");
        inOrder.verify(testSimulation2).recordStartAction("02_Action2");
        inOrder.verify(testSimulation2).wait(10);
        inOrder.verify(testSimulation2).takeScreenshot("02_Action2.png");
        inOrder.verify(testSimulation2).recordEndAction("02_Action2");
        inOrder.verify(testSimulation2).recordStartAction("03_Action3");
        inOrder.verify(testSimulation2).wait(10);
        inOrder.verify(testSimulation2).takeScreenshot("03_Action3.png");
        inOrder.verify(testSimulation2).recordEndAction("03_Action3");
        inOrder.verify(testSimulation2).saveRecordedActions();
        inOrder.verify(testSimulation2).saveHar();
        inOrder.verify(testSimulation2).tearDown();
    }

    @Test
    public void testFailToExecuteWithInvalidType() {
        TestmentSimulation simulationMock = mock(TestmentSimulation.class);
        when(simulationMock.getType()).then((invocation) -> String.class);
        RuntimeException ex = assertThrows(RuntimeException.class, () -> this.simulationExecutor.execute("invalid", simulationMock, new SimulationRunOptions()));
        assertThat(ex.getMessage()).matches("Simulation type '.*String' must be assignable from class '.*'");
    }

    @Simulation(value = "test", actionPrefix = "TS")
    public static class TestSimulation implements TestmentSimulation {
        @Action
        public void action1() { }
        @Action public void action2() { }
        @Action public void action3() { }
        @Action public void action4() { }
        @Action public void action5() { }
        @Action("customAction6") public void action6() { }
        @Action public void action7() { }
        @Action public void action8() { }
        @Action public void action9() { }
        @Action public void action10() { }

        @Override public void setUp() { }
        @Override public void tearDown() { }
        @Override public void recordStartAction(String name) { }
        @Override public void recordEndAction(String name) { }
        @Override public Path saveHar() { return null; }
        @Override public Path takeScreenshot(String name) { return null; }
        @Override public void wait(int seconds) { }
        @Override public Path saveRecordedActions() { return null; }
        @Override public Class<?> getType() { return TestSimulation.class; }
    }

    @Simulation(value = "test2")
    public static class TestSimulation2 implements TestmentSimulation {
        @Action public void action1() { }
        @Action public void action2() { }
        @Action public void action3() { }

        @Override public void setUp() { }
        @Override public void tearDown() { }
        @Override public void recordStartAction(String name) { }
        @Override public void recordEndAction(String name) { }
        @Override public Path saveHar() { return null; }
        @Override public Path takeScreenshot(String name) { return null; }
        @Override public void wait(int seconds) { }
        @Override public Path saveRecordedActions() { return null; }
        @Override public Class<?> getType() { return TestSimulation2.class; }
    }

}
