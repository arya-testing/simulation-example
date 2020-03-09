package dev.testment.core.simulation;

import dev.testment.core.simulation.action.ActionState;
import dev.testment.core.simulation.action.RecordedAction;
import dev.testment.core.simulation.exceptions.IllegalActionStateException;
import dev.testment.core.simulation.param.Param;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public abstract class AbstractSimulation implements TestmentSimulation {

    @Param(defaultValue = ".")
    protected String simulationPath;

    protected String logPath;
    protected String harPath;
    protected String tmpPath;
    protected String actionsPath;
    protected String screenshotsPath;

    List<RecordedAction> actions = new ArrayList<>();
    private Set<String> openedActions = new HashSet<>();

    @Override
    public void setUp() {
        simulationPath   = Paths.get(simulationPath).toAbsolutePath().toString();
        tmpPath          = Paths.get(simulationPath, "tmp").toString();
        logPath          = Paths.get(tmpPath, "log.txt").toString();
        harPath          = Paths.get(tmpPath, "recording.har").toString();
        actionsPath      = Paths.get(tmpPath, "actions.json").toString();
        screenshotsPath  = Paths.get(tmpPath, "screenshots").toString();
        createDirectories(tmpPath);
        createDirectories(screenshotsPath);
    }

    @Override
    public void tearDown() {

    }

    @Override
    public void recordStartAction(String name) {
        if(openedActions.contains(name)) {
            throw new IllegalActionStateException(String.format("Action %s cannot be restarted - it must be ended first", name));
        }
        RecordedAction action = new RecordedAction();
        action.setName(name);
        action.setDate(new Date());
        action.setState(ActionState.START);
        actions.add(action);
        openedActions.add(name);
    }

    @Override
    public void recordEndAction(String name) {
        if(!openedActions.contains(name)) {
            throw new IllegalActionStateException(String.format("Action %s cannot be ended - it must be started first", name));
        }
        RecordedAction action = new RecordedAction();
        action.setName(name);
        action.setDate(new Date());
        action.setState(ActionState.END);
        actions.add(action);
        openedActions.remove(name);
    }

    @Override
    public void wait(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Path saveRecordedActions() {
        ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        try {
            objectMapper.writeValue(new File(actionsPath), actions);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return Paths.get(actionsPath);
    }

    // TODO: move to utility class
    protected static void createDirectories(String path) {
        Path p = Paths.get(path);
        if(!Files.exists(p)) {
            try {
                Files.createDirectories(p);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // TODO: move to utility class
    private static boolean deleteDirectory(Path path) {
        File dir = path.toFile();
        File[] allContents = dir.listFiles();
        if(allContents != null) {
            for(File file : allContents) {
                deleteDirectory(file.toPath());
            }
        }
        return dir.delete();
    }

}
