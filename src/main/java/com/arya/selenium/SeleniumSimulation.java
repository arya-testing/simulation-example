package com.arya.selenium;

import com.arya.browsermob.BrowserMobProxyService;
import com.arya.browsermob.EmbeddedBrowserMobProxyService;
import com.arya.browsermob.StandaloneBrowserMobProxyService;
import com.arya.simulation.AbstractSimulation;
import com.arya.simulation.Param;
import com.arya.util.Validation;
import net.lightbody.bmp.proxy.CaptureType;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
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

    protected static final String BROWSER_CHROME = "chrome";
    protected static final String BROWSER_FIREFOX = "firefox";

    private static final Logger logger = Logger.getLogger(SeleniumSimulation.class.getName());

    @Param(name = "browser", defaultValue = BROWSER_CHROME, examples = { BROWSER_CHROME, BROWSER_FIREFOX })
    private String browserName;

    @Param(name = "hub-url", defaultValue = "http://localhost:4444")
    private String hubUrl;

    @Param(name = "local-driver")
    private boolean useLocalDriver;

    @Param(name = "driver-path", required = false)
    private String driverPath;

    @Param(name = "implicit-wait", defaultValue = "2", description = "Web driver implicit wait in seconds")
    private int implicitWait;

    @Param(name = "proxy-url", defaultValue = "http://localhost:8888")
    private String proxyUrl;

    @Param(name = "embedded-proxy")
    private boolean useEmbeddedProxy;

    protected WebDriver driver;
    protected BrowserMobProxyService proxy;

    @Override
    public void setUp() {
        super.setUp();
        proxy = createBrowserMobProxy();
        driver = createWebDriver(browserName);
        proxy.newHar();
    }

    @Override
    public void tearDown() {
        super.tearDown();
        if(driver != null) {
            driver.quit();
        }
        if(proxy != null) {
            proxy.stop();
        }
    }

    @Override
    public Path saveHar() {
        if(proxy != null) {
            String str = proxy.endHar();
            try {
                FileWriter fw = new FileWriter(harPath);
                fw.write(str);
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
                return Files.copy(screenshotFile.toPath(), screenshotPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    protected BrowserMobProxyService createBrowserMobProxy() {
        BrowserMobProxyService proxyService;
        if(this.useEmbeddedProxy) {
            proxyService = new EmbeddedBrowserMobProxyService();
        } else {
            proxyService = new StandaloneBrowserMobProxyService(this.proxyUrl);
        }
        proxyService.setTrustAllServers(true);
        proxyService.setHarCaptureTypes(CaptureType.REQUEST_CONTENT, CaptureType.RESPONSE_CONTENT);
        proxyService.start();
        return proxyService;
    }

    protected WebDriver createWebDriver(String browserName) {
        DesiredCapabilities cp = getCapabilities(browserName);
        WebDriver d;

        this.silentVerboseLogging();
        if(this.useLocalDriver) {
            d = createLocalWebDriver(browserName, cp);
        } else {
            d = createRemoteWebDriver(cp);
        }

        d.manage().timeouts().implicitlyWait(this.implicitWait, TimeUnit.SECONDS);
        return d;
    }

    protected DesiredCapabilities getCapabilities(String browserName) {
        Proxy proxy = this.proxy.createSeleniumProxy(this.proxy.getPort());

        DesiredCapabilities cp = new DesiredCapabilities();
        cp.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);
        cp.setCapability(CapabilityType.PROXY, proxy);

        switch(browserName) {
            case BROWSER_CHROME:
                ChromeOptions chromeOptions = new ChromeOptions();
//                chromeOptions.addArguments("--disable-gpu");
//                chromeOptions.addArguments("--no-sandbox");
                chromeOptions.addArguments("--ignore-certificate-errors");
                chromeOptions.addArguments("--disable-dev-shm-usage");
//                chromeOptions.addArguments("--headless");
                cp.merge(chromeOptions);
                break;
            case BROWSER_FIREFOX:
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                cp.merge(firefoxOptions);
                break;
            default:
                throw new IllegalArgumentException("Unsupported browserName: " + browserName);
        }

        return cp;
    }

    protected WebDriver createRemoteWebDriver(DesiredCapabilities cp) {
        return new RemoteWebDriver(toURL(this.hubUrl + "/wd/hub") , cp);
    }

    protected WebDriver createLocalWebDriver(String browserName, DesiredCapabilities cp) {
        switch(browserName) {
            case BROWSER_CHROME:
                if(Validation.isNotEmpty(driverPath))
                    System.setProperty("webdriver.chrome.driver", driverPath);
                return new ChromeDriver();
            case BROWSER_FIREFOX:
                return new FirefoxDriver();
            default:
                throw new IllegalArgumentException("Unsupported browserName: " + browserName);
        }
    }

    private void silentVerboseLogging() {
        // Turn off verbose logging.
        System.setProperty("webdriver.chrome.silentOutput", "true");
        java.util.logging.Logger.getLogger("org.openqa.selenium").setLevel(Level.WARNING);
    }

    private URL toURL(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

}
