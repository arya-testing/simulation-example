package com.arya.browsermob;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.lightbody.bmp.proxy.CaptureType;
import okhttp3.*;
import org.openqa.selenium.Proxy;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class StandaloneBrowserMobProxyService implements BrowserMobProxyService {

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final String browserMobHost;
    private final OkHttpClient httpClient;

    private boolean trustAllServers = false;
    private Set<CaptureType> captureTypes = new HashSet<>();
    private int currentPort;
    private int usePort;

    static {
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    public StandaloneBrowserMobProxyService(String browserMobHost) {
        this.browserMobHost = browserMobHost;
        this.httpClient = new OkHttpClient();
    }

    public StandaloneBrowserMobProxyService(String browserMobHost, OkHttpClient httpClient) {
        this.browserMobHost = browserMobHost;
        this.httpClient = httpClient;
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {
        if(this.currentPort > 0) {
            Request request = new Request.Builder()
                    .url(this.browserMobHost + "/proxy/" + this.currentPort)
                    .delete()
                    .build();
            execute(request);
        }
    }

    @Override
    public Proxy createSeleniumProxy(int port) {
        this.currentPort = port;
        Proxy proxy = new Proxy();
        proxy.setProxyType(Proxy.ProxyType.MANUAL);
        try {
            URL url = new URL(this.browserMobHost);
            String proxyUrl = url.getHost() + ":" + port;
            proxy.setHttpProxy(proxyUrl);
            proxy.setSslProxy(proxyUrl);
            return proxy;
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getPort() {
        StartProxyRequest startProxyRequest = new StartProxyRequest();
        startProxyRequest.setTrustAllServers(this.trustAllServers);

        String portQueryParam = this.usePort > 0 ? "port=" + this.usePort : "";
        Request request = new Request.Builder()
                .url(this.browserMobHost + "/proxy?" + portQueryParam)
                .post(this.createBody(startProxyRequest))
                .build();

        try (Response response = execute(request)) {
            this.currentPort = parse(response, StartProxyResponse.class).getPort();
            return this.currentPort;
        }
    }

    @Override
    public void setTrustAllServers(boolean trustAllServers) {
        this.trustAllServers = trustAllServers;
    }

    @Override
    public void setHarCaptureTypes(CaptureType... captureTypes) {
        this.captureTypes = new HashSet<>();
        for(CaptureType captureType : captureTypes) {
            this.captureTypes.add(captureType);
        }
    }

    @Override
    public void newHar() {
        CreateHarRequest createHarRequest = new CreateHarRequest();
        createHarRequest.setCaptureBinaryContent(this.capturingAny(CaptureType.REQUEST_BINARY_CONTENT, CaptureType.RESPONSE_BINARY_CONTENT));
        createHarRequest.setCaptureContent(this.capturingAny(CaptureType.REQUEST_CONTENT, CaptureType.RESPONSE_CONTENT));
        createHarRequest.setCaptureCookies(this.capturingAny(CaptureType.REQUEST_COOKIES, CaptureType.RESPONSE_COOKIES));
        createHarRequest.setCaptureHeaders(this.capturingAny(CaptureType.REQUEST_HEADERS, CaptureType.RESPONSE_HEADERS));

        Request request = new Request.Builder()
                .url(this.browserMobHost + "/proxy/" + this.currentPort + "/har")
                .put(this.createBody(createHarRequest))
                .build();

        execute(request);
    }

    @Override
    public String endHar() {
        Request request = new Request.Builder()
                .url(this.browserMobHost + "/proxy/" + this.currentPort + "/har")
                .get()
                .build();

        try(Response response = execute(request)) {
            try {
                return response.body().string();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public StandaloneBrowserMobProxyService usePort(int usePort) {
        this.usePort = usePort;
        return this;
    }

    private RequestBody createBody(Object object) {
        try {
            return RequestBody.create(this.objectMapper.writeValueAsString(object), JSON);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private Response execute(Request request) {
        try {
            return this.httpClient.newCall(request).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private <T> T parse(Response response, Class<T> clazz) {
        try {
            String body = response.body().string();
            return this.objectMapper.readValue(body, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean capturingAll(CaptureType... captureTypes) {
        for(CaptureType type : captureTypes) {
            if(this.captureTypes.contains(type)) {
                return false;
            }
        }
        return true;
    }

    private boolean capturingAny(CaptureType... captureTypes) {
        for(CaptureType type : captureTypes) {
            if(this.captureTypes.contains(type)) {
                return true;
            }
        }
        return false;
    }

}
