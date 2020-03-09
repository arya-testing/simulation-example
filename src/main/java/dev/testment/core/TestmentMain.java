package dev.testment.core;

import dev.testment.core.application.TestmentApplication;
import dev.testment.core.cli.Args;
import dev.testment.core.application.exceptions.AmbiguousApplicationException;
import dev.testment.core.application.exceptions.ApplicationNotFoundException;
import dev.testment.core.util.ReflectionUtil;
import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.util.Set;

public class TestmentMain {

    public static void main(String[] args) {
        String scanPrefix = Args.getValue(args, "scan-prefix", "");
        Reflections reflections = new Reflections(scanPrefix);
        Set<Class<?>> types = reflections.getTypesAnnotatedWith(TestmentApplication.class);
        if(types.size() < 1) {
            throw new ApplicationNotFoundException("No TestmentApplication found!");
        }
        if(types.size() > 1) {
            throw new AmbiguousApplicationException("Found " + types.size() + " TestmentApplication candidates! There should only be one");
        }
        for(Class<?> type : types) {
            Method method = ReflectionUtil.getMethod(type, "main", String[].class);
            ReflectionUtil.invokeStaticMethod(method, new Object[] { args });
            break;
        }
    }

}
