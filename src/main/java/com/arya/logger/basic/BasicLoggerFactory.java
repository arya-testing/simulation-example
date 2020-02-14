package com.arya.logger.basic;

import com.arya.logger.LogLevel;
import com.arya.logger.Logger;
import com.arya.util.Validation;

import java.util.ArrayList;
import java.util.List;

public class BasicLoggerFactory {

    private static LogLevel logLevel = LogLevel.INFO;
    private static List<Logger> loggers = new ArrayList<>();

    private BasicLoggerFactory() {
    }

    public static <T> Logger getLogger(Class<T> clazz) {
        Validation.notNull("clazz", clazz);
        Logger logger = new BasicLogger(clazz.getName());
        logger.setLevel(logLevel);
        loggers.add(logger);
        return logger;
    }

    public static void setGlobalLevel(LogLevel logLevel) {
        Validation.notNull("logLevel", logLevel);
        BasicLoggerFactory.logLevel = logLevel;
        for(Logger logger : loggers) {
            logger.setLevel(logLevel);
        }
    }

}
