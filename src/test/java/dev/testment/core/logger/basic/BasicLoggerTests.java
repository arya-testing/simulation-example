package dev.testment.core.logger.basic;

import dev.testment.core.cli.AnsiColors;
import dev.testment.core.logger.LoggerUtil;
import dev.testment.core.logger.LogLevel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.PrintStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BasicLoggerTests {

    @Mock
    private PrintStream printStream;

    private BasicLogger basicLogger;

    @Before
    public void setUp() {
        basicLogger = new BasicLogger("label", printStream);
    }

    @Test
    public void testLogError() {
        this.basicLogger.setLevel(LogLevel.ERROR);
        this.basicLogger.error("ERROR: %s!", "This is a test");
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(this.printStream, times(1)).print((captor.capture()));
        String message = captor.getValue();
        assertThat(message).contains("label");
        assertThat(message).contains(LoggerUtil.getLevelColor(LogLevel.ERROR));
        assertThat(message).contains(AnsiColors.RESET);
        assertThat(message).contains("ERROR: This is a test!");
    }

    @Test
    public void testLogWarning() {
        this.basicLogger.setLevel(LogLevel.WARNING);
        this.basicLogger.warning("WARNING: %s!", "This is a test");
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(this.printStream, times(1)).print((captor.capture()));
        String message = captor.getValue();
        assertThat(message).contains("label");
        assertThat(message).contains(LoggerUtil.getLevelColor(LogLevel.WARNING));
        assertThat(message).contains(AnsiColors.RESET);
        assertThat(message).contains("WARNING: This is a test!");
    }

    @Test
    public void testLogInfo() {
        this.basicLogger.setLevel(LogLevel.INFO);
        this.basicLogger.info("INFO: %s!", "This is a test");
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(this.printStream, times(1)).print((captor.capture()));
        String message = captor.getValue();
        assertThat(message).contains("label");
        assertThat(message).contains(LoggerUtil.getLevelColor(LogLevel.INFO));
        assertThat(message).contains(AnsiColors.RESET);
        assertThat(message).contains("INFO: This is a test!");
    }

    @Test
    public void testLogDebug() {
        this.basicLogger.setLevel(LogLevel.DEBUG);
        this.basicLogger.debug("DEBUG: %s!", "This is a test");
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(this.printStream, times(1)).print((captor.capture()));
        String message = captor.getValue();
        assertThat(message).contains("label");
        assertThat(message).contains(LoggerUtil.getLevelColor(LogLevel.DEBUG));
        assertThat(message).contains(AnsiColors.RESET);
        assertThat(message).contains("DEBUG: This is a test!");
    }

    @Test
    public void testLogVerbose() {
        this.basicLogger.setLevel(LogLevel.VERBOSE);
        this.basicLogger.verbose("VERBOSE: %s!", "This is a test");
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(this.printStream, times(1)).print((captor.capture()));
        String message = captor.getValue();
        assertThat(message).contains("label");
        assertThat(message).contains(LoggerUtil.getLevelColor(LogLevel.VERBOSE));
        assertThat(message).contains(AnsiColors.RESET);
        assertThat(message).contains("VERBOSE: This is a test!");
    }

    @Test
    public void testDoNotLogWithOffLogLevel() {
        this.basicLogger.setLevel(LogLevel.OFF);
        this.basicLogger.error("ERROR: %s!", "This is a test");
        verifyNoInteractions(this.printStream);
    }

    @Test
    public void testDoNotLogDebugMessagesWithInfoLogLevel() {
        this.basicLogger.setLevel(LogLevel.INFO);
        this.basicLogger.debug("DEBUG: %s!", "This is a test");
        verifyNoInteractions(this.printStream);
    }

}
