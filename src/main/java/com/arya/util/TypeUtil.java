package com.arya.util;

import java.util.HashMap;
import java.util.Map;

public class TypeUtil {

    private static final Map<Class<?>, Class<?>> map = new HashMap<>();

    static {
        map.put(Boolean.class, boolean.class);
        map.put(Byte.class, byte.class);
        map.put(Short.class, short.class);
        map.put(Character.class, char.class);
        map.put(Integer.class, int.class);
        map.put(Long.class, long.class);
        map.put(Float.class, float.class);
        map.put(Double.class, double.class);
        map.put(boolean.class, Boolean.class);
        map.put(byte.class, Byte.class);
        map.put(short.class, Short.class);
        map.put(char.class, Character.class);
        map.put(int.class, Integer.class);
        map.put(long.class, Long.class);
        map.put(float.class, Float.class);
        map.put(double.class, Double.class);
    }

    public static boolean isAssignableFrom(Class<?> lhs, Class<?> rhs) {
        Validation.notNull("lhs", lhs);
        Validation.notNull("rhs", rhs);
        if(lhs.isAssignableFrom(rhs))
            return true;
        return map.get(lhs) != null && map.get(lhs).isAssignableFrom(rhs);
    }

}
