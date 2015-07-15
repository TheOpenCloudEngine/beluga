package org.opencloudengine.cloud.garuda.master;

import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;

public class AppStatus {

    private long deploymentTime;
    private float avgReqProcessingTime;
    
    private float avgRoundTripTime;
    private long requestsCount;
    private long processingTimeSpan;
    private long roundTripTimeSpan;

    public AppStatus() {

    }

    public AppStatus(JSONObject json) {

        // Parse json
        try {
        	System.out.println(json);
            avgReqProcessingTime = (float) json.getDouble("avgResponseTime");
            deploymentTime = json.getLong("deployTimeSeconds");
            requestsCount = json.getLong("requests");
            processingTimeSpan = json.getLong("duration");
        } catch (JSONException e) {
        }
    }

    public long getDeploymentTime() {
        return deploymentTime;
    }

    public void setDeploymentTime(long deploymentTime) {
        this.deploymentTime = deploymentTime;
    }

    public float getAvgReqProcessingTime() {
        return avgReqProcessingTime;
    }

    public void setAvgReqProcessingTime(long avgReqProcessingTime) {
        this.avgReqProcessingTime = avgReqProcessingTime;
    }

    public long getRequestsCount() {
        return requestsCount;
    }

    public void setRequestsCount(long requestsCount) {
        this.requestsCount = requestsCount;
    }

    public long getTimeSpan() {
        return processingTimeSpan;
    }

    public void setTimeSpan(int timeSpan) {
        this.processingTimeSpan = timeSpan;
    }

    @Override
    public String toString() {
        return "(" + deploymentTime + ", " + avgReqProcessingTime + ", "
                + avgRoundTripTime + ", " + requestsCount + ", "
                + processingTimeSpan + ")";
    }

    public void setAvgRoundTripTime(float roundTripTime) {
        this.avgRoundTripTime = roundTripTime;
    }

    public float getAvgRoundTripTime() {
        return avgRoundTripTime;
    }

    public void setRoundTripTimeSpan(long roundTripTimeSpan) {
        this.roundTripTimeSpan = roundTripTimeSpan;
    }

    public long getRoundTripTimeSpan() {
        return roundTripTimeSpan;
    }
}
