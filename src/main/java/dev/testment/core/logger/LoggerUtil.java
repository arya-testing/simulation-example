package dev.testment.core.logger;

import dev.testment.core.cli.AnsiColors;

public class LoggerUtil {

    public static String getLevelColor(LogLevel logLevel) {
        if(logLevel == LogLevel.ERROR)
            return AnsiColors.RED;
        if(logLevel == LogLevel.WARNING)
            return AnsiColors.YELLOW;
        if(logLevel == LogLevel.DEBUG)
            return AnsiColors.BLUE;
        if(logLevel == LogLevel.VERBOSE)
            return AnsiColors.PURPLE;
        else
            return AnsiColors.GREEN;
    }

}
