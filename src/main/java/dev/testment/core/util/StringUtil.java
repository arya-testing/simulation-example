package dev.testment.core.util;

import dev.testment.core.util.exceptions.UnsupportedTypeException;

public class StringUtil {

    private StringUtil() {}

    public static <T> T parse(String value, Class<T> clazz) {
        Validation.notNull("value", value);
        Validation.notNull("clazz", clazz);

        if(TypeUtil.isAssignableFrom(Boolean.class, clazz)) {
            return (T)(Boolean)anyEqualsIgnoreCase(value, "true", "t", "yes", "y");
        } else if(TypeUtil.isAssignableFrom(Byte.class, clazz)) {
            return (T)(Byte)Byte.parseByte(value);
        } else if(TypeUtil.isAssignableFrom(Short.class, clazz)) {
            return (T)(Short)Short.parseShort(value);
        } else if(TypeUtil.isAssignableFrom(Character.class, clazz)) {
            return (T)(Character)value.charAt(0);
        } else if(TypeUtil.isAssignableFrom(Integer.class, clazz)) {
            return (T)(Integer)Integer.parseInt(value);
        } else if(TypeUtil.isAssignableFrom(Long.class, clazz)) {
            return (T)(Long)Long.parseLong(value);
        } else if(TypeUtil.isAssignableFrom(Float.class, clazz)) {
            return (T)(Float)Float.parseFloat(value);
        } else if(TypeUtil.isAssignableFrom(Double.class, clazz)) {
            return (T)(Double)Double.parseDouble(value);
        } else if(String.class.isAssignableFrom(clazz)) {
            return (T)value;
        } else {
            throw new UnsupportedTypeException("Parsing values of type " + clazz + " is not supported");
        }
    }

    public static String getLastCharacters(String str, int num) {
        Validation.notNull("str", str);
        Validation.notNull("num", num);

        if(str.length() <= num)
            return str;
        return str.substring(str.length() - num);
    }

    private static boolean anyEqualsIgnoreCase(String str, String... list) {
        Validation.notNull("str", str);
        Validation.notNull("list", list);

        for(String match : list) {
            if(str.equalsIgnoreCase(match))
                return true;
        }
        return false;
    }

}
