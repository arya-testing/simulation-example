package dev.testment.core.cli;

import dev.testment.core.cli.exceptions.InvalidValueFormatException;
import dev.testment.core.cli.exceptions.MissingValueException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.assertj.core.api.Assertions.assertThat;

public class ArgsTests {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testArgExistsWithValue() {
        String[] args = new String[]{"--arg1", "value1", "--arg2"};
        assertThat(Args.exists(args, "arg1")).isTrue();
    }

    @Test
    public void testArgExistsWithoutValue() {
        String[] args = new String[]{"--arg1", "value1", "--arg2"};
        assertThat(Args.exists(args, "arg2")).isTrue();
    }

    @Test
    public void testArgDoesNotExist() {
        String[] args = new String[]{"--arg1", "value1", "arg2"};
        assertThat(Args.exists(args, "arg2")).isFalse();
    }

    @Test
    public void testGetValue() {
        String[] args = new String[]{"--arg1", "value1"};
        assertThat(Args.getValue(args, "arg1")).isEqualTo("value1");
    }

    @Test
    public void testFailToGetValueWithoutArg() {
        String[] args = new String[]{"--arg1", "value1"};
        expectedException.expect(MissingValueException.class);
        expectedException.expectMessage("must have a value");
        Args.getValue(args, "arg2");
    }

    @Test(expected = MissingValueException.class)
    public void testFailToGetValueWithoutValue() {
        String[] args = new String[]{"--arg1"};
        Args.getValue(args, "arg1");
    }

    @Test
    public void testGetDefaultValue() {
        String[] args = new String[]{};
        String value = Args.getValue(args, "arg1", "value2");
        assertThat(value).isEqualTo("value2");
    }

    @Test(expected = MissingValueException.class)
    public void testFailToGetDefaultValueWithoutValue() {
        String[] args = new String[]{"--arg1"};
        Args.getValue(args, "arg1", "value2");
    }

    @Test
    public void testDoNotGetDefaultValue() {
        String[] args = new String[]{"--arg1", "value1"};
        String value = Args.getValue(args, "arg1", "value2");
        assertThat(value).isEqualTo("value1");
    }

    @Test
    public void testGetIntValue() {
        String[] args = new String[]{"--arg1", "123"};
        int value = Args.getIntValue(args, "arg1");
        assertThat(value).isEqualTo(123);
    }

    @Test
    public void testFailToGetIntValueWithInvalidFormat() {
        String[] args = new String[]{"--arg1", "value1"};
        expectedException.expect(InvalidValueFormatException.class);
        expectedException.expectMessage("must be an integer");
        Args.getIntValue(args, "arg1");
    }

    @Test
    public void testFailToGetIntValueWithoutArg() {
        String[] args = new String[]{"--arg1", "value1"};
        expectedException.expect(MissingValueException.class);
        expectedException.expectMessage("must have a value");
        Args.getIntValue(args, "arg2");
    }

    @Test
    public void testGetDefaultIntValue() {
        String[] args = new String[]{};
        int value = Args.getIntValue(args, "arg1", 456);
        assertThat(value).isEqualTo(456);
    }

    @Test
    public void testDoNotGetDefaultIntValue() {
        String[] args = new String[]{"--arg1", "123"};
        int value = Args.getIntValue(args, "arg1", 456);
        assertThat(value).isEqualTo(123);
    }

    @Test
    public void testFailToGetIntValueWithDefaultValueAndInvalidFormat() {
        String[] args = new String[]{"--arg1", "value1"};
        expectedException.expect(InvalidValueFormatException.class);
        expectedException.expectMessage("must be an integer");
        Args.getIntValue(args, "arg1", 456);
    }

}
