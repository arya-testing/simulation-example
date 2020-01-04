package com.arya.selenium;

import com.arya.simulation.AbstractSimulation;
import com.arya.simulation.Param;
import com.arya.util.Validation;
import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.client.ClientUtil;
import net.lightbody.bmp.core.har.Har;
import net.lightbody.bmp.proxy.CaptureType;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

public abstract class SeleniumSimulation extends AbstractSimulation {

    protected static final String BROWSER_CHROME = "chrome";
    protected static final String BROWSER_FIREFOX = "firefox";

    @Param(value = "browser", defaultValue = BROWSER_CHROME, examples = { BROWSER_CHROME, BROWSER_FIREFOX })
    private String browserName;

    @Param(value = "driver", required = false)
    private String driverPath;

    // TODO Test this
    //@Param(value = "implicit-wait", description = "Web driver implicit wait in seconds")
    private int implicitWait = 2;

    protected WebDriver driver;
    protected BrowserMobProxy proxy;

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
            Har har = proxy.endHar();
            try {
                har.writeTo(new File(harPath));
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

    protected BrowserMobProxy createBrowserMobProxy() {
        BrowserMobProxy browserMobProxy = new BrowserMobProxyServer();
        browserMobProxy.setTrustAllServers(true);
        browserMobProxy.enableHarCaptureTypes(CaptureType.REQUEST_CONTENT, CaptureType.RESPONSE_CONTENT);
        browserMobProxy.start();
        browserMobProxy.getPort();
        return browserMobProxy;
    }

    protected WebDriver createWebDriver(String browserName) {
        WebDriver d;
        switch(browserName) {
            case BROWSER_CHROME:
                d = createChromeDriver(driverPath, proxy);
                break;
            case BROWSER_FIREFOX:
                throw new UnsupportedOperationException("Not implemented yet");
            default:
                throw new IllegalArgumentException("Unsupported browserName: " + browserName);
        }
        d.manage().timeouts().implicitlyWait(implicitWait, TimeUnit.SECONDS);
        return d;
    }

    protected ChromeDriver createChromeDriver(String driverPath, BrowserMobProxy browserMobProxy) {
        if(Validation.isNotEmpty(driverPath))
            System.setProperty("webdriver.chrome.driver", driverPath);

        Proxy seleniumProxy = ClientUtil.createSeleniumProxy(browserMobProxy);
        return new ChromeDriver(new ChromeOptions()
                .addArguments("--ignore-certificate-errors")
                .addArguments("--no-sandbox")
                .addArguments("--disable-dev-shm-usage")
                .setProxy(seleniumProxy));
    }

}
