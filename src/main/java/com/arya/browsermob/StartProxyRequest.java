package com.arya.browsermob;

public class StartProxyRequest {
    private boolean trustAllServers;

    public boolean isTrustAllServers() {
        return trustAllServers;
    }

    public void setTrustAllServers(boolean trustAllServers) {
        this.trustAllServers = trustAllServers;
    }
}

