package com.arya.logger.basic;

import com.arya.logger.AnsiColors;
import com.arya.logger.LogLevel;
import com.arya.logger.Logger;
import com.arya.logger.LoggerUtil;
import com.arya.util.StringUtil;
import com.arya.util.Validation;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class BasicLogger implements Logger {

    private static Map<LogLevel, Integer> levelPriority = new HashMap<>();
    private static String logFormat = "[%1$tF %1$tT] %2$s [%3$-7s] %4$s %5$-45.45s: %6$s %n";

    static {
        levelPriority.put(LogLevel.OFF, 0);
        levelPriority.put(LogLevel.ERROR, 1);
        levelPriority.put(LogLevel.WARNING, 2);
        levelPriority.put(LogLevel.INFO, 3);
        levelPriority.put(LogLevel.DEBUG, 4);
        levelPriority.put(LogLevel.VERBOSE, 5);
    }

    private LogLevel level = LogLevel.INFO;
    private String name;

    protected BasicLogger(String name) {
        Validation.notNull("name", name);
        this.name = name;
    }

    @Override
    public void setLevel(LogLevel level) {
        Validation.notNull("level", level);
        this.level = level;
    }

    @Override
    public void error(String format, String... args) {
        this.log(LogLevel.ERROR, format, args);
    }

    @Override
    public void warning(String format, String... args) {
        this.log(LogLevel.WARNING, format, args);
    }

    @Override
    public void info(String format, String... args) {
        this.log(LogLevel.INFO, format, args);
    }

    @Override
    public void debug(String format, String... args) {
        this.log(LogLevel.DEBUG, format, args);
    }

    @Override
    public void verbose(String format, String... args) {
        this.log(LogLevel.VERBOSE, format, args);
    }

    private void log(LogLevel logLevel, String format, String... args) {
        if(shouldLog(logLevel)) {
            String logMessage = String.format(logFormat,
                    new Date(),
                    LoggerUtil.getLevelColor(logLevel),
                    logLevel.name(),
                    AnsiColors.RESET,
                    StringUtil.getLastCharacters(this.name, 45),
                    String.format(format, args));
            System.out.print(logMessage);
        }
    }

    private boolean shouldLog(LogLevel logLevel) {
        return logLevel.compareTo(this.level) <= 0;
    }

}
