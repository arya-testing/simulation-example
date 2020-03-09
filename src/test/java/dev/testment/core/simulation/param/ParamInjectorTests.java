package dev.testment.core.simulation.param;

import dev.testment.core.cli.exceptions.InvalidValueFormatException;
import dev.testment.core.cli.exceptions.MissingValueException;
import dev.testment.core.simulation.AbstractSimulation;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

@RunWith(MockitoJUnitRunner.class)
public class ParamInjectorTests {

    @InjectMocks
    private ParamInjector paramInjector;

    @Test
    public void testInjectWithMinimumArgs() {
        TestSimulation simulation = new TestSimulation();

        String[] args = new String[]{"--user", "johndoe01"};
        paramInjector.inject(args, simulation);

        assertThat(simulation.username).isEqualTo("johndoe01");
        assertThat(simulation.password).isNull();
        assertThat(simulation.hourlyRate).isEqualTo(15.45);
        assertThat(simulation.isManager).isFalse();
    }

    @Test
    public void testInjectWithMaximumArgs() {
        TestSimulation simulation = new TestSimulation();

        String[] args = new String[]{"--user", "johndoe01", "--pass", "password$1", "--hourlyRate", "45.2", "--manager"};
        paramInjector.inject(args, simulation);

        assertThat(simulation.username).isEqualTo("johndoe01");
        assertThat(simulation.password).isEqualTo("password$1");
        assertThat(simulation.hourlyRate).isEqualTo(45.2);
        assertThat(simulation.isManager).isTrue();
    }

    @Test
    public void testFailToInjectWithRequiredArg() {
        TestSimulation simulation = new TestSimulation();
        String[] args = new String[]{};
        MissingValueException ex = assertThrows(MissingValueException.class, () -> paramInjector.inject(args, simulation));
        assertThat(ex.getMessage()).isEqualTo("Argument 'user' must have a value");
    }

    @Test
    public void testFailToInjectWithInvalidArgType() {
        TestSimulation simulation = new TestSimulation();
        String[] args = new String[]{"--user", "johndoe01", "--hourlyRate", "invalid"};
        InvalidValueFormatException ex = assertThrows(InvalidValueFormatException.class, () -> paramInjector.inject(args, simulation));
        assertThat(ex.getMessage()).isEqualTo("Argument 'hourlyRate' must be of type 'double'");
    }

    private static abstract class BaseSimulation extends AbstractSimulation {
        @Param(defaultValue = "15.45")
        protected double hourlyRate;

        @Override
        public Path saveHar() {
            return null;
        }

        @Override
        public Path takeScreenshot(String name) {
            return null;
        }
    }

    private static class TestSimulation extends BaseSimulation {
        @Param(name = "user")
        private String username;

        @Param(name = "pass", required = false)
        private String password;

        @Param(name = "manager")
        private boolean isManager;
    }

}