package dev.testment.core.selenium.simulation;

import dev.testment.core.util.Validation;
import dev.testment.core.selenium.SeleniumPropertyKeys;
import dev.testment.core.simulation.BrowserName;
import dev.testment.core.browsermob.services.BrowserMobProxyService;
import dev.testment.core.browsermob.services.EmbeddedBrowserMobProxyService;
import dev.testment.core.browsermob.services.StandaloneBrowserMobProxyService;
import dev.testment.core.logger.LoggerName;
import dev.testment.core.simulation.AbstractSimulation;
import dev.testment.core.simulation.exceptions.UnsupportedBrowserException;
import dev.testment.core.simulation.param.Param;
import net.lightbody.bmp.proxy.CaptureType;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class SeleniumSimulation extends AbstractSimulation {

    private static final Logger logger = Logger.getLogger(SeleniumSimulation.class.getName());

    @Param(name = "browser", defaultValue = BrowserName.CHROME, examples = { BrowserName.CHROME, BrowserName.FIREFOX })
    String browserName;

    @Param(name = "hub-url", defaultValue = "http://localhost:4444")
    String hubUrl;

    @Param(name = "local-driver")
    boolean useLocalDriver;

    @Param(name = "driver-path", required = false)
    String driverPath;

    @Param(name = "implicit-wait", defaultValue = "2", description = "Web driver implicit wait in seconds")
    int implicitWait;

    @Param(name = "proxy-url", defaultValue = "http://localhost:8080")
    String proxyUrl;

    @Param(name = "embedded-proxy")
    boolean useEmbeddedProxy;

    @Param(name = "headless")
    boolean headlessMode;

    protected WebDriver driver;
    BrowserMobProxyService proxy;

    @Override
    public void setUp() {
        super.setUp();
        proxy = createBmpService();
        driver = createWebDriver(browserName);
        proxy.newHar();
    }

    @Override
    public void tearDown() {
        super.tearDown();
        if(driver != null) {
            try {
                driver.quit();
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        if(proxy != null) {
            try {
                proxy.stop();
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Path saveHar() {
        if(proxy != null) {
            String str = proxy.endHar();
            try {
                FileWriter fw = new FileWriter(harPath);
                fw.write(str);
                fw.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return Paths.get(harPath);
        }
        return null;
    }

    @Override
    public Path takeScreenshot(String name) {
        createDirectories(screenshotsPath);
        if(driver != null && TakesScreenshot.class.isAssignableFrom(driver.getClass())) {
            TakesScreenshot takesScreenshot = (TakesScreenshot)driver;
            File screenshotFile = takesScreenshot.getScreenshotAs(OutputType.FILE);
            Path screenshotPath = Paths.get(screenshotsPath, name);
            try {
                Files.deleteIfExists(screenshotPath);
                return Files.move(screenshotFile.toPath(), screenshotPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    BrowserMobProxyService createBmpService() {
        BrowserMobProxyService proxyService;
        if(this.useEmbeddedProxy) {
            proxyService = createEmbeddedBmpService();
        } else {
            proxyService = createStandaloneBmpService(this.proxyUrl);
        }
        proxyService.setTrustAllServers(true);
        proxyService.setHarCaptureTypes(CaptureType.REQUEST_CONTENT, CaptureType.RESPONSE_CONTENT);
        proxyService.start();
        return proxyService;
    }

    EmbeddedBrowserMobProxyService createEmbeddedBmpService() {
        return new EmbeddedBrowserMobProxyService();
    }

    StandaloneBrowserMobProxyService createStandaloneBmpService(String proxyUrl) {
        return new StandaloneBrowserMobProxyService(proxyUrl);
    }

    WebDriver createWebDriver(String browserName) {
        Capabilities cp = getCapabilities(browserName);
        WebDriver d;

        this.silentVerboseLogging();
        if(this.useLocalDriver) {
            d = createLocalWebDriver(browserName, cp);
        } else {
            d = createRemoteWebDriver(cp);
        }

        implicitlyWait(d);
        return d;
    }

    Capabilities getCapabilities(String browserName) {
        Proxy proxy = this.proxy.createSeleniumProxy(this.proxy.getPort());

        DesiredCapabilities cp = new DesiredCapabilities();
        cp.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);
        cp.setCapability(CapabilityType.PROXY, proxy);

        switch(browserName) {
            case BrowserName.CHROME:
                ChromeOptions chromeOptions = new ChromeOptions();
                if(this.headlessMode) {
                    chromeOptions.addArguments("--no-sandbox");
                    chromeOptions.addArguments("--disable-gpu");
                    chromeOptions.addArguments("--disable-extensions");
                    chromeOptions.addArguments("--headless");
                    chromeOptions.addArguments("--remote-debugging-port=9222");
                    chromeOptions.addArguments("--start-maximized");
                }
                chromeOptions.addArguments("--ignore-certificate-errors");
                chromeOptions.addArguments("--disable-dev-shm-usage");
                return chromeOptions.merge(cp);
            case BrowserName.FIREFOX:
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                return firefoxOptions.merge(cp);
            default:
                throw new UnsupportedBrowserException("Unsupported browserName: " + browserName);
        }
    }

    RemoteWebDriver createRemoteWebDriver(Capabilities cp) {
        return new RemoteWebDriver(toURL(this.hubUrl + "/wd/hub") , cp);
    }

    WebDriver createLocalWebDriver(String browserName, Capabilities cp) {
        switch(browserName) {
            case BrowserName.CHROME:
                if(Validation.isNotEmpty(driverPath))
                    System.setProperty(SeleniumPropertyKeys.CHROME_DRIVER, driverPath);
                return createChromeDriver((ChromeOptions)cp);
            case BrowserName.FIREFOX:
                if(Validation.isNotEmpty(driverPath))
                    System.setProperty(SeleniumPropertyKeys.FIREFOX_DRIVER, driverPath);
                return createFirefoxDriver((FirefoxOptions)cp);
            default:
                throw new UnsupportedBrowserException("Unsupported browserName: " + browserName);
        }
    }

    ChromeDriver createChromeDriver(ChromeOptions options) {
        return new ChromeDriver(options);
    }

    FirefoxDriver createFirefoxDriver(FirefoxOptions options) {
        return new FirefoxDriver(options);
    }

    void implicitlyWait(WebDriver d) {
        d.manage().timeouts().implicitlyWait(this.implicitWait, TimeUnit.SECONDS);
    }

    void silentVerboseLogging() {
        // Turn off verbose logging.
        System.setProperty(SeleniumPropertyKeys.CHROME_SILENT_OUTPUT, "true");
        java.util.logging.Logger.getLogger(LoggerName.SELENIUM).setLevel(Level.WARNING);
    }

    private URL toURL(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

}
