package dev.testment.core.util;

public class Validation {

    public static void notNull(String name, Object value) {
        if(value == null)
            throw new NullPointerException(String.format("'%s' cannot be null", name));
    }

    public static boolean isEmpty(String value) {
        return value == null || value.isEmpty();
    }

    public static boolean isNotEmpty(String value) {
        return !isEmpty(value);
    }

}
