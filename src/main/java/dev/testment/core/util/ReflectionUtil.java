package dev.testment.core.util;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class ReflectionUtil {

    private ReflectionUtil() { }

    public static Object createInstance(Class<?> type) {
        try {
            return type.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setField(Field field, Object object, Object value) {
        field.setAccessible(true);
        try {
            field.set(object, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } finally {
            field.setAccessible(false);
        }
    }

    public static Object invokeMethod(Method method, Object object, Object... args) {
        try {
            return method.invoke(object, args);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e.getCause());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object invokeStaticMethod(Method method, Object... args) {
        return invokeMethod(method, null, args);
    }

    public static Method getMethod(Class<?> type, String name, Class<?>... parameterTypes) {
        try {
            return type.getMethod(name, parameterTypes);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Method> getDeclaredMethodsSortedByLineNumber(Class<?> type) {
        TreeMap<Integer, Method> methods = new TreeMap<>();
        try {
            CtClass clazz = getCtClass(type.getName());
            for(Method m : type.getDeclaredMethods()) {
                CtClass[] paramTypes = getMethodParamCtClasses(m);
                int lineNum = clazz.getDeclaredMethod(m.getName(), paramTypes).getMethodInfo().getLineNumber(0);
                methods.put(lineNum, m);
            }
        } catch(NotFoundException e) {
            throw new RuntimeException(e);
        }
        return new ArrayList<>(methods.values());
    }

    private static CtClass[] getMethodParamCtClasses(Method m) {
        Class<?>[] parameterTypes = m.getParameterTypes();
        CtClass[] classes = new CtClass[parameterTypes.length];
        for(int i = 0; i < parameterTypes.length; i++) {
            classes[i] = getCtClass(parameterTypes[i].getName());
        }
        return classes;
    }

    private static CtClass getCtClass(String name) {
        try {
            return ClassPool.getDefault().get(name);
        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
