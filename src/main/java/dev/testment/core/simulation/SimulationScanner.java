package dev.testment.core.simulation;

import dev.testment.core.simulation.exceptions.AmbiguousSimulationException;
import dev.testment.core.simulation.exceptions.IncompatibleSimulationTypeException;
import dev.testment.core.simulation.exceptions.SimulationNotFoundException;
import dev.testment.core.util.ReflectionUtil;
import org.reflections.Reflections;

import java.util.HashSet;
import java.util.Set;

public class SimulationScanner {

    public TestmentSimulation scan(String prefix, String name, boolean failIfNotFound) {
        Reflections reflections = new Reflections(prefix);
        Set<Class<?>> types = reflections.getTypesAnnotatedWith(Simulation.class);
        Set<Class<?>> typesMatchedByName = this.filterTypesBySimulationName(types, name);

        if(typesMatchedByName.size() <= 0) {
            if(failIfNotFound) {
                throw new SimulationNotFoundException(String.format("Simulation '%s' not found", name));
            } else {
                return null;
            }
        }

        if(typesMatchedByName.size() > 1) {
            throw new AmbiguousSimulationException(name, typesMatchedByName);
        }

        Class<?> type = typesMatchedByName.iterator().next();
        if(TestmentSimulation.class.isAssignableFrom(type)) {
            return (TestmentSimulation) ReflectionUtil.createInstance(type);
        } else {
            throw new IncompatibleSimulationTypeException("Simulation " + name + " is not of type " + TestmentSimulation.class.getCanonicalName());
        }
    }

    private Set<Class<?>> filterTypesBySimulationName(Set<Class<?>> types, String name) {
        Set<Class<?>> matchingTypes = new HashSet<>();
        for(Class<?> type : types) {
            Simulation sim = type.getAnnotation(Simulation.class);
            if(name.equalsIgnoreCase(sim.value())) {
                matchingTypes.add(type);
            }
        }
        return matchingTypes;
    }

}
