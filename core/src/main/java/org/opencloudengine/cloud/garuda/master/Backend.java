package org.opencloudengine.cloud.garuda.master;

import com.amazonaws.services.ec2.model.InstanceState;
import com.amazonaws.services.ec2.model.InstanceStatus;
import com.amazonaws.services.ec2.model.InstanceStatusSummary;
import com.amazonaws.util.json.JSONArray;
import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;
import org.opencloudengine.cloud.CloudInstance;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class Backend {

	// private String dnsName;
	// private String instanceId;
	// private String instanceState;
	private static Properties options;
	private CloudInstance instance;
	private InstanceStatus ec2InstanceStatus;
	
	private int statusHistorySize = 10;
	private long lastUpdate;
	private final Vector<BackendStatus> statusHistory = new Vector<BackendStatus>();
	private Vector<SessionObject> sessionObjectList = new Vector<SessionObject>();
	private boolean controllerRunning = false;
	private boolean appListUpdated = false;

	private final Lock lock = new ReentrantLock();

	private long startTimeInMilliseconds = 0;
	private int controllerPort;
	private int appPort;
	private HashMap<String,Integer> sessionTime = new HashMap<String, Integer>();

	// private HashMap<String, DeployedAppStatus> deployedApps = new
	// HashMap<String, DeployedAppStatus>();
	private final HashMap<String, App> deployedApps = new HashMap<String, App>();
	private final HashMap<String, App> availibleApps = new HashMap<String, App>();

	public Backend(CloudInstance instance, Properties options, long startTimeInMilliseconds) {
		this.instance = instance;
		Backend.options = options;
		this.setStartTimeInMilliseconds(startTimeInMilliseconds);
		setVariablesFromOptions();

		// If the instance has no state we assume it's not an ec2 instance and
		// is already up and running
		if (instance.getState() == null || instance.getState().length() <= 2) {
			InstanceState state = new InstanceState();
			state.setName("running");
		}
	}

	public Backend(String id, String dns, Properties options, long startTimeInMilliseconds) {
		instance = new CloudInstance();
		instance.setPublicDnsName(dns);
		instance.setId(id);

		InstanceState state = new InstanceState();
		state.setName("running");

		Backend.options = options;
		setVariablesFromOptions();
	}


	public void setVariablesFromOptions() {
		setControllerPort(Integer.valueOf(options.getProperty("backendLocalControllerPort")));
		appPort = Integer.valueOf(options.getProperty("backendAppPort"));
	}

	public void addApp(String appName) {
		App app = new App(appName);
		deployedApps.put(app.getAppName(), app);
		availibleApps.put(app.getAppName(), app);
	}

	public void deleteApp(String appName) {
		if (deployedApps.containsKey(appName)) {
			deployedApps.remove(appName);            
		}
		if (availibleApps.containsKey(appName)) {
			availibleApps.remove(appName);            
		}
	}

	public Set<String> getDeployedAppNames() {
		return deployedApps.keySet();
	}

	public Collection<App> getDeployedApps() {
		return deployedApps.values();
	}
	public Set<String> getAvailibleAppNames() {
		return availibleApps.keySet();
	}
	public Collection<App> getAvailibleApps() {
		return availibleApps.values();
	}
	public void setAppUnavailible(String appName) {
		if(deployedApps.containsKey(appName) && availibleApps.containsKey(appName)) {
			availibleApps.remove(appName);
		}
	}
	public void setAppAvailible(String appName) {
		if(deployedApps.containsKey(appName)) {
			availibleApps.put(appName, deployedApps.get(appName));
		}
	}
	
	
	/**
	 * Update the application repository url on the backend.
	 * 
	 * @param repoUrl
	 *            The hostname (and :port) of the application repository
	 */
	public void updateRepoUrl(String repoUrl) {
		// Check which app repository url is used on the app server and
		// update it if it's wrong
		if (!repoUrl.equals(options.getProperty("appRepoUrl"))) {
			try {
				sendCommand("/updateRepoUrl/"
						+ options.getProperty("appRepoUrl"));
			} catch (IOException e) {
				// e.printStackTrace();
			}
		}
	}

	public void updateAppStatus() {

		// Empty deployed apps list
		// deployedApps.clear();

		// Fetch status report from remote ArvueMonitor
		String report = null;
		try {
			report = sendCommand("/status");
		} catch (IOException e) {
			// e.printStackTrace();
			controllerRunning = false;
			return;
		}

		// Parse report
		BackendStatus status = null;
		if (report != null && report.length() > 0) {
			try {
				status = parseStatusJSON(report);
				controllerRunning = true;
			} catch (Exception e) {
				controllerRunning = false;
			}
		}
		
		// Do stuff if the status was fetched properly
		if (status != null) {

			// Add status to history
			addStatus(status);

			// Update the app repo url (if neccessary)
			updateRepoUrl(status.getAppRepoUrl());
			
			// Update the AWS credentials (just in case)
			updateAWSCredentials();
		}

	}

	/**
	 * Sends the AWS AccessKey and SecretKey to the local running controller on
	 * the backend.
	 */
	public void updateAWSCredentials() {
		String accessKey = options.getProperty("repoAccessKey");
		String secretKey = options.getProperty("repoSecretKey");

		try {
			sendCommand("/updateAccessKey/" + accessKey);
		} catch (IOException e) {
			// e.printStackTrace();
		}
		try {
			sendCommand("/updateSecretKey/" + secretKey);
		} catch (IOException e) {
			// e.printStackTrace();
		}

	}
/* TODO: This code is horrible
 * 
 */
	public void deployApp(String appName) throws AlreadyDeployedException, NotDeployedException, IOException {
		String response = null;
		response = sendCommand("/" + appName + "/deploy");

		// Parse response
		String result = null;
		String message = null;
		try {
			result = new JSONObject(response).getString("result");
			message = new JSONObject(response).getString("message");
		} catch (Exception e) {
			System.out.println("caught exception when deploying app "+appName);
			e.printStackTrace();
			System.out.println(result);
			System.out.println(message);        
		}
		
		// Throw exception if the action failed
		if (result == null || !result.equals("success")) {
			if(message.contains("deployed")) {
				throw new AlreadyDeployedException("Could not deploy application " + appName + " because it was already deployed");
			}
			else {
				System.out.println("failed to deploy application "+appName+" because: "+message);
				throw new NotDeployedException();
			}
		}
		return;
	}

	/*TODO: This is part of a temporary fix for a far greater problem regarding app deployment -- exceptions are ugly*/
	public class AlreadyDeployedException extends Exception {

		/**
		 * 
		 */
		private static final long serialVersionUID = -7760019836427681768L;
		
		AlreadyDeployedException(String message) {
			super(message);
		}
		
	}
	
	public class NotDeployedException extends Exception {
		/**
		 * 
		 */
		private static final long serialVersionUID = -2223165987165602490L;

		NotDeployedException() {
			super();
		}
	}
	public void removeApp(String appName) throws Exception {
		String response = null;

		response = sendCommand("/" + appName + "/remove");

		// Parse response
		String result = null;
		String message = null;
		try {
			result = new JSONObject(response).getString("result");
			message = new JSONObject(response).getString("message");
		} catch (Exception e) {

		}

		// Throw execption if the action failed
		if (result == null || !result.equals("success")) {
			if(message.contains("deployed")) {
				System.out.println("Could not remove application " +
				"because it was never deployed");
			}
			else {
				System.out.println("failed to remove application "+appName+" because: "+message);
				throw new Exception();
			}
		}
		else {
			resetSessionTime(appName);
		}
		return;
	}
	public float getMemoryRank() {
		if (statusHistory.size() > 0) {
			float memRank = this.getLastStatus().getMemUsage() * 10;
			memRank = memRank * memRank;
			return memRank;
		}
		else {
			return -1;
		}
	}
	public float getCpuRank() {
		if (statusHistory.size() > 0) {
			float cpuRank = this.getLastStatus().getCpuUsage1() * 10;
			cpuRank = cpuRank * cpuRank;
			return cpuRank;
		}
		else {
			return -1;
		}
	}
	public float getRank() {
		if (statusHistory.size() > 0) {
			float memRank = this.getLastStatus().getMemUsage() * 10;
			float cpuRank = this.getLastStatus().getCpuUsage1() * 10;
			memRank = memRank * memRank;
			cpuRank = cpuRank * cpuRank;
			return memRank + cpuRank;
		} else {
			return -1;
		}
	}

	public boolean isControllerRunning() {
		/*
        if (ec2Instance.getState().getName().equals("pending")) {
            return true;
        }
        return false;
		 */
		return controllerRunning;
	}

	private void addStatus(BackendStatus backendStatus) {
		statusHistory.add(backendStatus);

		if (statusHistory.size() > statusHistorySize) {
			statusHistory.remove(0);
		}
	}

	public BackendStatus getLastStatus() {
		if (statusHistory.size() > 0) {
			return statusHistory.get(statusHistory.size() - 1);
		}
		return null;
	}

	public Vector<BackendStatus> getStatusHistory() {
		return statusHistory;
	}

	private String sendCommand(String command) throws IOException {
		
		
		String response = "";
		String sendURL = "http://" + instance.getPublicDnsName() + ":" + getControllerPort() + command;
		URL backendServer = new URL(sendURL);
		
		/*
        BufferedReader in = new BufferedReader(new InputStreamReader(
                backendServer.openConnection().getInputStream()));
        response = in.readLine();

        in.close();
		 */
		URLConnection conn = backendServer.openConnection();
		conn.setDoOutput(true);
		OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
		wr.write("");
		wr.flush();

		// Get the response
		BufferedReader rd = new BufferedReader(new InputStreamReader(conn
				.getInputStream()));
		String line;
		while ((line = rd.readLine()) != null) {
			if (line != null) {
				response += line;
			}
		}
		wr.close();
		rd.close();

		return response;
	}
	
	
//	public static void main(String args[]){
//		String response = "";
//		String sendURL = "http://" + instance.getPublicDnsName() + ":" + getControllerPort() + command;
//		
//		URL backendServer = new URL(sendURL);
//		/*
//        BufferedReader in = new BufferedReader(new InputStreamReader(
//                backendServer.openConnection().getInputStream()));
//        response = in.readLine();
//
//        in.close();
//		 */
//		URLConnection conn = backendServer.openConnection();
//		conn.setDoOutput(true);
//		OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
//		wr.write("");
//		wr.flush();
//
//		// Get the response
//		BufferedReader rd = new BufferedReader(new InputStreamReader(conn
//				.getInputStream()));
//		String line;
//		while ((line = rd.readLine()) != null) {
//			if (line != null) {
//				response += line;
//			}
//		}
//		wr.close();
//		rd.close();
//	}

	private BackendStatus parseStatusJSON(String reportString)
	throws JSONException, ParseException {

		// HashMap<String, DeployedAppStatus> newApps = new HashMap<String,
		// DeployedAppStatus>();
		appListUpdated = false;
		JSONObject json = new JSONObject(reportString);
		System.out.println("BackendStatus ==> " + json);
		// Parse machine stats
		BackendStatus bs = new BackendStatus();
		bs.setAppRepoUrl(json.getString("repoUrl"));
		bs.setCpuUsage1((float) json.getDouble("cpu1min"));
		bs.setCpuUsage5((float) json.getDouble("cpu5min"));
		bs.setCpuUsage10((float) json.getDouble("cpu10min"));
		bs.setMemTotal(json.getLong("memTotal"));
		bs.setMemFree(json.getLong("memFree"));
		this.addStatus(bs);

		// Parse deployed apps
		JSONArray apps = json.getJSONArray("deployedApps");
		Vector<String> updatedApps = new Vector<String>();
		for (int i = 0; i < apps.length(); i++) {
			JSONObject appJSON = apps.getJSONObject(i);

			String appName = appJSON.getString("appName");
			//String bundleName = appJSON.getString("bundleName");

			// Update app list
			App app = deployedApps.get(appName);
			if (app == null) {
				App newApp = new App(appName);
				// newApp.setAppName(appName);
				// newApp.setBundleName(bundleName);
				deployedApps.put(appName, newApp);
				availibleApps.put(appName, newApp);
				appListUpdated = true;
			} else {
				// app.setAppName(appName);
				// app.setBundleName(bundleName);
				app.updateApp(appJSON);
				/* Why in the world would this be needed?
				deployedApps.put(appName, app);
				availibleApps.put(appName, app); */
			}

			// Remove app from previous list
			updatedApps.add(appName);
			// newApp.setVersion(app.getString("version"));
		}

		// Remove 'undeployed' apps from list
		Vector<String> removedApps = new Vector<String>();
		for (String appName : deployedApps.keySet()) {
			if (!updatedApps.contains(appName)) {
				removedApps.add(appName);
			}
		}
		for (String appName : removedApps) {
			deployedApps.remove(appName);
			appListUpdated = true;
		}

		// Backend status was updated successfully, i.e. not pending
		controllerRunning = false;
		lastUpdate = System.currentTimeMillis();
		return bs;
	}

	public void unlock() {
		lock.unlock();
	}

	public void acquireLock() {
		lock.lock();
	}

	public long getLastUpdateTime() {
		return lastUpdate;
	}

	public String getInstanceId() {
		return instance.getId();
	}

	public String getPublicDnsName() {
		return instance.getPublicDnsName();
	}

	public String getState() {
		return instance.getState();
	}

	public void updateCloudInstance(CloudInstance instance) {
		this.instance = instance;
	}
	
	public CloudInstance getCloudInstance() {
		return instance;
	}
	
	public boolean isPassedStatus(){
		InstanceStatus instanceStatus = this.getEC2InstanceStatus();
		
		if(instanceStatus != null){
			InstanceStatusSummary instanceStatusSummary = instanceStatus.getInstanceStatus();
			InstanceStatusSummary systemStatusSummary = instanceStatus.getSystemStatus();
			
			if("ok".equals(instanceStatusSummary.getStatus()) && "ok".equals(systemStatusSummary.getStatus()))
				return true;
		}
		
		return false;
	}
	
	public boolean isRunning() {
		if (instance.isRunning()) {
			return true;
		}
		return false;
	}

	public boolean appListUpdated() {
		return appListUpdated;
	}

	@Override
	public String toString() {

		String r = "";
		r += this.getInstanceId() + "\t";
		r += this.getPublicDnsName() + "\t";
		r += this.getState() + "\t" + this.isControllerRunning() + "\t"
		+ new Date(this.getLastUpdateTime()) + "\t";
		r += this.getDeployedAppNames().size() + "\t";

		// Check rank
		try {
			r += this.getRank();
		} catch (Exception e) {
		}
		return r;
	}

	public void setStatusHistorySize(int statusHistorySize) {
		this.statusHistorySize = statusHistorySize;
	}

	public int getStatusHistorySize() {
		return statusHistorySize;
	}

	public void updateAppRttTime(HashMap<String, Float> rtt) {

		// Loop through app names
		for (String appName : rtt.keySet()) {
			float roundTripTime = rtt.get(appName);

			// Update app status
			if (deployedApps.containsKey(appName)) {
				App app = deployedApps.get(appName);
				AppStatus appStatus = app.getLastStatus();
				if (appStatus == null) {
					appStatus = new AppStatus();
					app.addStatus(appStatus);
				}
				appStatus.setAvgRoundTripTime(roundTripTime);
			}
		}
	}

	public String getAppPort() {
		return String.valueOf(appPort);
	}

	public void setAppPort(int appPort) {
		this.appPort = appPort;
	}

	public void setControllerPort(int controllerPort) {
		this.controllerPort = controllerPort;

	}

	public void setStartTimeInMilliseconds(long startTimeInMilliseconds) {
		this.startTimeInMilliseconds = startTimeInMilliseconds;
	}

	public long getStartTimeInMilliseconds() {
		return startTimeInMilliseconds;
	}

	public boolean setSessionObjectList(Vector<SessionObject> appSessionList) {
		this.sessionObjectList = appSessionList;
		return true;
	}

	public Vector<SessionObject> getSessionObjectList() {
		return sessionObjectList;
	}
	public void addSessionTime(String appName, int time) {
		if(sessionTime.containsKey(appName)) {
			int current = sessionTime.get(appName);
			sessionTime.put(appName, (current+time));
		}
		else {
			sessionTime.put(appName, time);
		}
	}
	public void resetSessionTime(String appName) {
		sessionTime.remove(appName);
	}
	public HashMap<String, Integer> getSessionTime() {
		return sessionTime;
	}

	public int getControllerPort() {
		return controllerPort;
	}

	public InstanceStatus getEC2InstanceStatus() {
		return ec2InstanceStatus;
	}

	public void setEC2InstanceStatus(InstanceStatus ec2InstanceStatus) {
		this.ec2InstanceStatus = ec2InstanceStatus;
	}
}
