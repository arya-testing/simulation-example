package dev.testment.core.browsermob;

import dev.testment.core.browsermob.services.StandaloneBrowserMobProxyService;
import net.lightbody.bmp.proxy.CaptureType;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.openqa.selenium.Proxy;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoInteractions;

@RunWith(MockitoJUnitRunner.class)
public class StandaloneBrowserMobProxyServiceTests {

    @Spy
    private OkHttpClient okHttpClientSpy;

    private StandaloneBrowserMobProxyService bmpService;
    private MockWebServer mockWebServer;
    private HttpUrl serverUrl;

    @Before
    public void setUp() throws IOException {
        this.mockWebServer = new MockWebServer();
        this.mockWebServer.start();
        this.serverUrl = this.mockWebServer.url("/");
        this.bmpService = new StandaloneBrowserMobProxyService(this.serverUrl.toString(), this.okHttpClientSpy);
    }

    @After
    public void tearDown() throws IOException {
        this.mockWebServer.shutdown();
    }

    @Test
    public void testStart() {
        this.bmpService.start();
        verifyNoInteractions(this.okHttpClientSpy);
    }

    @Test
    public void testStop() {
        this.bmpService.stop();
        verifyNoInteractions(this.okHttpClientSpy);
    }

    @Test
    public void testStopRunningProxy() throws InterruptedException {
        this.mockWebServer.enqueue(new MockResponse().setBody("{\"port\":8081}"));
        this.bmpService.getPort();
        this.mockWebServer.takeRequest();

        this.mockWebServer.enqueue(new MockResponse());
        this.bmpService.stop();

        RecordedRequest request = this.mockWebServer.takeRequest();
        assertThat(request.getRequestUrl().host()).isEqualTo(serverUrl.host());
        assertThat(request.getRequestUrl().port()).isEqualTo(serverUrl.port());
        assertThat(request.getMethod()).isEqualToIgnoringCase("DELETE");
        assertThat(request.getPath()).isEqualTo("/proxy/8081");
    }

    @Test
    public void testCreateSeleniumProxy() {
        Proxy proxy = this.bmpService.createSeleniumProxy(8081);
        assertThat(proxy).isNotNull();
        assertThat(proxy.getProxyType()).isEqualTo(Proxy.ProxyType.MANUAL);
        assertThat(proxy.getHttpProxy()).isEqualTo(serverUrl.host() + ":8081");
        assertThat(proxy.getSslProxy()).isEqualTo(serverUrl.host() + ":8081");
    }

    @Test
    public void testGetPort() throws InterruptedException {
        this.mockWebServer.enqueue(new MockResponse().setBody("{\"port\":9091}"));

        this.bmpService.usePort(9091);
        this.bmpService.setTrustAllServers(true);

        int port = this.bmpService.getPort();

        assertThat(port).isEqualTo(9091);

        RecordedRequest request = this.mockWebServer.takeRequest();
        assertThat(request.getRequestUrl().host()).isEqualTo(serverUrl.host());
        assertThat(request.getRequestUrl().port()).isEqualTo(serverUrl.port());
        assertThat(request.getMethod()).isEqualToIgnoringCase("POST");
        assertThat(request.getPath()).isEqualTo("/proxy?port=9091&trustAllServers=true");
    }

    @Test
    public void testGetPortWithoutUsePort() throws InterruptedException {
        this.mockWebServer.enqueue(new MockResponse().setBody("{\"port\":9091}"));
        this.bmpService.setTrustAllServers(true);

        int port = this.bmpService.getPort();
        assertThat(port).isEqualTo(9091);

        RecordedRequest request = this.mockWebServer.takeRequest();
        assertThat(request.getRequestUrl().host()).isEqualTo(serverUrl.host());
        assertThat(request.getRequestUrl().port()).isEqualTo(serverUrl.port());
        assertThat(request.getMethod()).isEqualToIgnoringCase("POST");
        assertThat(request.getPath()).isEqualTo("/proxy?trustAllServers=true");
    }

