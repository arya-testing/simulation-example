package dev.testment.core.util;

import dev.testment.core.util.exceptions.UnsupportedTypeException;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class StringUtilTests {

    @Test
    public void testParseBoolean() {
        boolean value;

        value = StringUtil.parse("true", Boolean.class);
        assertThat(value).isTrue();

        value = StringUtil.parse("t", boolean.class);
        assertThat(value).isTrue();

        value = StringUtil.parse("yes", Boolean.class);
        assertThat(value).isTrue();

        value = StringUtil.parse("y", Boolean.class);
        assertThat(value).isTrue();

        value = StringUtil.parse("false", Boolean.class);
        assertThat(value).isFalse();

        value = StringUtil.parse("f", Boolean.class);
        assertThat(value).isFalse();

        value = StringUtil.parse("no", Boolean.class);
        assertThat(value).isFalse();

        value = StringUtil.parse("n", Boolean.class);
        assertThat(value).isFalse();

        value = StringUtil.parse("sdfsdf", boolean.class);
        assertThat(value).isFalse();
    }

    @Test
    public void testParseByte() {
        byte value = StringUtil.parse("-124", Byte.class);
        assertThat(value).isEqualTo((byte)-124);
    }

    @Test
    public void testParseShort() {
        short value = StringUtil.parse("400", short.class);
        assertThat(value).isEqualTo((short)400);
    }

    @Test
    public void testParseChar() {
        char value = StringUtil.parse("string", Character.class);
        assertThat(value).isEqualTo('s');
    }

    @Test
    public void testParseInt() {
        int value = StringUtil.parse("-405005945", int.class);
        assertThat(value).isEqualTo(-405005945);
    }

    @Test
    public void testParseLong() {
        long value = StringUtil.parse("234234405005945", long.class);
        assertThat(value).isEqualTo(234234405005945L);
    }

    @Test
    public void testParseFloat() {
        Float value = StringUtil.parse("44.982", Float.class);
        assertThat(value).isEqualTo(44.982F);
    }

    @Test
    public void testParseDouble() {
        Double value = StringUtil.parse("440948098.982", double.class);
        assertThat(value).isEqualTo(440948098.982);
    }

    @Test
    public void testParseString() {
        String value = StringUtil.parse("Hello world", String.class);
        assertThat(value).isEqualTo("Hello world");
    }

    @Test(expected = UnsupportedTypeException.class)
    public void testFailToParseStringWithSupportedType() {
        StringUtil.parse("{}", Object.class);
    }

    @Test
    public void testGetLastCharacters() {
        String str = "hello world!!!";
        String value = StringUtil.getLastCharacters(str, 8);
        assertThat(value).isEqualTo("world!!!");
    }

    @Test
    public void testGetLastCharactersWhenNumIsGreaterThanOrEqualToLength() {
        String str = "hello world!!!";
        String value = StringUtil.getLastCharacters(str, 14);
        assertThat(value).isEqualTo("hello world!!!");
    }

}
