package com.arya;

import com.arya.simulation.AryaSimulation;
import com.arya.simulation.RuntimeOptions;
import com.arya.util.Args;

import static com.arya.AryaUtil.*;

public class Arya {

    private Arya() { }

    public static void runApplication(String[] args) {
        System.out.println(args);
        String simulationName = Args.getValue(args, "simulation").toUpperCase();
        RuntimeOptions runtimeOptions = getRuntimeOptions(args);
        AryaSimulation simulation = strictScanSimulations(simulationName);
        setSimulationParams(args, simulation);
        executeSimulation(simulationName, simulation, runtimeOptions);
    }

    private static AryaSimulation strictScanSimulations(String name) {
        AryaSimulation simulation = scanSimulations(name);
        if(simulation == null)
            throw new RuntimeException(String.format("Simulation '%s' not found", name));
        return simulation;
    }

}
