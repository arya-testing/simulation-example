package com.arya.browsermob;

import net.lightbody.bmp.proxy.CaptureType;
import org.openqa.selenium.Proxy;

public interface BrowserMobProxyService {
    void start();
    void stop();
    Proxy createSeleniumProxy(int port);
    int getPort();
    void setTrustAllServers(boolean trustAllServers);
    void setHarCaptureTypes(CaptureType... captureTypes);
    void newHar();
    String endHar();
}
