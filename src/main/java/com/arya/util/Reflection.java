package com.arya.util;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class Reflection {

    private Reflection() { }

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

    public static void invokeMethod(Method method, Object object, Object... args) {
        try {
            method.invoke(object, args);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e.getCause());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static void invokeStaticMethod(Method method, Object... args) {
        invokeMethod(method, null, args);
    }

    public static Method getMethod(Class<?> type, String name, Class<?>... parameterTypes) {
        try {
            return type.getMethod(name, parameterTypes);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Method> getSortedDeclaredMethods(Class<?> type) {
        TreeMap<Integer, Method> methods = new TreeMap<>();
        try {
            CtClass clazz = ClassPool.getDefault().get(type.getName());
            for(Method m : type.getDeclaredMethods()) {
                int lineNum = clazz.getDeclaredMethod(m.getName()).getMethodInfo().getLineNumber(0);
                methods.put(lineNum, m);
            }
        } catch(NotFoundException e) {
            throw new RuntimeException(e);
        }
        return new ArrayList<>(methods.values());
    }

}
