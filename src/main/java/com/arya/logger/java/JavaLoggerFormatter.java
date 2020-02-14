package com.arya.logger.java;

import com.arya.logger.AnsiColors;
import com.arya.logger.LogLevel;
import com.arya.logger.LoggerUtil;
import com.arya.util.StringUtil;

import java.util.Date;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

public class JavaLoggerFormatter extends SimpleFormatter {

    private static final String format = "[%1$tF %1$tT] %2$s [%3$-7s] %4$s %5$-45.45s: %6$s %n";

    @Override
    public synchronized String format(LogRecord lr) {
        LogLevel logLevel = JavaLoggerUtil.getLogLevel(lr.getLevel());
        return String.format(format,
                new Date(lr.getMillis()),
                LoggerUtil.getLevelColor(logLevel),
                logLevel.name(),
                AnsiColors.RESET,
                StringUtil.getLastCharacters(lr.getLoggerName(), 45),
                lr.getMessage()
        );
    }

}
