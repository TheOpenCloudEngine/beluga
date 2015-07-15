package org.opencloudengine.cloud.garuda.master;

import com.amazonaws.services.ec2.model.InstanceStatus;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.opencloudengine.cloud.CloudInstance;
import org.opencloudengine.cloud.garuda.master.Backend.AlreadyDeployedException;
import org.opencloudengine.cloud.garuda.master.Backend.NotDeployedException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

//import com.amazonaws.util.json.JSONObject;

public class BackendController {

	private AbstractLoadBalancerController loadBalancerController = null;
	private final HashMap<String, Backend> backends = new HashMap<String, Backend>();
	private static Backend entertainmentServer = null;
	private final Vector<String> newAppQueue = new Vector<String>();
	private Properties options = null;
	private static long lastAddedBackendTime;
	private boolean locked = false;

	/*
    private int backendControllerPort;
    private static int backendAppPort;
	 */

	public BackendController(Properties options) {
		this.options = options;

		// Setup entertainment server
		String entertainmentServerUrl = options.getProperty("entertainmentServerUrl");
		String port = null;
		try {
			port = entertainmentServerUrl.split(":")[1];
		} catch (Exception e) {
		}

		entertainmentServer = new Backend("enter", entertainmentServerUrl.split(":")[0], options, System.currentTimeMillis());
		if (port == null) {
			entertainmentServer.setAppPort(80);
		} else {
			entertainmentServer.setAppPort(Integer.valueOf(port));
		}
		System.out.println("entertainment server: " + entertainmentServer);
	}

	/*
    public void setBackendControllerPort(int port) {
        backendControllerPort = port;
    }*/

	public void setEntertainmentServer(Backend entertainmentServer) {
		BackendController.entertainmentServer = entertainmentServer;
	}

	public boolean updateBackends() {
		// Environment Inject
		updateEnvUrl();

		// Create BackendUpdaters for each Backend
		Vector<BackendUpdater> updaters = new Vector<BackendUpdater>();
		for (Backend backend : backends.values()) {
			if (backend.isRunning()) {
				BackendUpdater bu = new BackendUpdater(backend);
				updaters.add(bu);
			}
		}

		// Start updating Backends
		for (BackendUpdater bu : updaters) {
			bu.start();
		}

		try {
			Thread.sleep(10);
		} catch (Exception e) {
		}

		// Wait until all backends have been updated
		for (Backend backend : backends.values()) {
			if (backend.isRunning()) {
				backend.acquireLock();
				backend.unlock();
			}
		}

		// Check deployed app names against newAppQueue and remove all matches
		// in queue
		boolean newAppsFound = false;
		for (Backend backend : backends.values()) {
			backend.acquireLock();
			if (backend.appListUpdated()) {
				newAppsFound = true;
				for (String appName : backend.getDeployedAppNames()) {
					if (newAppQueue.contains(appName)) {
						newAppQueue.remove(appName);
					}
				}

				// Reconfigure load balancer
				try {
					Vector<Backend> availableBackends = getBackends();
					availableBackends.add(entertainmentServer);
					loadBalancerController.generateConfiguration(availableBackends);
					loadBalancerController.reconfigure();
				} catch (Exception e) {
					// e.printStackTrace();
				}

			}
			backend.unlock();
		}
		return newAppsFound;
	}

	public void updateInstanceStates(List instanceList) {
		for(Iterator iterator = instanceList.iterator(); iterator.hasNext();){
			CloudInstance instance = (CloudInstance)iterator.next();
			Backend backend = (Backend)backends.get(instance.getId());
			
			if(backend != null)
				backend.updateCloudInstance(instance);
		}
	}

	public void updateInstanceStatus(List<InstanceStatus> instanceStatusList){
		for(InstanceStatus instanceStatus : instanceStatusList){
			Backend backend = backends.get(instanceStatus.getInstanceId());

			if (backend != null) {
				backend.setEC2InstanceStatus(instanceStatus);
			}
		}
	}

