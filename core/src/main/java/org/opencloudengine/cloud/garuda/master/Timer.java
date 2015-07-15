package org.opencloudengine.cloud.garuda.master;

public class Timer {

    private long startTime;
    private long stopTime;

    public Timer() {

    }

    public void startTimer() {
        startTime = System.currentTimeMillis();
    }

    public long getStartTime() {
        return startTime;
    }

    public void stopTimer() {
        stopTime = System.currentTimeMillis() - startTime;
    }

    public long getStopTime() {
        return stopTime;
    }

    public long getTime() {
        return System.currentTimeMillis() - startTime;
    }
}