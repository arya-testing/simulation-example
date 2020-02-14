package com.arya.logger.java;

import com.arya.logger.LogLevel;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class JavaLoggerUtil {

    private static final Map<LogLevel, Level> logLevelToJavaLevel = new HashMap<>();
    private static final Map<Level, LogLevel> javaLevelToLogLevel = new HashMap<>();

    static {
        logLevelToJavaLevel.put(LogLevel.OFF, Level.OFF);
        logLevelToJavaLevel.put(LogLevel.ERROR, Level.SEVERE);
        logLevelToJavaLevel.put(LogLevel.WARNING, Level.WARNING);
        logLevelToJavaLevel.put(LogLevel.INFO, Level.INFO);
        logLevelToJavaLevel.put(LogLevel.DEBUG, Level.FINE);
        logLevelToJavaLevel.put(LogLevel.VERBOSE, Level.FINEST);

        for(Map.Entry<LogLevel, Level> entry : logLevelToJavaLevel.entrySet()) {
            javaLevelToLogLevel.put(entry.getValue(), entry.getKey());
        }
    }

    private JavaLoggerUtil() {
    }

    public static Level getJavaLevel(LogLevel level) {
        return logLevelToJavaLevel.getOrDefault(level, Level.INFO);
    }

    public static LogLevel getLogLevel(Level level) {
        return javaLevelToLogLevel.get(level);
    }

}
