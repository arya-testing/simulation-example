package com.arya;

import com.arya.util.Reflection;
import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.util.Set;

public class AryaApplicationLoader {

    public static void main(String[] args) {
        Reflections reflections = new Reflections();
        Set<Class<?>> types = reflections.getTypesAnnotatedWith(AryaApplication.class);
        if(types.size() < 1) {
            throw new RuntimeException("No AryaApplication found!");
        }
        if(types.size() > 1) {
            throw new RuntimeException("Found " + types.size() + " AryaApplication candidates! There should only be one");
        }
        for(Class<?> type : types) {
            Method method = Reflection.getMethod(type, "main", String[].class);
            Reflection.invokeStaticMethod(method, new Object[] { args });
            break;
        }
    }

}