	public void addBackend(Backend backend) {
		/*
        backend.setControllerMonitorPort(Integer.valueOf(options
                .getProperty("backendLocalControllerPort")));
        backend.setAppRepoUrl(options.getProperty("appRepoUrl"));
        backend.setAppPort(Integer.valueOf(options
                .getProperty("backendAppPort")));
		 */
		backends.put(backend.getInstanceId(), backend);
		lastAddedBackendTime = System.currentTimeMillis();
	}

	public void addBackends(Vector<Backend> backends) {
		if (backends != null) {
			for (Backend backend : backends) {
				addBackend(backend);
			}
		}
	}

	public void addInstances(List<CloudInstance> instances) {
		if (instances != null) {
			for (CloudInstance instance : instances) {
				addBackend(new Backend(instance, options, System.currentTimeMillis()));
			}
		}
	}

	public Vector<Backend> removeBackend(String instanceId) {
		// TODO: test this
		Vector<Backend> removedBackends = new Vector<Backend>();
		removedBackends.add(backends.get(instanceId));
		backends.remove(instanceId);
		return removedBackends;
	}

	public long getLastAddedBackendTime() {
		return lastAddedBackendTime;
	}

	public Vector<Backend> getBackends() {
		Vector<Backend> b = new Vector<Backend>();
		for (Backend backend : backends.values()) {
			b.add(backend);
		}
		return b;
	}

	public Backend getBackend(String instanceId) {
		return backends.get(instanceId);
	}
	
	public boolean hasBackend(){
		return backends.size() > 0;
	}

	public HashMap<String, Float> getBackendRanks() {
		HashMap<String, Float> backendRanks = new HashMap<String, Float>();
		for (Backend backend : backends.values()) {
			backend.acquireLock();
			if (backend.isControllerRunning()) {
				try {
					backendRanks.put(backend.getInstanceId(), backend.getRank());
				} catch (Exception e) {
				}
			}
			backend.unlock();
		}
		return backendRanks;
	}
	public HashMap<String, Float> getBackendMemRanks() {
		HashMap<String, Float> backendRanks = new HashMap<String, Float>();
		for (Backend backend : backends.values()) {
			backend.acquireLock();
			if (backend.isControllerRunning()) {
				try {
					backendRanks.put(backend.getInstanceId(), backend.getMemoryRank());
				} catch (Exception e) {
				}
			}
			backend.unlock();
		}
		return backendRanks;
	}
	public HashMap<String, Float> getBackendCpuRanks() {
		HashMap<String, Float> backendRanks = new HashMap<String, Float>();
		for (Backend backend : backends.values()) {
			backend.acquireLock();
			if (backend.isControllerRunning()) {
				try {
					backendRanks.put(backend.getInstanceId(), backend.getCpuRank());
				} catch (Exception e) {
				}
			}
			backend.unlock();
		}
		return backendRanks;
	}

	public int getRunningBackends() {
		int running = 0;
		for (Backend backend : backends.values()) {
			if (backend.isRunning() && backend.isControllerRunning()) {
				running++;
			}
		}
		return running;
	}

