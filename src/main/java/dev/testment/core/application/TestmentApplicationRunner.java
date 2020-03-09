package dev.testment.core.application;

import dev.testment.core.cli.Args;
import dev.testment.core.logger.Logger;
import dev.testment.core.logger.LoggerConfigurator;
import dev.testment.core.logger.LoggerFactory;
import dev.testment.core.util.Validation;
import dev.testment.core.simulation.TestmentSimulation;
import dev.testment.core.simulation.SimulationRunOptions;
import dev.testment.core.simulation.SimulationExecutor;
import dev.testment.core.simulation.param.ParamInjector;
import dev.testment.core.simulation.SimulationScanner;

public class TestmentApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(TestmentApplicationRunner.class);

    private final LoggerConfigurator loggerConfigurator;
    private final ParamInjector paramInjector;
    private final SimulationExecutor simulationExecutor;
    private final SimulationScanner simulationScanner;

    public TestmentApplicationRunner() {
        this.loggerConfigurator = new LoggerConfigurator();
        this.paramInjector = new ParamInjector();
        this.simulationExecutor = new SimulationExecutor();
        this.simulationScanner = new SimulationScanner();
    }

    public TestmentApplicationRunner(LoggerConfigurator loggerConfigurator, ParamInjector paramInjector,
                                     SimulationExecutor simulationExecutor, SimulationScanner simulationScanner) {
        Validation.notNull("loggerConfigurator", loggerConfigurator);
        Validation.notNull("paramInjector", paramInjector);
        Validation.notNull("simulationExecutor", simulationExecutor);
        Validation.notNull("simulationScanner", simulationScanner);
        this.loggerConfigurator = loggerConfigurator;
        this.paramInjector = paramInjector;
        this.simulationExecutor = simulationExecutor;
        this.simulationScanner = simulationScanner;
    }

    public void run(Class<?> clazz, String[] args) {
        this.loggerConfigurator.configure(args);
        logger.debug("Command line arguments: %s", formatArgs(args));

        String simulationName = Args.getValue(args, "simulation").toUpperCase();
        SimulationRunOptions options = this.getSimulationRuntimeOptions(args);

        TestmentSimulation simulation = this.simulationScanner.scan(clazz.getPackage().getName(), simulationName, true);
        this.paramInjector.inject(args, simulation);

        this.logger.info("Starting simulation \"%s\"", simulationName);
        this.simulationExecutor.execute(simulationName, simulation, options);
        this.logger.info("Finished simulation \"%s\"", simulationName);
    }

    private SimulationRunOptions getSimulationRuntimeOptions(String[] args) {
        SimulationRunOptions options = new SimulationRunOptions();
        options.setAutoScreenshots(Args.exists(args, "auto-screenshots"));
        options.setAutoWaitTime(Args.getIntValue(args, "auto-wait", 0));
        return options;
    }

    private static String formatArgs(String[] args) {
        StringBuilder builder = new StringBuilder();
        for(String arg : args) {
            builder.append(arg + " ");
        }
        if(args.length > 0) {
            builder.deleteCharAt(builder.length() - 1);
        }
        return builder.toString();
    }

}
