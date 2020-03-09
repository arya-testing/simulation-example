package dev.testment.core.logger.basic;

import dev.testment.core.cli.AnsiColors;
import dev.testment.core.logger.Logger;
import dev.testment.core.logger.LoggerUtil;
import dev.testment.core.util.StringUtil;
import dev.testment.core.util.Validation;
import dev.testment.core.logger.LogLevel;

import java.io.PrintStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class BasicLogger implements Logger {

    private static Map<LogLevel, Integer> levelPriority = new HashMap<>();
    private static String logFormat = "[%1$tF %1$tT] %2$s [%3$-7s] %4$s %5$-60.60s: %6$s %n";

    static {
        levelPriority.put(LogLevel.OFF, 0);
        levelPriority.put(LogLevel.ERROR, 1);
        levelPriority.put(LogLevel.WARNING, 2);
        levelPriority.put(LogLevel.INFO, 3);
        levelPriority.put(LogLevel.DEBUG, 4);
        levelPriority.put(LogLevel.VERBOSE, 5);
    }

    private final PrintStream printStream;
    private LogLevel level = LogLevel.INFO;
    private String name;

    protected BasicLogger(String name, PrintStream printStream) {
        Validation.notNull("name", name);
        this.name = name;
        this.printStream = printStream;
    }

    @Override
    public void setLevel(LogLevel level) {
        Validation.notNull("level", level);
        this.level = level;
    }

    @Override
    public LogLevel getLevel() {
        return this.level;
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

    public PrintStream getPrintStream() {
        return this.printStream;
    }

    public String getName() {
        return this.name;
    }

    private void log(LogLevel logLevel, String format, String... args) {
        Validation.notNull("format", format);
        if(shouldLog(logLevel)) {
            String logMessage = String.format(logFormat,
                    new Date(),
                    LoggerUtil.getLevelColor(logLevel),
                    logLevel.name(),
                    AnsiColors.RESET,
                    StringUtil.getLastCharacters(this.name, 60),
                    String.format(format, args));
            this.printStream.print(logMessage);
        }
    }

    private boolean shouldLog(LogLevel logLevel) {
        return logLevel.compareTo(this.level) <= 0;
    }

}
