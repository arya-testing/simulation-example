package com.arya.logger;

import com.arya.logger.basic.BasicLoggerFactory;

public class LoggerFactory {

    public static <T> Logger getLogger(Class<T> clazz) {
        return BasicLoggerFactory.getLogger(clazz);
    }

    public static void setGlobalLevel(LogLevel logLevel) {
        BasicLoggerFactory.setGlobalLevel(logLevel);
    }

}