    @Test
    public void testNewHar() throws InterruptedException {
        this.mockWebServer.enqueue(new MockResponse().setBody("{\"port\":8081}"));
        this.bmpService.getPort();
        this.mockWebServer.takeRequest();

        this.mockWebServer.enqueue(new MockResponse());
        this.bmpService.newHar();

        RecordedRequest request = this.mockWebServer.takeRequest();
        assertThat(request.getRequestUrl().host()).isEqualTo(serverUrl.host());
        assertThat(request.getRequestUrl().port()).isEqualTo(serverUrl.port());
        assertThat(request.getMethod()).isEqualToIgnoringCase("PUT");
        assertThat(request.getPath()).isEqualTo("/proxy/8081/har?captureBinaryContent=false&captureContent=false&captureCookies=false&captureHeaders=false");
    }

    @Test
    public void testNewHarWithRequestCaptureTypes() throws InterruptedException {
        this.mockWebServer.enqueue(new MockResponse().setBody("{\"port\":8081}"));
        this.bmpService.getPort();
        this.mockWebServer.takeRequest();

        this.mockWebServer.enqueue(new MockResponse());
        this.bmpService.setHarCaptureTypes(CaptureType.REQUEST_BINARY_CONTENT, CaptureType.REQUEST_CONTENT, CaptureType.REQUEST_COOKIES, CaptureType.REQUEST_HEADERS);
        this.bmpService.newHar();

        RecordedRequest request = this.mockWebServer.takeRequest();
        assertThat(request.getRequestUrl().host()).isEqualTo(serverUrl.host());
        assertThat(request.getRequestUrl().port()).isEqualTo(serverUrl.port());
        assertThat(request.getMethod()).isEqualToIgnoringCase("PUT");
        assertThat(request.getPath()).isEqualTo("/proxy/8081/har?captureBinaryContent=true&captureContent=true&captureCookies=true&captureHeaders=true");
    }

    @Test
    public void testNewHarWithResponseCaptureTypes() throws InterruptedException {
        this.mockWebServer.enqueue(new MockResponse().setBody("{\"port\":8081}"));
        this.bmpService.getPort();
        this.mockWebServer.takeRequest();

        this.mockWebServer.enqueue(new MockResponse());
        this.bmpService.setHarCaptureTypes(CaptureType.RESPONSE_BINARY_CONTENT, CaptureType.RESPONSE_CONTENT, CaptureType.RESPONSE_COOKIES, CaptureType.RESPONSE_HEADERS);
        this.bmpService.newHar();

        RecordedRequest request = this.mockWebServer.takeRequest();
        assertThat(request.getRequestUrl().host()).isEqualTo(serverUrl.host());
        assertThat(request.getRequestUrl().port()).isEqualTo(serverUrl.port());
        assertThat(request.getMethod()).isEqualToIgnoringCase("PUT");
        assertThat(request.getPath()).isEqualTo("/proxy/8081/har?captureBinaryContent=true&captureContent=true&captureCookies=true&captureHeaders=true");
    }

    @Test
    public void testEndHar() throws InterruptedException {
        this.mockWebServer.enqueue(new MockResponse().setBody("{\"port\":8081}"));
        this.bmpService.getPort();
        this.mockWebServer.takeRequest();

        this.mockWebServer.enqueue(new MockResponse().setBody("{\"log\":{}}"));
        String har = this.bmpService.endHar();
        assertThat(har).isEqualTo("{\"log\":{}}");

        RecordedRequest request = this.mockWebServer.takeRequest();
        assertThat(request.getRequestUrl().host()).isEqualTo(serverUrl.host());
        assertThat(request.getRequestUrl().port()).isEqualTo(serverUrl.port());
        assertThat(request.getMethod()).isEqualToIgnoringCase("GET");
        assertThat(request.getPath()).isEqualTo("/proxy/8081/har");
    }

}
