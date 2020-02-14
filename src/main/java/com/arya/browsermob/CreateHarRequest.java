package com.arya.browsermob;

public class CreateHarRequest {
    private boolean captureHeaders;
    private boolean captureCookies;
    private boolean captureContent;
    private boolean captureBinaryContent;

    public boolean isCaptureHeaders() {
        return captureHeaders;
    }

    public void setCaptureHeaders(boolean captureHeaders) {
        this.captureHeaders = captureHeaders;
    }

    public boolean isCaptureCookies() {
        return captureCookies;
    }

    public void setCaptureCookies(boolean captureCookies) {
        this.captureCookies = captureCookies;
    }

    public boolean isCaptureContent() {
        return captureContent;
    }

    public void setCaptureContent(boolean captureContent) {
        this.captureContent = captureContent;
    }

    public boolean isCaptureBinaryContent() {
        return captureBinaryContent;
    }

    public void setCaptureBinaryContent(boolean captureBinaryContent) {
        this.captureBinaryContent = captureBinaryContent;
    }
}
