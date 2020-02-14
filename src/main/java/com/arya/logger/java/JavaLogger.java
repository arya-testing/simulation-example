package com.arya.logger.java;

import com.arya.logger.LogLevel;
import com.arya.logger.Logger;

public class JavaLogger implements Logger {

    private final java.util.logging.Logger logger;

    public JavaLogger(java.util.logging.Logger logger) {
        this.logger = logger;
    }

    @Override
    public void setLevel(LogLevel level) {
        this.logger.setLevel(JavaLoggerUtil.getJavaLevel(level));
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

    private void log(LogLevel level, String format, String... args) {
        this.logger.log(JavaLoggerUtil.getJavaLevel(level), String.format(format, args));
    }

}
