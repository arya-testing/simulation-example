package com.arya.simulation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class AbstractSimulation implements AryaSimulation {

    @Param(defaultValue = ".")
    protected String simulationPath;

    protected String harPath;
    protected String tmpPath;
    protected String actionsPath;
    protected String screenshotsPath;

    protected List<RecordedSimulationAction> actions = new ArrayList<>();

    @Override
    public void setUp() {
        simulationPath   = Paths.get(simulationPath).toAbsolutePath().toString();
        tmpPath          = Paths.get(simulationPath, "tmp").toString();
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
        RecordedSimulationAction action = new RecordedSimulationAction();
        action.setName(name);
        action.setStart(new Date());
        actions.add(action);
    }

    @Override
    public void recordEndAction() {
        if(actions.size() < 1)
            throw new RuntimeException("No actions to end");
        RecordedSimulationAction last = actions.get(actions.size() - 1);
        if(last.getEnd() != null)
            throw new RuntimeException("Last action has already ended");
        last.setEnd(new Date());
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

}
