package com.arya.simulation;

public class RuntimeOptions {

    private boolean autoScreenshots;
    private int autoWaitTime;

    public boolean isAutoScreenshots() {
        return autoScreenshots;
    }

    public void setAutoScreenshots(boolean autoScreenshots) {
        this.autoScreenshots = autoScreenshots;
    }

    public int getAutoWaitTime() {
        return autoWaitTime;
    }

    public void setAutoWaitTime(int autoWaitTime) {
        this.autoWaitTime = autoWaitTime;
    }

}
