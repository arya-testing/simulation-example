package dev.testment.core.util;

import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ReflectionUtilTests {

    static class Cat {
        private String name;
        private int weight;

        public static Cat createCat(String first, String last, int weight) {
            Cat cat = new Cat();
            cat.name = first + " " + last;
            cat.weight = weight;
            return cat;
        }

        private String getName() {
            return null;
        }

        protected static Cat setName(String name) {
            return null;
        }

        public Cat setName(String first, String last) {
            this.name = first + " " + last;
            return this;
        }
    }

    @Test
    public void testCreateInstance() {
        Cat cat = (Cat) ReflectionUtil.createInstance(Cat.class);
        assertThat(cat).isInstanceOf(Cat.class);
    }

    @Test
    public void testSetField() throws NoSuchFieldException {
        Cat cat = new Cat();
        Field field = Cat.class.getDeclaredField("name");
        ReflectionUtil.setField(field, cat, "Garfield");
        assertThat(cat.name).isEqualTo("Garfield");
        assertThat(field.isAccessible()).isFalse();
    }

    @Test
    public void testInvokeMethod() throws NoSuchMethodException {
        Cat cat = new Cat();
        Method method = Cat.class.getDeclaredMethod("setName", String.class, String.class);
        Cat returnedCat = (Cat) ReflectionUtil.invokeMethod(method, cat, "Mr.", "Whiskers");
        assertThat(cat.name).isEqualTo("Mr. Whiskers");
        assertThat(cat).isEqualTo(returnedCat);
    }

    @Test
    public void testInvokeStaticMethod() throws NoSuchMethodException {
        Method method = Cat.class.getDeclaredMethod("createCat", String.class, String.class, int.class);
        Cat cat = (Cat) ReflectionUtil.invokeStaticMethod(method, "Mrs.", "Paws", 9);
        assertThat(cat.name).isEqualTo("Mrs. Paws");
        assertThat(cat.weight).isEqualTo(9);
    }

    @Test
    public void testGetMethod() {
        Method method = ReflectionUtil.getMethod(Cat.class, "setName", String.class, String.class);
        assertThat(method).isNotNull();
    }

    @Test
    public void testGetSortedDeclaredMethods() throws NoSuchMethodException {
        List<Method> methods = ReflectionUtil.getDeclaredMethodsSortedByLineNumber(Cat.class);
        assertThat(methods).containsSequence(
                Cat.class.getDeclaredMethod("createCat", String.class, String.class, int.class),
                Cat.class.getDeclaredMethod("getName"),
                Cat.class.getDeclaredMethod("setName", String.class),
                Cat.class.getDeclaredMethod("setName", String.class, String.class)
        );
    }

}
