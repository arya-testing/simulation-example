package dev.testment.core.selenium.simulation;

import dev.testment.core.browsermob.services.StandaloneBrowserMobProxyService;
import dev.testment.core.selenium.SeleniumPropertyKeys;
import dev.testment.core.simulation.BrowserName;
import dev.testment.core.simulation.exceptions.UnsupportedBrowserException;
import dev.testment.core.browsermob.services.BrowserMobProxyService;
import dev.testment.core.browsermob.services.EmbeddedBrowserMobProxyService;
import dev.testment.core.logger.LoggerName;
import net.lightbody.bmp.proxy.CaptureType;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SeleniumSimulationTests {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Mock private EmbeddedBrowserMobProxyService embeddedBmpServiceMock;
    @Mock private StandaloneBrowserMobProxyService standaloneBmpServiceMock;
    @Mock private ChromeDriver chromeDriverMock;
    @Mock private FirefoxDriver firefoxDriverMock;
    @Mock private RemoteWebDriver remoteWebDriverMock;

    private BaseSeleniumSimulation seleniumSimulation;
    private String simPath;

    @Before
    public void setUp() {
        simPath = temporaryFolder.getRoot().toPath().toString();

        this.seleniumSimulation = spy(new BaseSeleniumSimulation());

        // Set up simulation.
        this.seleniumSimulation.setSimulationPath(temporaryFolder.getRoot().toPath().toString());
        this.seleniumSimulation.browserName = BrowserName.CHROME;
        this.seleniumSimulation.hubUrl = "http://hub:4444";
        this.seleniumSimulation.proxyUrl = "http://proxy:8080";

        // Reset global state.
        System.clearProperty(SeleniumPropertyKeys.CHROME_DRIVER);
        System.clearProperty(SeleniumPropertyKeys.FIREFOX_DRIVER);
        System.clearProperty(SeleniumPropertyKeys.CHROME_SILENT_OUTPUT);
        java.util.logging.Logger.getLogger(LoggerName.SELENIUM).setLevel(Level.INFO);
    }

    @Test
    public void testSetUp() {
        // Mock createBmpService() and createWebDriver().
        doReturn(this.standaloneBmpServiceMock).when(this.seleniumSimulation).createBmpService();
        doReturn(this.remoteWebDriverMock).when(this.seleniumSimulation).createWebDriver(anyString());

        this.seleniumSimulation.setUp();

        // Test behavior of super.setUp();
        assertThat(this.seleniumSimulation.getSimulationPath()).isEqualTo(Paths.get(this.simPath).toAbsolutePath().toString());
        assertThat(this.seleniumSimulation.getTmpPath()).isEqualTo(Paths.get(this.simPath, "tmp").toAbsolutePath().toString());
        assertThat(this.seleniumSimulation.getLogPath()).isEqualTo(Paths.get(this.simPath, "tmp", "log.txt").toAbsolutePath().toString());
        assertThat(this.seleniumSimulation.getHarPath()).isEqualTo(Paths.get(this.simPath, "tmp", "recording.har").toAbsolutePath().toString());
        assertThat(this.seleniumSimulation.getActionsPath()).isEqualTo(Paths.get(this.simPath, "tmp", "actions.json").toAbsolutePath().toString());
        assertThat(this.seleniumSimulation.getScreenshotsPath()).isEqualTo(Paths.get(this.simPath, "tmp", "screenshots").toAbsolutePath().toString());
        assertThat(Paths.get(this.simPath, "tmp")).isDirectory();
        assertThat(Paths.get(this.simPath, "tmp", "screenshots")).isEmptyDirectory();
        assertThat(this.seleniumSimulation.proxy).isEqualTo(this.standaloneBmpServiceMock);
        assertThat(this.seleniumSimulation.driver).isEqualTo(this.remoteWebDriverMock);

        verify(this.seleniumSimulation).createBmpService();
        verify(this.seleniumSimulation).createWebDriver(BrowserName.CHROME);
        verify(this.standaloneBmpServiceMock).newHar();
    }

    @Test
    public void testTeardown() {
        this.seleniumSimulation.driver = this.remoteWebDriverMock;
        this.seleniumSimulation.proxy = this.standaloneBmpServiceMock;

        this.seleniumSimulation.tearDown();

        verify(this.remoteWebDriverMock).quit();
        verify(this.standaloneBmpServiceMock).stop();
    }

    @Test
    public void testTeardownWithoutDriverOrProxy() {
        this.seleniumSimulation.tearDown();
    }

    @Test
    public void testSaveHar() throws IOException {
        // Create directories needed for har file.
        Path tmpPath = Paths.get(this.simPath, "tmp");
        Files.createDirectories(tmpPath);

        // Set har file path in simulation.
        Path expectedHarPath = Paths.get(this.simPath, "tmp", "recording.har");
        this.seleniumSimulation.setHarPath(expectedHarPath.toAbsolutePath().toString());

        // Mock proxyService to return a har file.
        when(this.standaloneBmpServiceMock.endHar()).thenReturn("{}");
        this.seleniumSimulation.proxy = this.standaloneBmpServiceMock;

        // Method under test.
        Path actualHarPath = this.seleniumSimulation.saveHar();

        assertThat(actualHarPath).isEqualTo(expectedHarPath);
        assertThat(expectedHarPath).hasContent("{}");
    }

    @Test
    public void testSaveHarWithoutProxy() {
        Path harPath = this.seleniumSimulation.saveHar();
        assertThat(harPath).isNull();
    }

    @Test
    public void testTakeScreenshot() {
        this.seleniumSimulation.driver = this.remoteWebDriverMock;
        this.seleniumSimulation.setScreenshotsPath(Paths.get(this.simPath, "tmp", "screenshots").toAbsolutePath().toString());

        Path screenshotPath = Paths.get(this.simPath, "tmp", "screenshots", "screenshot.png");

        // Mock webDriver to return the "new" screenshot at a temp path.
        when(((TakesScreenshot)this.remoteWebDriverMock).getScreenshotAs(any(OutputType.class))).then(invocation -> {
            File file = Paths.get(this.simPath, "temp-new-screenshot.png").toFile();
            FileWriter newFw = new FileWriter(file);
            newFw.write("this is a new screenshot");
            newFw.flush();
            return file;
        });

        Path actualScreenshotPath = this.seleniumSimulation.takeScreenshot("screenshot.png");

        assertThat(actualScreenshotPath).isEqualTo(screenshotPath);
        assertThat(screenshotPath).hasContent("this is a new screenshot");
        assertThat(Paths.get(this.simPath, "temp-new-screenshot.png")).doesNotExist();
    }

    @Test
    public void testTakeScreenshotWhenFileExists() throws IOException {
        this.seleniumSimulation.driver = this.remoteWebDriverMock;
        this.seleniumSimulation.setScreenshotsPath(Paths.get(this.simPath, "tmp", "screenshots").toAbsolutePath().toString());

        Path screenshotPath = Paths.get(this.simPath, "tmp", "screenshots", "screenshot.png");

        // Write an "old" screenshot file to test deleting the old file.
        Files.createDirectories(Paths.get(this.simPath, "tmp", "screenshots"));
        FileWriter oldFw = new FileWriter(screenshotPath.toFile());
        oldFw.write("this is an old screenshot");
        oldFw.flush();

        // Mock webDriver to return the "new" screenshot at a temp path.
        when(((TakesScreenshot)this.remoteWebDriverMock).getScreenshotAs(any(OutputType.class))).then(invocation -> {
            File file = Paths.get(this.simPath, "temp-new-screenshot.png").toFile();
            FileWriter newFw = new FileWriter(file);
            newFw.write("this is a new screenshot");
            newFw.flush();
            return file;
        });

        Path actualScreenshotPath = this.seleniumSimulation.takeScreenshot("screenshot.png");

        assertThat(actualScreenshotPath).isEqualTo(screenshotPath);
        assertThat(screenshotPath).hasContent("this is a new screenshot");
        assertThat(Paths.get(this.simPath, "temp-new-screenshot.png")).doesNotExist();
    }

    @Test
    public void testTakeScreenshotWithoutDriver() {
        this.seleniumSimulation.setScreenshotsPath(Paths.get(this.simPath, "tmp", "screenshots").toAbsolutePath().toString());
        Path path = this.seleniumSimulation.takeScreenshot("screenshot.png");
        assertThat(path).isNull();
    }

    @Test
    public void testCreateBmpService_Embedded() {
        // Mock createEmbeddedBmpService().
        doReturn(this.embeddedBmpServiceMock).when(this.seleniumSimulation).createEmbeddedBmpService();

        this.seleniumSimulation.useEmbeddedProxy = true;

        BrowserMobProxyService bmpService = this.seleniumSimulation.createBmpService();
        assertThat(bmpService).isEqualTo(this.embeddedBmpServiceMock);

        verify(this.seleniumSimulation).createEmbeddedBmpService();
        verify(this.embeddedBmpServiceMock).setTrustAllServers(true);
        verify(this.embeddedBmpServiceMock).setHarCaptureTypes(CaptureType.REQUEST_CONTENT, CaptureType.RESPONSE_CONTENT);
        verify(this.embeddedBmpServiceMock).start();
    }

    @Test
    public void testCreateBmpService_Standalone() {
        // Mock createStandaloneBmpService().
        doReturn(this.standaloneBmpServiceMock).when(this.seleniumSimulation).createStandaloneBmpService(anyString());

        this.seleniumSimulation.useEmbeddedProxy = false;
        this.seleniumSimulation.proxyUrl = "http://path-to-proxy";

        BrowserMobProxyService bmpService = this.seleniumSimulation.createBmpService();
        assertThat(bmpService).isEqualTo(this.standaloneBmpServiceMock);

        verify(this.seleniumSimulation).createStandaloneBmpService("http://path-to-proxy");
        verify(this.standaloneBmpServiceMock).setTrustAllServers(true);
        verify(this.standaloneBmpServiceMock).setHarCaptureTypes(CaptureType.REQUEST_CONTENT, CaptureType.RESPONSE_CONTENT);
        verify(this.standaloneBmpServiceMock).start();
    }

    @Test
    public void testCreateEmbeddedBmpService() {
        assertThat(this.seleniumSimulation.createEmbeddedBmpService()).isInstanceOf(EmbeddedBrowserMobProxyService.class);
    }

    @Test
    public void testCreateStandaloneBmpService() {
        Assertions.assertThat(this.seleniumSimulation.createStandaloneBmpService("http://path-to-proxy")).isInstanceOf(StandaloneBrowserMobProxyService.class);
    }

    @Test
    public void testCreateWebDriver_Local() {
        // Mock getCapabilities().
        DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
        doReturn(desiredCapabilities).when(this.seleniumSimulation).getCapabilities(anyString());

        // Mock createLocalWebDriver() and implicitlyWait().
        doReturn(this.chromeDriverMock).when(this.seleniumSimulation).createLocalWebDriver(anyString(), any());
        doNothing().when(this.seleniumSimulation).implicitlyWait(any());

        this.seleniumSimulation.useLocalDriver = true;

        WebDriver webDriver = this.seleniumSimulation.createWebDriver(BrowserName.CHROME);
        assertThat(webDriver).isEqualTo(this.chromeDriverMock);

        verify(this.seleniumSimulation).silentVerboseLogging();
        verify(this.seleniumSimulation).createLocalWebDriver(BrowserName.CHROME, desiredCapabilities);
        verify(this.seleniumSimulation).implicitlyWait(this.chromeDriverMock);
    }

    @Test
    public void testCreateWebDriver_Remote() {
        // Mock getCapabilities().
        DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
        doReturn(desiredCapabilities).when(this.seleniumSimulation).getCapabilities(anyString());

        // Mock createRemoteWebDriver() and implicitlyWait().
        doReturn(this.remoteWebDriverMock).when(this.seleniumSimulation).createRemoteWebDriver(any());
        doNothing().when(this.seleniumSimulation).implicitlyWait(any());

        this.seleniumSimulation.useLocalDriver = false;

        WebDriver webDriver = this.seleniumSimulation.createWebDriver(BrowserName.CHROME);
        assertThat(webDriver).isEqualTo(this.remoteWebDriverMock);

        verify(this.seleniumSimulation).silentVerboseLogging();
        verify(this.seleniumSimulation).createRemoteWebDriver(desiredCapabilities);
        verify(this.seleniumSimulation).implicitlyWait(this.remoteWebDriverMock);
    }

    @Test
    public void testGetChromeCapabilities() {
        Proxy proxy = new Proxy();
        when(this.standaloneBmpServiceMock.getPort()).thenReturn(8080);
        when(this.standaloneBmpServiceMock.createSeleniumProxy(anyInt())).thenReturn(proxy);
        this.seleniumSimulation.proxy = this.standaloneBmpServiceMock;

        DesiredCapabilities cp = this.seleniumSimulation.getCapabilities(BrowserName.CHROME);

        assertThat(cp.getCapability(CapabilityType.ACCEPT_INSECURE_CERTS)).isEqualTo(true);
        assertThat(cp.getCapability(CapabilityType.PROXY)).isEqualTo(proxy);
        assertThat(cp.getBrowserName()).isEqualTo(BrowserType.CHROME);

        verify(this.standaloneBmpServiceMock).createSeleniumProxy(8080);
    }

    @Test
    public void testGetFirefoxCapabilities() {
        Proxy proxy = new Proxy();
        when(this.standaloneBmpServiceMock.getPort()).thenReturn(8080);
        when(this.standaloneBmpServiceMock.createSeleniumProxy(anyInt())).thenReturn(proxy);
        this.seleniumSimulation.proxy = this.standaloneBmpServiceMock;

        DesiredCapabilities cp = this.seleniumSimulation.getCapabilities(BrowserName.FIREFOX);

        assertThat(cp.getCapability(CapabilityType.ACCEPT_INSECURE_CERTS)).isEqualTo(true);
        assertThat(cp.getCapability(CapabilityType.PROXY)).isEqualTo(proxy);
        assertThat(cp.getBrowserName()).isEqualTo(BrowserType.FIREFOX);

        verify(this.standaloneBmpServiceMock).createSeleniumProxy(8080);
    }

    @Test
    public void testFailToGetCapabilitiesForUnsupportedBrowser() {
        Proxy proxy = new Proxy();
        when(this.standaloneBmpServiceMock.getPort()).thenReturn(8080);
        when(this.standaloneBmpServiceMock.createSeleniumProxy(anyInt())).thenReturn(proxy);
        this.seleniumSimulation.proxy = this.standaloneBmpServiceMock;

        UnsupportedBrowserException ex = assertThrows(UnsupportedBrowserException.class, () -> this.seleniumSimulation.getCapabilities("unsupported-browser"));
        assertThat(ex).hasMessageContaining("Unsupported browserName: unsupported-browser");
    }

    @Test
    public void testCreateLocalWebDriver_Chrome() {
        // Mock createChromeDriver().
        doReturn(this.chromeDriverMock).when(this.seleniumSimulation).createChromeDriver(any());

        DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
        desiredCapabilities.setCapability("key", "value");

        // Method under test.
        WebDriver webDriver = this.seleniumSimulation.createLocalWebDriver(BrowserName.CHROME, desiredCapabilities);

        assertThat(webDriver).isEqualTo(this.chromeDriverMock);
        assertThat(System.getProperty(SeleniumPropertyKeys.CHROME_DRIVER)).isNull();

        // Verify chromeOptions passed to createChromeDriver().
        ArgumentCaptor<ChromeOptions> chromeOptions = ArgumentCaptor.forClass(ChromeOptions.class);
        verify(this.seleniumSimulation).createChromeDriver(chromeOptions.capture());
        assertThat(chromeOptions.getValue().getCapability("key")).isEqualTo("value");
    }

    @Test
    public void testCreateLocalWebDriverWithDriverPath_Chrome() {
        // Mock createChromeDriver().
        doReturn(this.chromeDriverMock).when(this.seleniumSimulation).createChromeDriver(any());

        DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
        desiredCapabilities.setCapability("key", "value");

        // Set driver path.
        this.seleniumSimulation.driverPath = "path-to-chrome-driver";

        // Method under test.
        WebDriver webDriver = this.seleniumSimulation.createLocalWebDriver(BrowserName.CHROME, desiredCapabilities);

        assertThat(webDriver).isEqualTo(this.chromeDriverMock);
        assertThat(System.getProperty(SeleniumPropertyKeys.CHROME_DRIVER)).isEqualTo("path-to-chrome-driver");

        // Verify chromeOptions passed to createChromeDriver().
        ArgumentCaptor<ChromeOptions> chromeOptions = ArgumentCaptor.forClass(ChromeOptions.class);
        verify(this.seleniumSimulation).createChromeDriver(chromeOptions.capture());
        assertThat(chromeOptions.getValue().getCapability("key")).isEqualTo("value");
    }

    @Test
    public void testCreateLocalWebDriver_Firefox() {
        // Mock createChromeDriver().
        doReturn(this.firefoxDriverMock).when(this.seleniumSimulation).createFirefoxDriver(any());

        DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
        desiredCapabilities.setCapability("key", "value");

        // Method under test.
        WebDriver webDriver = this.seleniumSimulation.createLocalWebDriver(BrowserName.FIREFOX, desiredCapabilities);

        assertThat(webDriver).isEqualTo(this.firefoxDriverMock);
        assertThat(System.getProperty(SeleniumPropertyKeys.FIREFOX_DRIVER)).isNull();

        // Verify chromeOptions passed to createChromeDriver().
        ArgumentCaptor<FirefoxOptions> firefoxOptions = ArgumentCaptor.forClass(FirefoxOptions.class);
        verify(this.seleniumSimulation).createFirefoxDriver(firefoxOptions.capture());
        assertThat(firefoxOptions.getValue().getCapability("key")).isEqualTo("value");
    }

    @Test
    public void testCreateLocalWebDriverWithDriverPath_Firefox() {
        // Mock createChromeDriver().
        doReturn(this.firefoxDriverMock).when(this.seleniumSimulation).createFirefoxDriver(any());

        DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
        desiredCapabilities.setCapability("key", "value");

        this.seleniumSimulation.driverPath = "path-to-firefox-driver";

        // Method under test.
        WebDriver webDriver = this.seleniumSimulation.createLocalWebDriver(BrowserName.FIREFOX, desiredCapabilities);

        assertThat(webDriver).isEqualTo(this.firefoxDriverMock);
        assertThat(System.getProperty(SeleniumPropertyKeys.FIREFOX_DRIVER)).isEqualTo("path-to-firefox-driver");

        // Verify chromeOptions passed to createChromeDriver().
        ArgumentCaptor<FirefoxOptions> firefoxOptions = ArgumentCaptor.forClass(FirefoxOptions.class);
        verify(this.seleniumSimulation).createFirefoxDriver(firefoxOptions.capture());
        assertThat(firefoxOptions.getValue().getCapability("key")).isEqualTo("value");
    }

    @Test
    public void testFailToCreateLocalWebDriverForUnsupportedBrowser() {
        UnsupportedBrowserException ex = assertThrows(UnsupportedBrowserException.class, () -> this.seleniumSimulation.createLocalWebDriver("unsupported-browser", null));
        assertThat(ex).hasMessageContaining("Unsupported browserName: unsupported-browser");
    }

    @Test
    public void testSilentVerboseLogging() {
        this.seleniumSimulation.silentVerboseLogging();
        assertThat(System.getProperty(SeleniumPropertyKeys.CHROME_SILENT_OUTPUT)).isEqualTo("true");
        assertThat(java.util.logging.Logger.getLogger(LoggerName.SELENIUM).getLevel()).isEqualTo(Level.WARNING);
    }

    class BaseSeleniumSimulation extends SeleniumSimulation {

        public String getSimulationPath() {
            return simulationPath;
        }

        /**
         * Simulation path is protected in AbstractSimulation, so public setter needed.
         * @param simulationPath
         */
        public void setSimulationPath(String simulationPath) {
            this.simulationPath = simulationPath;
        }

        public String getLogPath() {
            return logPath;
        }

        public String getHarPath() {
            return harPath;
        }

        public void setHarPath(String harPath) {
            this.harPath = harPath;
        }

        public String getTmpPath() {
            return tmpPath;
        }

        public String getActionsPath() {
            return actionsPath;
        }

        public String getScreenshotsPath() {
            return screenshotsPath;
        }

        public void setScreenshotsPath(String path) {
            this.screenshotsPath = path;
        }

    }

}