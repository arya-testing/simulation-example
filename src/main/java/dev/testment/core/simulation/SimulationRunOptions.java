package dev.testment.core.simulation;

public class SimulationRunOptions {

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
