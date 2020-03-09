package dev.testment.core.util;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.assertj.core.api.Assertions.assertThat;

public class ValidationTests {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testNotNull() {
        Validation.notNull("value", new Object());
    }

    @Test
    public void testFailNotNull() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("value");
        expectedException.expectMessage("cannot be null");
        Validation.notNull("value", null);
    }

    @Test
    public void testIsEmptyWithNullPtr() {
        assertThat(Validation.isEmpty(null)).isTrue();
    }

    @Test
    public void testIsEmptyWithEmptyString() {
        assertThat(Validation.isEmpty("")).isTrue();
    }

    @Test
    public void testIsEmptyWithNonEmptyString() {
        assertThat(Validation.isEmpty("test")).isFalse();
    }

    @Test
    public void testIsNotEmptyWithNullPtr() {
        assertThat(Validation.isNotEmpty(null)).isFalse();
    }

    @Test
    public void testIsNotEmptyWithEmptyString() {
        assertThat(Validation.isNotEmpty("")).isFalse();
    }

    @Test
    public void testIsNotEmptyWithNonEmptyString() {
        assertThat(Validation.isNotEmpty("test")).isTrue();
    }

}
