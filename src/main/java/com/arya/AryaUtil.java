package com.arya;

import com.arya.simulation.*;
import com.arya.util.Args;
import com.arya.util.Reflection;
import com.arya.util.Validation;
import org.reflections.Reflections;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

public class AryaUtil {

    private AryaUtil() { }

    public static RuntimeOptions getRuntimeOptions(String[] args) {
        RuntimeOptions options = new RuntimeOptions();
        options.setAutoScreenshots(Args.exists(args, "auto-screenshots"));
        options.setAutoWaitTime(Args.getIntValue(args, "auto-wait", 0));
        return options;
    }

    public static AryaSimulation scanSimulations(String name) {
        Reflections reflections = new Reflections("com.arya");
        Set<Class<?>> types = reflections.getTypesAnnotatedWith(Simulation.class);
        for(Class<?> type : types) {
            Simulation sim = type.getAnnotation(Simulation.class);
            if(name.equalsIgnoreCase(sim.value())) {
                if(AryaSimulation.class.isAssignableFrom(type)) {
                    return (AryaSimulation) Reflection.createInstance(type);
                } else {
                    throw new RuntimeException("Simulation " + name + " is not of type " + AryaSimulation.class.getCanonicalName());
                }
            }
        }
        return null;
    }

    public static void setSimulationParams(String[] args, AryaSimulation simulation) {
        Class<?> type = simulation.getClass();
        while(type != null) {
            for(Field field : type.getDeclaredFields()) {
                Param param = field.getAnnotation(Param.class);
                if(param == null)
                    continue;

                String paramName = param.value() == null || param.value().isEmpty() ? field.getName() : param.value();
                String paramValue;

                if(param.required() && (param.defaultValue() == null || param.defaultValue().isEmpty())) {
                    paramValue = Args.getValue(args, paramName);
                } else {
                    paramValue = Args.getValue(args, paramName, param.defaultValue());
                    if(paramValue.isEmpty())
                        paramValue = null;
                }
                // TODO determine and use type of param

                Reflection.setField(field, simulation, paramValue);
            }
            type = type.getSuperclass();
        }
    }

    public static void executeSimulation(String name, AryaSimulation simulation, RuntimeOptions options) {
        List<Method> methods = Reflection.getSortedDeclaredMethods(simulation.getClass());
        System.out.println("START SIMULATION: " + name);

        try {
            simulation.setUp();
            for(int i = 0, pos = 0; i < methods.size(); i++) {
                Method method = methods.get(i);
                Action action = method.getAnnotation(Action.class);

                if(action == null)
                    continue;

                pos++;

                Simulation sim = simulation.getClass().getAnnotation(Simulation.class);
                String actionName = getActionName(sim.actionPrefix(), action.value(), method.getName(), pos, methods.size());

                System.out.println("START ACTION: " + actionName);
                simulation.recordStartAction(actionName);

                // Execute action.
                Reflection.invokeMethod(method, simulation);

                if(options.getAutoWaitTime() > 0) {
                    System.out.println("AUTO WAIT: " + options.getAutoWaitTime() + " seconds");
                    simulation.wait(options.getAutoWaitTime());
                }

                takeScreenshot(simulation, options, actionName, null);
                simulation.recordEndAction();
                System.out.println("END ACTION: " + actionName);

            }
        } finally {
            simulation.tearDown();
        }

        simulation.saveRecordedActions();
        simulation.saveHar();
        System.out.println("COMPLETE");
    }

    public static String getActionName(String prefix, String name) {
        StringBuilder builder = new StringBuilder();
        builder.append(Validation.isEmpty(prefix) ? "" : prefix + "_");
        builder.append(name);
        return builder.toString();
    }

    public static String getActionName(String prefix, String base, int position, int targetPositionWidth) {
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

        // Add base of action name, making the first character uppercase.
        if(base.length() >= 1)
            builder.append(base.substring(0, 1).toUpperCase());

        if(base.length() >= 2)
            builder.append(base.substring(1));

        return builder.toString();
    }

    protected static String getActionName(String prefix, String actionName, String methodName, int position, int total) {
        if(Validation.isEmpty(actionName)) {
            int targetPositionWidth = total < 10 ? 2 : (int) Math.floor(Math.log10(position)) + 1;
            return getActionName(prefix, methodName, position, targetPositionWidth);
        } else {
            return getActionName(prefix, actionName);
        }
    }

    protected static Path takeScreenshot(AryaSimulation simulation, RuntimeOptions options, String actionName, String label) {
        if(options.isAutoScreenshots()) {
            String screenshotName = actionName + (label == null ? "" : "_" + label) + ".png";
            System.out.println("AUTO " + (label == null ? "" : label.toUpperCase() + " ") + "SCREENSHOT: " + screenshotName);
            return simulation.takeScreenshot(screenshotName);
        }
        return null;
    }

}