package com.arya.browsermob;

import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.client.ClientUtil;
import net.lightbody.bmp.core.har.Har;
import net.lightbody.bmp.proxy.CaptureType;
import org.openqa.selenium.Proxy;

import java.io.IOException;
import java.io.StringWriter;

public class EmbeddedBrowserMobProxyService implements BrowserMobProxyService {

    private final BrowserMobProxyServer proxy;

    public EmbeddedBrowserMobProxyService() {
        this.proxy = new BrowserMobProxyServer();
    }

    public EmbeddedBrowserMobProxyService(BrowserMobProxyServer proxy) {
        this.proxy = proxy;
    }

    @Override
    public void start() {
        this.proxy.start();
    }

    @Override
    public void stop() {
        this.proxy.stop();
    }

    @Override
    public Proxy createSeleniumProxy(int port) {
        Proxy p = ClientUtil.createSeleniumProxy(this.proxy);
        p.setHttpProxy("localhost:" + port);
        p.setSslProxy("localhost:" + port);
        return p;
    }

    @Override
    public int getPort() {
        return this.proxy.getPort();
    }

    @Override
    public void setTrustAllServers(boolean trustAllServers) {
        this.proxy.setTrustAllServers(trustAllServers);
    }

    @Override
    public void setHarCaptureTypes(CaptureType... captureTypes) {
        this.proxy.setHarCaptureTypes(captureTypes);
    }

    @Override
    public void newHar() {
        this.proxy.newHar();
    }

    @Override
    public String endHar() {
        Har h = this.proxy.getHar();
        StringWriter sw = new StringWriter();

        try {
            h.writeTo(sw);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return sw.toString();
    }

    public BrowserMobProxyServer getProxy() {
        return proxy;
    }

}