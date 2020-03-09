package dev.testment.core.browsermob.services;

import dev.testment.core.browsermob.dtos.StartProxyResponse;
import dev.testment.core.util.OkHttpUtil;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.testment.core.util.exceptions.HttpStatusCodeException;
import net.lightbody.bmp.proxy.CaptureType;
import okhttp3.*;
import org.openqa.selenium.Proxy;

import java.util.HashSet;
import java.util.Set;

public class StandaloneBrowserMobProxyService implements BrowserMobProxyService {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private HttpUrl serverUrl;
    private final OkHttpClient httpClient;

    private boolean trustAllServers = false;
    private Set<CaptureType> captureTypes = new HashSet<>();
    private int currentPort;
    private int usePort = 0;

    static {
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    public StandaloneBrowserMobProxyService(String browserMobUrl) {
        this(browserMobUrl, new OkHttpClient());
    }

    public StandaloneBrowserMobProxyService(String browserMobUrl, OkHttpClient httpClient) {
        this.serverUrl = HttpUrl.get(browserMobUrl);
        this.httpClient = httpClient;
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {
        if(this.currentPort > 0) {
            HttpUrl url = this.serverUrl.newBuilder()
                    .addPathSegment("proxy")
                    .addPathSegment(this.currentPort + "")
                    .build();

            Request request = new Request.Builder()
                    .url(url)
                    .delete()
                    .build();
            OkHttpUtil.executeRequest(this.httpClient, request);
        }
    }

    @Override
    public Proxy createSeleniumProxy(int port) {
        this.currentPort = port;
        Proxy proxy = new Proxy();
        proxy.setProxyType(Proxy.ProxyType.MANUAL);
        String proxyUrl = this.serverUrl.host() + ":" + port;
        proxy.setHttpProxy(proxyUrl);
        proxy.setSslProxy(proxyUrl);
        return proxy;
    }

    @Override
    public int getPort() {
        HttpUrl.Builder builder = this.serverUrl.newBuilder().addPathSegment("proxy");
        if(this.usePort > 0) {
            builder.addQueryParameter("port", this.usePort + "");
        }

        builder.addQueryParameter("trustAllServers", this.trustAllServers + "")
                .build();

        Request request = new Request.Builder()
                .url(builder.build())
                .post(OkHttpUtil.createEmptyJsonBody())
                .build();

        try (Response response = executeRequest(request)) {
            this.currentPort = OkHttpUtil.parseResponse(this.objectMapper, response, StartProxyResponse.class).getPort();
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
        HttpUrl url = this.serverUrl.newBuilder()
                .addPathSegment("proxy")
                .addPathSegment(this.currentPort + "")
                .addPathSegment("har")
                .addQueryParameter("captureBinaryContent", this.capturingAny(CaptureType.REQUEST_BINARY_CONTENT, CaptureType.RESPONSE_BINARY_CONTENT) + "")
                .addQueryParameter("captureContent", this.capturingAny(CaptureType.REQUEST_CONTENT, CaptureType.RESPONSE_CONTENT) + "")
                .addQueryParameter("captureCookies", this.capturingAny(CaptureType.REQUEST_COOKIES, CaptureType.RESPONSE_COOKIES) + "")
                .addQueryParameter("captureHeaders", this.capturingAny(CaptureType.REQUEST_HEADERS, CaptureType.RESPONSE_HEADERS) + "")
                .build();

        Request request = new Request.Builder()
                .url(url)
                .put(OkHttpUtil.createEmptyJsonBody())
                .build();

        executeRequest(request);
    }

    @Override
    public String endHar() {
        HttpUrl url = this.serverUrl.newBuilder()
                .addPathSegment("proxy")
                .addPathSegment(this.currentPort + "")
                .addPathSegment("har")
                .build();

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try(Response response = executeRequest(request)) {
            return OkHttpUtil.parseResponseAsString(response);
        }
    }

    public void usePort(int usePort) {
        this.usePort = usePort;
    }

    private Response executeRequest(Request request) {
        Response response = OkHttpUtil.executeRequest(this.httpClient, request);
        if(!response.isSuccessful()) {
            throw new HttpStatusCodeException("Received a non-successful response from BrowserMob Proxy Server: " + response);
        }
        return response;
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
