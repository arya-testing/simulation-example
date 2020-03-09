package dev.testment.core.simulation;

import dev.testment.core.logger.Logger;
import dev.testment.core.logger.LoggerFactory;
import dev.testment.core.simulation.action.Action;
import dev.testment.core.util.Validation;
import dev.testment.core.util.ReflectionUtil;

import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.List;

public class SimulationExecutor {

    private static final Logger logger = LoggerFactory.getLogger(SimulationExecutor.class);

    public void execute(String name, TestmentSimulation simulation, SimulationRunOptions options) {
        if(!simulation.getType().isAssignableFrom(simulation.getClass())) {
            throw new RuntimeException(String.format("Simulation type '%s' must be assignable from class '%s'", simulation.getType(), simulation.getClass()));
        }

        List<Method> methods = ReflectionUtil.getDeclaredMethodsSortedByLineNumber(simulation.getType());
        int actionCount = getActionCount(methods);

        logger.debug("START SIMULATION: " + name);

        try {
            simulation.setUp();
            for(int i = 0, pos = 0; i < methods.size(); i++) {
                Method method = methods.get(i);
                Action action = method.getAnnotation(Action.class);

                if(action == null)
                    continue;

                pos++;

                Simulation sim = simulation.getClass().getAnnotation(Simulation.class);
                String actionName = getActionName(sim.actionPrefix(), action.value(), method.getName(), pos, actionCount);

                logger.debug("START ACTION: " + actionName);
                simulation.recordStartAction(actionName);

                // Execute action.
                ReflectionUtil.invokeMethod(method, simulation);

                if(options.getAutoWaitTime() > 0) {
                    logger.debug("AUTO WAIT: " + options.getAutoWaitTime() + " seconds");
                    simulation.wait(options.getAutoWaitTime());
                }

                takeScreenshot(simulation, options, actionName, null);
                simulation.recordEndAction(actionName);
                logger.debug("END ACTION: " + actionName);
            }

            simulation.saveRecordedActions();
            simulation.saveHar();

        } finally {
            simulation.tearDown();
        }

        logger.debug("COMPLETE");
    }

    private int getActionCount(List<Method> methods) {
        int count = 0;
        for(Method method : methods) {
            Action action = method.getAnnotation(Action.class);
            if(action != null) {
                count++;
            }
        }
        return count;
    }

    private String getActionName(String prefix, String actionName, String methodName, int position, int total) {
        boolean useActionName = Validation.isNotEmpty(actionName);
        String base = useActionName ? actionName : methodName;
        boolean uppercaseFirstChar = !useActionName;
        int targetPositionWidth = total < 10 ? 2 : (int) Math.floor(Math.log10(total)) + 1;

        StringBuilder builder = new StringBuilder();

        // Add prefix.
        builder.append(Validation.isEmpty(prefix) ? "" : prefix + "_");

        // Left-pad with zeroes (if needed).
        int positionWidth = (int) Math.floor(Math.log10(position)) + 1;
        int pad = targetPositionWidth - positionWidth;
        for(int i = 0; i < pad; i++)
            builder.append("0");

        // Add position.
        builder.append(position);
        builder.append("_");

        if(uppercaseFirstChar) {
            // Add base of action name, making the first character uppercase.
            if(base.length() >= 1)
                builder.append(base.substring(0, 1).toUpperCase());
            if(base.length() >= 2)
                builder.append(base.substring(1));
        } else {
            builder.append(base);
        }

        return builder.toString();
    }

    private Path takeScreenshot(TestmentSimulation simulation, SimulationRunOptions options, String actionName, String label) {
        if(options.isAutoScreenshots()) {
            String screenshotName = actionName + (label == null ? "" : "_" + label) + ".png";
            logger.debug("AUTO " + (label == null ? "" : label.toUpperCase() + " ") + "SCREENSHOT: " + screenshotName);
            return simulation.takeScreenshot(screenshotName);
        }
        return null;
    }

}