	public void deployApp(String appName) {

		// Check for running backends
		if (getRunningBackends() <= 0) {
			System.out.println("no backend running");
			return;
			// TODO: something smart
		}

		// Do nothing if appName is already in queue
		if (newAppQueue.contains(appName)) {
			System.out.println("app alreday in deploylist");
			return;
		} else {
			newAppQueue.add(appName);
		}

		System.out.println("deploying app " + appName);

		// Add to entertainment server
		entertainmentServer.addApp(appName);

		// Reconfigure load balancer to redirect appName to entertainmentServer
		try {
			Vector<Backend> availableBackends = getBackends();
			//availableBackends.add(entertainmentServer);
			loadBalancerController.generateConfiguration(availableBackends);
			loadBalancerController.reconfigure();
		} catch (Exception e) {
			// e.printStackTrace();
			// TODO: something
		}

		// Find the most suitable backend
		// Current strategy: find the backend with the lowest rank (lowest
		// mem/cpu usage)
		// TODO: something cool when no backends are running
		Backend deployBackend = null;
		float minRank = -1;
		for (Backend backend : backends.values()) {
			if (backend.isControllerRunning() && backend.isRunning()) {
				if (minRank == -1 || (backend.getRank() != -1 && backend.getRank() < minRank)) {
					minRank = backend.getRank();
					deployBackend = backend;
				}
				// break;
			}
		}

		System.out.println("found backend " + deployBackend.getPublicDnsName());

		// Deploy app on the chosen backend and wait for it to load
		// TODO: if the app for some reason won't start, this loop will never
		// end, fix this!
		/* TODO: A quick fix was hacked together, but all of this needs extensive refactoring */
		System.out.println("*************************************");
		System.out.println("Thread #" + Thread.currentThread().getId() + " about to acquire deployment lock");
		System.out.println("*************************************");
		deployBackend.acquireLock();

		try {
			deployBackend.deployApp(appName);
			// Wait for it to load
			while (deployBackend.appListUpdated() == false) {
				deployBackend.updateAppStatus();
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
					Thread.currentThread().interrupt();
				}
			}
		} catch (AlreadyDeployedException e) {
			String msg = e.getMessage();
			if(msg != null) {
				System.out.println(msg);
			}
		} catch (NotDeployedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			deployBackend.unlock();
			System.out.println("*************************************");
			System.out.println("Thread #" + Thread.currentThread().getId() + " released deployment lock");
			System.out.println("*************************************");
		}

		newAppQueue.remove(appName);

		System.out.println("app " + appName + " was deployed on " + deployBackend.getPublicDnsName());

		// Remove app from entertainment server
		entertainmentServer.deleteApp(appName);

