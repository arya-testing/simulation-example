package com.arya.logger.java;

import com.arya.logger.LogLevel;
import com.arya.logger.Logger;

import java.io.OutputStream;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;

public class JavaLoggerFactory {

    private JavaLoggerFactory() {
    }

    public static <T> Logger getLogger(Class<T> clazz) {
        java.util.logging.Logger logger = java.util.logging.Logger.getLogger(clazz.getName());
        logger.setUseParentHandlers(false);
        ConsoleHandler handler = getConsoleHandler();
        handler.setFormatter(new JavaLoggerFormatter());
        logger.addHandler(handler);
        return new JavaLogger(logger);
    }

    public static void setGlobalLevel(LogLevel logLevel) {
        java.util.logging.Logger rootLogger = LogManager.getLogManager().getLogger("");
        Level javaLogLevel = JavaLoggerUtil.getJavaLevel(logLevel);
        rootLogger.setLevel(javaLogLevel);
        for(Handler h : rootLogger.getHandlers()) {
            h.setFormatter(new JavaLoggerFormatter());
            h.setLevel(javaLogLevel);
        }
    }

    private static ConsoleHandler getConsoleHandler() {
        return new ConsoleHandler() {
            protected synchronized void setOutputStream(OutputStream out) throws SecurityException {
                super.setOutputStream(System.out);
            }
        };
    }

}
