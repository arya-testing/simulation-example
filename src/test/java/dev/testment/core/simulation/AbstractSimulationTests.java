package dev.testment.core.simulation;

import dev.testment.core.simulation.action.ActionState;
import dev.testment.core.simulation.exceptions.IllegalActionStateException;
import dev.testment.core.simulation.action.RecordedAction;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class AbstractSimulationTests {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @InjectMocks
    private BaseSimulation baseSimulation;
    private String simulationPath;

    @Before
    public void setUp() {
        this.simulationPath = this.temporaryFolder.getRoot().toPath().toString();
        this.baseSimulation.simulationPath = this.simulationPath;
    }

    @Test
    public void testSetUp() {
        this.baseSimulation.setUp();
        assertThat(this.baseSimulation.simulationPath).isEqualTo(Paths.get(this.simulationPath).toAbsolutePath().toString());
        assertThat(this.baseSimulation.tmpPath).isEqualTo(Paths.get(this.simulationPath, "tmp").toAbsolutePath().toString());
        assertThat(this.baseSimulation.logPath).isEqualTo(Paths.get(this.simulationPath, "tmp", "log.txt").toAbsolutePath().toString());
        assertThat(this.baseSimulation.harPath).isEqualTo(Paths.get(this.simulationPath, "tmp", "recording.har").toAbsolutePath().toString());
        assertThat(this.baseSimulation.actionsPath).isEqualTo(Paths.get(this.simulationPath, "tmp", "actions.json").toAbsolutePath().toString());
        assertThat(this.baseSimulation.screenshotsPath).isEqualTo(Paths.get(this.simulationPath, "tmp", "screenshots").toAbsolutePath().toString());
        assertThat(Paths.get(this.simulationPath, "tmp")).isDirectory();
        assertThat(Paths.get(this.simulationPath, "tmp", "screenshots")).isEmptyDirectory();
    }

    @Test
    public void testTearDown() {
        this.baseSimulation.tearDown();
    }

    @Test
    public void testRecordStartActions() throws InterruptedException {
        this.baseSimulation.recordStartAction("action1");
        Thread.sleep(1);

        this.baseSimulation.recordStartAction("action2");
        Thread.sleep(1);

        this.baseSimulation.recordStartAction("action3");

        assertThat(this.baseSimulation.actions).hasSize(3);
        assertThat(this.baseSimulation.actions.get(0).getName()).isEqualTo("action1");
        assertThat(this.baseSimulation.actions.get(0).getState()).isEqualTo(ActionState.START);
        assertThat(this.baseSimulation.actions.get(1).getName()).isEqualTo("action2");
        assertThat(this.baseSimulation.actions.get(1).getState()).isEqualTo(ActionState.START);
        assertThat(this.baseSimulation.actions.get(2).getName()).isEqualTo("action3");
        assertThat(this.baseSimulation.actions.get(2).getState()).isEqualTo(ActionState.START);

        assertThat(this.baseSimulation.actions.get(0).getDate()).isBefore(this.baseSimulation.actions.get(1).getDate());
        assertThat(this.baseSimulation.actions.get(1).getDate()).isBefore(this.baseSimulation.actions.get(2).getDate());
    }

    @Test(expected = IllegalActionStateException.class)
    public void testFailToRecordStartActionWhenNotEnded() {
        this.baseSimulation.recordStartAction("action1");
        this.baseSimulation.recordStartAction("action1");
    }

    @Test(expected = IllegalActionStateException.class)
    public void testFailToRecordEndActionWhenNotStarted() {
        this.baseSimulation.recordEndAction("action1");
    }

    @Test
    public void testRecordStartAndEndActions() throws InterruptedException {
        this.baseSimulation.recordStartAction("action1");
        Thread.sleep(1);

        this.baseSimulation.recordStartAction("action2");
        Thread.sleep(1);

        this.baseSimulation.recordEndAction("action1");
        Thread.sleep(1);

        this.baseSimulation.recordStartAction("action3");

        assertThat(this.baseSimulation.actions).hasSize(4);
        assertThat(this.baseSimulation.actions.get(0).getName()).isEqualTo("action1");
        assertThat(this.baseSimulation.actions.get(0).getState()).isEqualTo(ActionState.START);
        assertThat(this.baseSimulation.actions.get(1).getName()).isEqualTo("action2");
        assertThat(this.baseSimulation.actions.get(1).getState()).isEqualTo(ActionState.START);
        assertThat(this.baseSimulation.actions.get(2).getName()).isEqualTo("action1");
        assertThat(this.baseSimulation.actions.get(2).getState()).isEqualTo(ActionState.END);
        assertThat(this.baseSimulation.actions.get(3).getName()).isEqualTo("action3");
        assertThat(this.baseSimulation.actions.get(3).getState()).isEqualTo(ActionState.START);

        assertThat(this.baseSimulation.actions.get(0).getDate()).isBefore(this.baseSimulation.actions.get(1).getDate());
        assertThat(this.baseSimulation.actions.get(1).getDate()).isBefore(this.baseSimulation.actions.get(2).getDate());
        assertThat(this.baseSimulation.actions.get(2).getDate()).isBefore(this.baseSimulation.actions.get(3).getDate());
    }

    @Test
    public void testWait() {
        Date before = new Date();
        this.baseSimulation.wait(1);
        Date after = new Date();
        assertThat(after.getTime() - before.getTime()).isBetween(1000L, 1100L); // between 1.0 and 1.1 seconds
    }

    @Test
    public void testSaveRecordedActions() throws IOException {
        Path expectedPath = Paths.get(this.simulationPath, "actions.json");
        this.baseSimulation.actionsPath = expectedPath.toString();

        RecordedAction startAction1 = new RecordedAction();
        startAction1.setName("action1");
        startAction1.setState(ActionState.START);
        startAction1.setDate(new Date(0));

        RecordedAction startAction2 = new RecordedAction();
        startAction2.setName("action2");
        startAction2.setState(ActionState.START);
        startAction2.setDate(new Date(1));

        RecordedAction endAction1 = new RecordedAction();
        endAction1.setName("action1");
        endAction1.setState(ActionState.END);
        endAction1.setDate(new Date(2));

        this.baseSimulation.actions.add(startAction1);
        this.baseSimulation.actions.add(startAction2);
        this.baseSimulation.actions.add(endAction1);

        Path actualPath = this.baseSimulation.saveRecordedActions();
        assertThat(actualPath).isEqualTo(actualPath);
        assertThat(actualPath).isRegularFile();

        String json = new String(Files.readAllBytes(actualPath));
        assertThat(json).isEqualTo("[ {\n" +
                "  \"name\" : \"action1\",\n" +
                "  \"date\" : 0,\n" +
                "  \"state\" : \"start\"\n" +
                "}, {\n" +
                "  \"name\" : \"action2\",\n" +
                "  \"date\" : 1,\n" +
                "  \"state\" : \"start\"\n" +
                "}, {\n" +
                "  \"name\" : \"action1\",\n" +
                "  \"date\" : 2,\n" +
                "  \"state\" : \"end\"\n" +
                "} ]");
    }

    static class BaseSimulation extends AbstractSimulation {
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