		// Reconfigure load balancer
		try {
			Vector<Backend> availableBackends = getBackends();
			availableBackends.add(entertainmentServer);
			loadBalancerController.generateConfiguration(availableBackends);
			loadBalancerController.reconfigure();
		} catch (Exception e) {
			// e.printStackTrace();
			// TODO: something
		}
	}
	public void removeApp(String appName) {
		// Find all backends with the deployed app
		Vector<Backend> backendsWithApp = new Vector<Backend>();
		for (Backend backend : backends.values()) {
			backend.acquireLock();

			if (backend.getDeployedAppNames().contains(appName)) {
				backendsWithApp.add(backend);
			}
			backend.unlock();
		}
		// Remove the app from some backend
		// TODO: which backend?
		if (backendsWithApp.size() > 0) {
			backendsWithApp.get(0).acquireLock();
			try {
				backendsWithApp.get(0).removeApp(appName);
			} catch (Exception e) {
				// e.printStackTrace();
			}
			backendsWithApp.get(0).unlock();
		}
	}

	public void setLoadBalancerController(AbstractLoadBalancerController loadBalancerController) {
		this.loadBalancerController = loadBalancerController;
	}

	public void addBackendsFromFile(String path) {

		// Read lines from file
		Vector<String> configLines = new Vector<String>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(path));
			String line = null;
			while ((line = reader.readLine()) != null) {
				if (line.startsWith("#")) {
					continue;
				}
				configLines.add(line);
			}
		} catch (IOException e) {
			// We probably don't need to do anything here
			return;
		}

		// Parse lines
		for (String line : configLines) {

			String[] parts = line.split(" ");
			String type, instanceId;

			// Separate the parts
			try {
				type = parts[0];
				instanceId = parts[1];
			} catch (Exception e) {
				continue;
			}

			String publicDnsName = null;
			try {
				publicDnsName = parts[2].split(":")[0];
			} catch (Exception e) {
			}

			// See if a port was specified. Example: "localhost:1234"
			String port = null;
			try {
				port = parts[2].split(":")[1];
			} catch (Exception e) {
			}

			if (publicDnsName != null && publicDnsName.endsWith("\n")) {
				publicDnsName = publicDnsName.substring(0,
						instanceId.length() - 2);
			}

			// Make an instance of Instance
			CloudInstance i = new CloudInstance();
			i.setId(instanceId);
			i.setPublicDnsName(publicDnsName);

			// Create and add a new instance of Backend to the list if it's of
			// type "backend" or
			// "appserver"
			if (type.equals("appserver") || type.equals("backend")) {
				System.out
				.println("adding existing instance: " + publicDnsName);
				Backend newBackend = null;

				newBackend = new Backend(i, options, System.currentTimeMillis());

				// If a port number was explicitly specified, set it
				if (port != null) {
					newBackend.setControllerPort(Integer.valueOf(port));
				}

				this.addBackend(newBackend);
			}

			// Check if entertainment server
			/*
            if (type.equals("entertainment")) {
                entertainmentServer = new Backend(i);
                if (port == null) {
                    entertainmentServer.setAppPort(80);
                } else {
                    entertainmentServer.setAppPort(Integer.valueOf(port));
                }
                System.out.println("entertainment server: "
                        + entertainmentServer);
            }*/
		}
	}

	public void unlock() {
		this.locked = false;
	}

	public void acquireLock() {
		while (locked) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		}
		locked = true;
	}

	@Override
	public String toString() {
		String r = "";

		int N = backends.size();
		int M = 0;

		if (backends.size() > 0) {
			r += "instance id\tpublic dns\tstate\tcontroller\tlast seen\tapps\n";
			// r +=
			// "-----------------------------------------------------------------------------------------------------\n";

			for (Backend backend : backends.values()) {
				backend.acquireLock();
				r += backend.toString() + "\n";

				if (backend.isRunning()) {
					M++;
				}

				backend.unlock();
			}
			r += "\ninstances: " + M + "\n";
			r += "pending: " + (N - M) + "\n";
		} else {
			r += "no backends running\n";
		}
		return r;
	}

	public void updateEnvUrl() {
		if (options.getProperty("envInjectInfo") != null || options.getProperty("jgroupsXmlPath") != null) {
			try {
				String envInjectInfo = options.getProperty("envInjectInfo");

				JSONParser parser = new JSONParser();
				Object obj = parser.parse(new FileReader(envInjectInfo));

				JSONObject svcJson = (JSONObject) obj;
				Iterator<?> svcKeys = svcJson.keySet().iterator();

				File jgroupsXml = new File(options.getProperty("jgroupsXmlPath"));
				HashMap mapInject = new HashMap();
				JChannel jc = new JChannel(jgroupsXml);
				jc.connect("garudaCluster");    	

				while(svcKeys.hasNext()){
					String serviceName = (String)svcKeys.next();
					if( svcJson.get(serviceName) instanceof JSONObject ){
						JSONObject subJson = (JSONObject)svcJson.get(serviceName);
						Iterator<?> confKeys = subJson.keySet().iterator();
						while(confKeys.hasNext()){
							String confName = (String)confKeys.next();
							mapInject.put(serviceName+"."+confName , subJson.get(confName));
						}
					}
				}

				jc.send(new Message(null, mapInject));
				jc.close();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


	/*public static void main(String args[]){
		JSONParser parser = new JSONParser();
		Object obj;
		try {
			obj = parser.parse(new FileReader("E:\\source\\uengine\\garuda-garudamaster\\src\\main\\resources\\conf\\env.json"));
			HashMap mapInject = new HashMap();

			JSONObject svcJson = (JSONObject) obj;
			Iterator<?> svcKeys = svcJson.keySet().iterator();

			while(svcKeys.hasNext()){
				String serviceName = (String)svcKeys.next();
				if( svcJson.get(serviceName) instanceof JSONObject ){
					JSONObject subJson = (JSONObject)svcJson.get(serviceName);
					Iterator<?> confKeys = subJson.keySet().iterator();
					while(confKeys.hasNext()){
						String confName = (String)confKeys.next();
						mapInject.put(serviceName+"."+confName , subJson.get(confName));
					}
	            }
			}

			System.out.println(mapInject.toString());

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}

	}*/

	/*
    public static void setBackendAppPort(int backendAppPort) {
        BackendController.backendAppPort = backendAppPort;
    }

    public static int getBackendAppPort() {
        return backendAppPort;
    }
	 */
}
