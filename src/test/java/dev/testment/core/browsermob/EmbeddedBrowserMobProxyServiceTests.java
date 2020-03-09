package dev.testment.core.browsermob;

import dev.testment.core.browsermob.services.EmbeddedBrowserMobProxyService;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.core.har.*;
import net.lightbody.bmp.proxy.CaptureType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.openqa.selenium.Proxy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EmbeddedBrowserMobProxyServiceTests {

    @Mock
    private BrowserMobProxyServer bmpServerMock;

    @InjectMocks
    private EmbeddedBrowserMobProxyService bmpService;

    @Test
    public void testStart() {
        this.bmpService.start();
        verify(this.bmpServerMock).start();
    }

    @Test
    public void testStop() {
        this.bmpService.stop();
        verify(this.bmpServerMock).stop();
    }

    @Test
    public void testCreateSeleniumProxy() {
        Proxy proxy = this.bmpService.createSeleniumProxy(8080);
        assertThat(proxy).isNotNull();
        assertThat(proxy.getHttpProxy()).isEqualTo("localhost:8080");
        assertThat(proxy.getSslProxy()).isEqualTo("localhost:8080");
    }

    @Test
    public void testGetPort() {
        when(this.bmpServerMock.getPort()).thenReturn(8081);
        assertThat(this.bmpService.getPort()).isEqualTo(8081);
    }

    @Test
    public void testSetTrustAllServers() {
        this.bmpService.setTrustAllServers(true);
        verify(this.bmpServerMock).setTrustAllServers(true);
    }

    @Test
    public void testSetHarCaptureTypes() {
        this.bmpService.setHarCaptureTypes(CaptureType.REQUEST_HEADERS, CaptureType.RESPONSE_CONTENT);
        verify(this.bmpServerMock).setHarCaptureTypes(CaptureType.REQUEST_HEADERS, CaptureType.RESPONSE_CONTENT);
    }

    @Test
    public void testNewHar() {
        this.bmpService.newHar();
        verify(this.bmpServerMock).newHar();
    }

    @Test
    public void testEndHar() {
        Har h = new Har();
        h.setLog(new HarLog());
        when(this.bmpServerMock.getHar()).thenReturn(h);
        String har = this.bmpService.endHar();
        assertThat(har).isNotEmpty();
    }

    @Test
    public void testGetProxy() {
        assertThat(this.bmpService.getProxy()).isEqualTo(this.bmpServerMock);
    }

}
