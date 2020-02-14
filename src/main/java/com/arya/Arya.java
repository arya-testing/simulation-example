package com.arya;

import com.arya.logger.LogLevel;
import com.arya.logger.Logger;
import com.arya.logger.LoggerFactory;
import com.arya.simulation.AryaSimulation;
import com.arya.simulation.RuntimeOptions;
import com.arya.util.Args;

import static com.arya.AryaUtil.*;

public class Arya {

    private static final Logger logger = LoggerFactory.getLogger(Arya.class);

    private Arya() { }

    public static void runApplication(String[] args) {
        configureLoggers(args);
        logger.info(formatArgs(args));
        String simulationName = Args.getValue(args, "simulation").toUpperCase();
        RuntimeOptions runtimeOptions = getRuntimeOptions(args);
        AryaSimulation simulation = strictScanSimulations(simulationName);
        setSimulationParams(args, simulation);
        logger.info("Starting simulation \"%s\"", simulationName);
        executeSimulation(simulationName, simulation, runtimeOptions);
        logger.info("Finished simulation \"%s\"", simulationName);
    }

    private static void configureLoggers(String[] args) {
        LogLevel logLevel = LogLevel.valueOf(Args.getValue(args, "log-level", LogLevel.INFO.name()).toUpperCase());
        LoggerFactory.setGlobalLevel(logLevel);
    }

    private static String formatArgs(String[] args) {
        StringBuilder builder = new StringBuilder("Command line arguments: \"");
        for(String arg : args) {
            builder.append(arg + " ");
        }
        if(args.length > 0) {
            builder.deleteCharAt(builder.length() - 1);
        }
        builder.append("\"");
        return builder.toString();
    }

    private static AryaSimulation strictScanSimulations(String name) {
        AryaSimulation simulation = scanSimulations(name);
        if(simulation == null)
            throw new RuntimeException(String.format("Simulation '%s' not found", name));
        return simulation;
    }

}