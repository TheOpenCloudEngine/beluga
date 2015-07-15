package org.opencloudengine.cloud.garuda.master;

import com.amazonaws.util.json.JSONObject;

import java.util.Vector;

public class App {

    private static int logHistoryTime = 60; // Log history in seconds
    private String appName, version;
    private String bundleName;

    private final Vector<AppStatus> appStatusHistory = new Vector<AppStatus>();

    public App() {

    }

    public App(JSONObject json) {
    	
    }

    public App(String appName) {
        this.appName = appName;
    }

    public App(String appName, int logHistoryTime) {
        this.appName = appName;
        this.logHistoryTime = logHistoryTime;
    }
    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getBundleName() {
        return bundleName;
    }

    public void setBundleName(String bundleName) {
        this.bundleName = bundleName;
    }

    @Override
    public String toString() {
        return appName + " " + appStatusHistory.size();
    }

    public void addStatus(AppStatus status) {
        appStatusHistory.add(status);
    }

    public AppStatus getLastStatus() {
        if (appStatusHistory.size() > 0) {
            return appStatusHistory.get(0);
        }
        return null;
    }

    public Vector<AppStatus> getStatusHistory() {
        return appStatusHistory;
    }

    public void updateApp(JSONObject json) {
        AppStatus newStatus = new AppStatus(json);
        this.addStatus(newStatus);

    }

    /*
    public void removeExpiredRequests(Calendar referenceTime) {

        if (appStatusHistory.size() > 1) {

            referenceTime.add(Calendar.SECOND, -logHistoryTime);

            for (int i = 0; i < appStatusHistory.size(); i++) {

                if (appStatusHistory.get(i).getTimeStamp().before(referenceTime)) {
                    System.out.println("removed from " + appName + ": "
                            + appStatusHistory.get(i));
                    appStatusHistory.remove(i);
                }
            }
        }
    }*/

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }
}
