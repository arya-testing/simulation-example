package dev.testment.core.logger;

import dev.testment.core.cli.AnsiColors;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LoggerUtilTests {

    @Test
    public void testGetLevelColor() {
        assertThat(LoggerUtil.getLevelColor(LogLevel.ERROR)).isEqualTo(AnsiColors.RED);
        assertThat(LoggerUtil.getLevelColor(LogLevel.WARNING)).isEqualTo(AnsiColors.YELLOW);
        assertThat(LoggerUtil.getLevelColor(LogLevel.DEBUG)).isEqualTo(AnsiColors.BLUE);
        assertThat(LoggerUtil.getLevelColor(LogLevel.VERBOSE)).isEqualTo(AnsiColors.PURPLE);
        assertThat(LoggerUtil.getLevelColor(LogLevel.INFO)).isEqualTo(AnsiColors.GREEN);
    }

}
