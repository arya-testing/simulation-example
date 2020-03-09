package dev.testment.core.simulation.exceptions;

import java.util.Collection;

public class AmbiguousSimulationException extends RuntimeException {
    public AmbiguousSimulationException(String message) {
        super(message);
    }

    public AmbiguousSimulationException(String name, Collection<Class<?>> types) {
        this(getMessage(name, types));
    }

    private static String getMessage(String name, Collection<Class<?>> types) {
        return String.format("Found %d candidate simulations with name '%s'! There should be only one: %s", types.size(), name, types);
    }
}
