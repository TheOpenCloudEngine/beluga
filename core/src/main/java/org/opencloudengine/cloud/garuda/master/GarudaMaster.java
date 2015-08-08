package org.opencloudengine.cloud.garuda.master;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jclouds.compute.RunNodesException;
import org.opencloudengine.cloud.CloudInstance;
import org.opencloudengine.cloud.builder.CloudBuilder;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.rmi.RemoteException;
import java.util.*;

public class GarudaMaster {
	private static String LOCALHOST = "localhost";
	private static String groupId = null;
	
	private static int statusRound= 0;
	private static Vector<CloudInstance> masterInstances = null;
	private static BackendController backendController = null;	
	private static Vector<Backend> downscalingInstances = new Vector<Backend>();
	private static Vector<Backend> backendsEglibleForMigration = new Vector<Backend>();
	private static AbstractLoadBalancerController loadBalancerController = null;
	private static CloudBuilder cloudBuilder = null;
	private static Properties options = new Properties();
	private static long iterationTime;
	private static int minInstances, maxInstances;
	private static int runtime = 0;
	private static int memTotalMin = 80;
	private static int cpuTotalMin = 90;
	private static int singleAppLoad = 200;
	private static int memTotalMax = 50;
	private static int cpuTotalMax = 50;
	private static float individualCpuLoad = 1;
	private static float individualMemLoad = (float) 0.8;

	private static String ipAddress;

	/**
	 * @param args
	 * @throws RunNodesException 
	 */
	public static void main(String[] args) throws RunNodesException {
		
		// Disable annoying logger
		Logger rootLogger = LogManager.getRootLogger();
		Level lev = Level.toLevel("ERROR");
		rootLogger.setLevel(lev);

		// Set default properties and load properties from file before we start looping
		setOptions();
		// Check if a config file was specified with command line args 
		// and parse command line arguments		
		parseCommandLine(args);
		// Setup and initialize EC2 Interface
		setupCloudInterface();
		//Setup backendcontroller and loadBalancerController
		setupControllers();
		//Setup provisioner and sessionhandler
		setupHandlers();		

		// Set new rules
		// TODO : localhost 임시 처
		//ipAddress = getIP();
		ipAddress = LOCALHOST;
		groupId = options.getProperty("groupId");
		
		// Add existing backends
		if (!options.getProperty("runningBackendsList").equals("")) {
			backendController.addBackendsFromFile(options.getProperty("runningBackendsList"));
		}
		
		// Start loadbalancer
		startLoadBalancer();
		
		// Start the syslog server interface
		SyslogServerInterface syslogServer = new SyslogServerInterface();
		syslogServer.setPort(Integer.valueOf(options.getProperty("syslogServerPort")));
		syslogServer.setHost(options.getProperty("syslogServerHost"));

		//masterInstances = ec2Interface.getRunningInstances("arvuemaster");

		System.out.println("Starting monitoring");		
		/**
		 * Control loop used for adding and removing servers aswell as 
		 * deploying, undeploying and migrating applications.
		 */	
		while (true) {
			scanForNewInput();
			System.out.println("Updating ");
			
			backendsEglibleForMigration = new Vector<Backend>();
			long iterationStartTime = System.currentTimeMillis();

			// Update instance states from EC2 and backends
			updateInstancesAndBackends(syslogServer);			
			// Check if there are new AppServer instances that were started
			// outside the application
			List<CloudInstance> runningInstances = cloudBuilder.getRunningInstances(groupId);
			
			checkAppServers(runningInstances);
			/* Broadcast master1 address to running backends */
			//broadcastMasterAddress();

			// Update backend statuses
			backendController.acquireLock();
			backendController.updateBackends();
			backendController.unlock();
			// Check limitations (min and max) compared to active instances
			int activeInstances = backendController.getBackends().size();
			//System.out.println("checking limitations on backends...");
			checkLimitations(activeInstances);
			/*Check the list of backends that we are waiting for sessions 
			 * to end so we can close them.
			 */
			
			Vector<Backend> backendsForRemoval = new Vector<Backend>();
			checkSessions(backendsForRemoval);
			if(backendsForRemoval.size() != 0) {
				removeBackends(backendsForRemoval);
			}
			
			/*
			 * Check if the average load on servers is too high or too low, so that 
			 * we can decide to start or terminate instances.
			 */
			backendController.acquireLock();            
			HashMap<String, Float> backendMemRanks = backendController.getBackendMemRanks();
			HashMap<String, Float> backendCpuRanks = backendController.getBackendCpuRanks();   
			backendController.unlock();
			updateRanks(backendMemRanks, backendCpuRanks, runningInstances);		

			/*
			 * Check if a individual AppServers have too much load
			 */			
			Vector <Backend> backendCpuLoadList = checkIndividualCpuLoad();	
			Vector <Backend> backendMemLoadList = checkIndividualMemLoad();
			Vector <Backend> currentMigrationList = new Vector<Backend>();
			/*
			 * Migrate applications from a server if it has too much 
			 * load.
			 */
			if(backendCpuLoadList != null) {
				for(Backend backendLoad: backendCpuLoadList) {				
					//Checks how many applications that are to be migrated based on CPU usage

					String singleAppWithTooMuchLoad = checkSingleApps(backendLoad);
					if(singleAppWithTooMuchLoad == null) {
						Set<String> appsForMigration = getAppsForMigration(backendLoad, true);
						if(backendLoad != null && backendsEglibleForMigration.size() > 0 && !backendsEglibleForMigration.contains(backendLoad)) {
							//start migrating the applications 
							migrateApplications(backendLoad, backendsEglibleForMigration, appsForMigration);
							currentMigrationList.add(backendLoad);
						}
					}
					else {
						if(backendsEglibleForMigration.size() > 0 ) {
							Backend toBackend = null;
							Random randomBackend = new Random();							
							boolean carry = true;
							int maxRounds = 0;
							while(carry) {
								int randomNumber = randomBackend.nextInt(backendsEglibleForMigration.size());
								toBackend = backendsEglibleForMigration.get(randomNumber);
								if(!toBackend.getDeployedAppNames().contains(singleAppWithTooMuchLoad)) {									
									try {
										toBackend.deployApp(singleAppWithTooMuchLoad);
										toBackend.addApp(singleAppWithTooMuchLoad);
									} catch (Exception e) {
										e.printStackTrace();
									}
									System.out.println("Deployed application "+singleAppWithTooMuchLoad+" on another server");
									carry = false;
								}
								else if(toBackend.getDeployedAppNames().contains(singleAppWithTooMuchLoad)) {
									if(toBackend.getAvailibleAppNames().contains(singleAppWithTooMuchLoad)) {
										System.out.println("Backend already has "+singleAppWithTooMuchLoad +" deployed and availible.");
										maxRounds++;
										if(maxRounds < 5) {
											continue;
										}
									}							
								}
								carry = false;
							}
						}
					}
				}
			}
			
			for(Backend backendLoad: backendMemLoadList) {
				if(!currentMigrationList.contains(backendLoad)) {
					//Checks how many applications that are to be migrated based on Mem usage
					Set<String> appsForMigration = getAppsForMigration(backendLoad, false);
					if(backendLoad != null && backendsEglibleForMigration.size() > 0 && 
							!backendsEglibleForMigration.contains(backendLoad)) {
						//start migrating the applications 
						migrateApplications(backendLoad, backendsEglibleForMigration, appsForMigration);					
					}
				}
			}	
			
			/**
			 * Check if we have too many servers active at this given moment.
			 */
			//Check if individual 

			//if we have servers eglible for downscaling we check if downscaling is necessary			
			/**
			 * Undeploy application if there has been no 
			 * traffic to the application for the last X seconds
			 */
			/*
			 * Check the backends which applications currenlty have sessions. Then
			 * add the time that has passed since last check if no session for that 
			 * application is active.
			 */
			// TODO: session 을 사용하지 않는 부분에 대한 고민 필요
			//checkBackendSessions();
			/**
			 * Migrate applications from a server if the systemload is low 
			 * enough that we can close one instance.
			 */
			/*
			 * migrateAllApplications(backendWithLeastLoad)
			 */
			runtime += (int)iterationTime/1000;
			/*
			System.out.println("------------------------------------------------------------------------------");
			System.out.println("ArvueMaster has been running: "+runtime+" seconds");
			System.out.println("------------------------------------------------------------------------------");
			System.out.println("\n" + new Date() + " Status Update " + statusRound);
			*/
			
			System.out.println(backendController);

			// Sleep until it's time for the next iteration
			long lastIterationTime = System.currentTimeMillis() - iterationStartTime;	
			if (lastIterationTime < iterationTime) {
				try {
					Thread.sleep(iterationTime - lastIterationTime);
				} 
				catch (InterruptedException e) {
				}
			}
			
			if(true) {
				try{
					String text ="Backends: "+backendController.getRunningBackends()+"\n";
					for(Backend b : backendController.getBackends()) {
						text+="Backend: "+b.getInstanceId()+" ";
						text+=" Availible apps: "+b.getAvailibleAppNames();
						text+=" Deployed apps: "+b.getDeployedApps();
						text+=" Cpu rank: "+b.getCpuRank();
						text+=" Memory rank: "+b.getMemoryRank()+"\n";
					}

					//System.out.println(text);
					
					// Create file
					if(!options.getProperty("printOutPut").equals("")){
						FileWriter fstream = new FileWriter(options.getProperty("printOutPut"));
						BufferedWriter out = new BufferedWriter(fstream);
						out.write(text);
						//Close the output stream
						out.close();
					}
					
				}catch (Exception e){//Catch exception if any
					System.err.println("Error: " + e.getMessage());
				}				
			}
		}
	}
	private static void scanForNewInput() {
		String text = "";
		Scanner scanner = null;
		try {
			scanner = new Scanner(new FileInputStream("/tmp/ArvueMasterInput"));
		} catch (FileNotFoundException e1) {
			return;
		}
		try {				
			while (scanner.hasNextLine()) {
				text += scanner.nextLine();					
			}									
		}
		finally {
			if(scanner != null) {
				scanner.close();
			}
		}
		StringTokenizer st = new StringTokenizer(text);
		for(int i = 0; i < 8; i++) {
			if(st.hasMoreTokens()) {
				if(i == 0) {
					minInstances = Integer.parseInt(st.nextToken());
				}
				else if(i == 1) {
					cpuTotalMin = Integer.parseInt(st.nextToken());					
				}
				else if(i == 2) {
					memTotalMin = Integer.parseInt(st.nextToken());	
				}
				else if(i == 3) {
					cpuTotalMax = Integer.parseInt(st.nextToken());	
				}
				else if(i == 4) {
					memTotalMax = Integer.parseInt(st.nextToken());	
				}
				else if(i == 5) {
					singleAppLoad = Integer.parseInt(st.nextToken());	
				}
				else if(i == 6) {
					individualCpuLoad = Float.parseFloat(st.nextToken());	
				}
				else if(i == 7) {
					individualMemLoad = Float.parseFloat(st.nextToken());	
				}
			}
		}
		return;
	}
	private static String checkSingleApps(Backend backendLoad) {
		HashMap<String, Float> ApplicationAvgRTT = new HashMap<String, Float> ();
		float totalRttTime = 0;
		float mostRtt = 0;
		String highestLoadApp = null;
		
		if(backendLoad.getDeployedApps().size() != 0) {
			for(App app : backendLoad.getDeployedApps()){
				AppStatus status = app.getLastStatus();
				
				if(status == null)
					continue;
				
				System.out.println("**********************************");
				System.out.println(app.getAppName() + ": " + status.getAvgRoundTripTime());
				System.out.println("**********************************");
				if(ApplicationAvgRTT.containsKey(app.getAppName())) {
					Float rtt = ApplicationAvgRTT.get(app.getAppName());
					rtt += status.getAvgRoundTripTime();
					
					ApplicationAvgRTT.put(app.getAppName(), rtt);
					totalRttTime += status.getAvgRoundTripTime();
				}				
				else {
					ApplicationAvgRTT.put(app.getAppName(), status.getAvgRoundTripTime());
					totalRttTime += status.getAvgRoundTripTime();
				}
			}
			
			Set<Map.Entry<String,Float>> valueiterator = ApplicationAvgRTT.entrySet();
			for (Map.Entry<String, Float> thisApp : valueiterator) {
				if(thisApp.getValue() > mostRtt)  {
					highestLoadApp = thisApp.getKey();
					mostRtt = thisApp.getValue();
				}								
			}	
		}
		
		if(ApplicationAvgRTT.size() > 0 && highestLoadApp != null) {
			if(ApplicationAvgRTT.get(highestLoadApp) > singleAppLoad) {
				return highestLoadApp;
			}
			else {
				return null;
			}
		}
		else {
			return null;
		}
		
		/*
		if(backendLoad.getSessionObjectList().size() != 0) {
			for(SessionObject s: backendLoad.getSessionObjectList()) {
				System.out.println("**********************************");
				System.out.println(s.getAppName() + ": " + s.getCpu());
				System.out.println("**********************************");
				if(ApplicationCpu.containsKey(s.getAppName())) {
					Float cputime = ApplicationCpu.get(s.getAppName());
					cputime += s.getCpu();
					ApplicationCpu.put(s.getAppName(), cputime);
					totalCpuTime += s.getCpu();
				}				
				else {
					ApplicationCpu.put(s.getAppName(), s.getCpu());
					totalCpuTime += s.getCpu();
				}
			}
			Set<Map.Entry<String,Float>> valueiterator = ApplicationCpu.entrySet();
			for (Map.Entry<String, Float> thisApp : valueiterator) {
				if(thisApp.getValue() > mostCpu)  {
					highestLoadApp = thisApp.getKey();
					mostCpu = thisApp.getValue();
				}								
			}										
		}
		if(ApplicationCpu.size() > 0) {
			if(ApplicationCpu.get(highestLoadApp) > singleAppLoad * totalCpuTime) {
				return highestLoadApp;
			}
			else {
				return null;
			}
		}
		else {
			return null;
		}
		*/
	}
	/**
	 * Get applications for migration from a backend with too much load. 
	 */
	private static Set<String> getAppsForMigration(Backend backendLoad, Boolean cpuUsage) {
		HashMap<String, Float> ApplicationCpu = new HashMap<String, Float> ();
		//ApplicationCpu.put("test", (float)0);
		float totalCpuTime = 0;
		if(backendLoad.getSessionObjectList().size() != 0) {
			for(SessionObject s: backendLoad.getSessionObjectList()) {
				if(ApplicationCpu.containsKey(s.getAppName())) {
					Float cputime = ApplicationCpu.get(s.getAppName());
					cputime += s.getCpu();
					ApplicationCpu.put(s.getAppName(), cputime);
					totalCpuTime += s.getCpu();
				}				
				else {
					ApplicationCpu.put(s.getAppName(), s.getCpu());
					totalCpuTime += s.getCpu();
				}
			}
			float reduceBy = 0;
			if(cpuUsage) {
				reduceBy = 1- 90 / backendLoad.getCpuRank();
			}
			else {
				reduceBy = 1- 70 / backendLoad.getMemoryRank();
			}
			if(reduceBy > 0) {
				if(ApplicationCpu.size() > 0) {
					//System.out.println("ApplicationCpu.size() "+ ApplicationCpu.size());
					for(float s: ApplicationCpu.values()) {
						System.out.println("ApplicationCpu values: "+s);
					}								
					float current = 0;
					float tempValue;
					Set<String> ValuesCollection = ApplicationCpu.keySet();				
					for(String value: ValuesCollection) {
						tempValue = ApplicationCpu.get(value);
						current += tempValue;
						if(current > reduceBy * totalCpuTime) {
							current -= tempValue;
							ApplicationCpu.remove(value);
						}
					}	
				}
			}
		}
		else {
			return null;
		}
		return ApplicationCpu.keySet();
	}
	/**
	 * Method for migrating applications from one server to another
	 * @param from
	 * @param to
	 * @param migrationAppNumber
	 */
	private static boolean migrateApplications(Backend fromBackend, 
			Vector<Backend> backendsEglibleForMigration, Set<String> migrateAppList) {
		boolean migrated = false;
		if(migrateAppList != null  && migrateAppList.size() > 0 && backendsEglibleForMigration.size() > 0) {

			System.out.print("migration of applications: ");
			for(String s: migrateAppList) {
				System.out.print(s+" ");
			}
			System.out.print(" from: "+fromBackend.getInstanceId());
			System.out.println("");

			for(String s: migrateAppList) {				
				Random randomBackend = new Random();
				boolean contjuni = true;
				int i = 0;
				while(contjuni) {
					System.out.println("number of backends eglible for migration: " +backendsEglibleForMigration.size());
					int randomNumber = randomBackend.nextInt(backendsEglibleForMigration.size());
					try {
						Backend toBackend = backendsEglibleForMigration.get(randomNumber);
						System.out.println("Migrating to random backend: "+randomNumber
								+" which has the name: " +
								toBackend.getInstanceId());

						if(toBackend != fromBackend) {
							if(!toBackend.getDeployedAppNames().contains(s)) {
								toBackend.addApp(s);
								toBackend.deployApp(s);
								fromBackend.setAppUnavailible(s);
								System.out.println("Migrated application "+s);
								contjuni = false;
								migrated = true;
							}
							else if(toBackend.getDeployedAppNames()
									.contains(s)) {
								if(toBackend.getAvailibleAppNames().contains(s)) {
									System.out.println("Backend already has "+s
											+" deployed and availible.");
									migrated = true;
								}
								else {
									toBackend.setAppAvailible(s);
									fromBackend.setAppUnavailible(s);
									System.out.println("Setting application "+s+" availible.");
									migrated = true;
								}
								contjuni = false;								
							}
						}
						else {
							System.out.println("From and To backends are the same, trying again");
							i++;
							if(i < 5) {
								continue;
							}
							else {
								contjuni = false;
								migrated = false;
								System.out.println("No AppServer eglible for migration availible, " +
										"skipping migration from "+fromBackend.getInstanceId()+
								" this interval");
								downscalingInstances.remove(fromBackend);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			/*
			 * reconfiguring haproxy is done by giving all the availible backends 
			 * as a list to the loadbalancercontroller. The problem here is that 
			 * all applications are added from the backends to the backends in haproxy.
			 * That means we need to remake how the system works for migration to work.
			 * We need to have two separate lists. One of all deployed apps on a backend 
			 * and one that lists currently availible apps.		 
			 */
			try {
				loadBalancerController.generateConfiguration(backendController.getBackends());
				loadBalancerController.reconfigure();

			} catch (Exception e) {
				System.out.println("reconfiguration problem");
				e.printStackTrace();
			}

			/*TODO: reconfigure haproxy so that no new requests are sent to backend 
			 * migrateFromThisBackend for the applications in migrateAppList	
			 * 
			 * Randomize which server we migrate to if there are more than one eglible?	 
			 */

		}else{
			migrated = true;
		}
		return migrated;
	}
	private static void checkBackendSessions() {		
		//System.out.println("number of appservers: "+backendController.getBackends().size());
		backendController.acquireLock();
		for(Backend appServer : backendController.getBackends()) {


			HashMap <String, Integer> undeployableApplications = appServer.getSessionTime();
			
			System.out.println(undeployableApplications);
			Set <String> keySet = null;
			boolean concurrent = true;
			while(concurrent) {
				try {
					keySet= undeployableApplications.keySet();
					concurrent = false;
				}
				catch(Exception e) {
					System.out.println("Could not get keySet, retrying in 1 seconds");
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					continue;
				}
			}

			Set <String> deployedApplications = appServer.getDeployedAppNames();
			//System.out.println("number of deployed apps on "+appServer.getInstanceId()+" is "+deployedApplications.size());
			Vector<SessionObject> sO = appServer.getSessionObjectList();
			//remove all applications from the set that has active sessions
			for(SessionObject s : sO) {
				System.out.println("sessionobject name: "+s.getAppName());
				deployedApplications.remove(s.getAppName());
				appServer.resetSessionTime(s.getAppName());
			}
			for(String s : deployedApplications) {
				appServer.addSessionTime(s, (int)iterationTime);
			}		
			/*
			 * Undeploy applications that have been idle for over X seconds.
			 * Currently  X = 200 s
			 */

			Vector<String> removedApplications = new Vector<String>();
			for (String s: keySet) {
				/*
				 * TODO: check what happens if sessions are updated at the same time as they are being checked
				 * 
				 */
				System.out.println("time without requests is "+undeployableApplications.get(s)/1000+"s for application: "+s);
				if(undeployableApplications.get(s)/1000 > 200) {

					try {
						//appServer.deleteApp(s);
						//appServer.removeApp(s);
					} catch (Exception e) {
						System.out.println("Caught exception when removing applications");
						//e.printStackTrace();
					}
					System.out.println("undeploying application "+s+" from AppServer "+appServer.getInstanceId());
					removedApplications.add(s);
					//appServer.getSessionTime().remove(s);
				}
			}
			for(String s: removedApplications) {				
				try {
					appServer.deleteApp(s);
					appServer.removeApp(s);
					appServer.getSessionTime().remove(s);
					try {
						loadBalancerController.generateConfiguration(backendController.getBackends());
						loadBalancerController.reconfigure();

					} catch (Exception e) {
						System.out.println("reconfiguration problem");
						e.printStackTrace();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		backendController.unlock();
		return;
	}
	/**
	 * Algorithm for checking if individual appServers have too much load.
	 * @return
	 */
	private static Vector <Backend> checkIndividualCpuLoad() {		
		Vector <Backend> backendLoadList = new Vector <Backend>();
		backendController.acquireLock();
		BackendStatus bS = null;
		for(Backend backendControl : backendController.getBackends()) {						
			bS = backendControl.getLastStatus();
			if(bS != null) {
				if(bS.getCpuUsage1() > individualCpuLoad || bS.getCpuUsage5() > individualCpuLoad || bS.getCpuUsage10() > individualCpuLoad) {
					backendLoadList.add(backendControl);
				}
			}
			else {
				System.out.println("AppServerController is not activated");
				backendController.unlock();
				return null;
			}
		}
		backendController.unlock();
		return backendLoadList;
	}
	private static Vector <Backend> checkIndividualMemLoad() {	
		Vector <Backend> backendLoadList = new Vector <Backend>();
		backendController.acquireLock();
		BackendStatus bS = null;
		for(Backend backendControl : backendController.getBackends()) {				
			bS = backendControl.getLastStatus();
			if(bS != null) {
				if(bS.getMemUsage() > individualMemLoad) {
					backendLoadList.add(backendControl);
				}
			}
		}
		backendController.unlock();
		return backendLoadList;
	}
	private static void updateRanks(HashMap<String, Float> backendMemRanks, 
			HashMap<String, Float> backendCpuRanks, List<CloudInstance> runningInstances) throws RunNodesException {
		System.out.println("Compounding rank information");

		float avgTotalMemRank = 0;
		float avgBackendMemRank = 0;
		float avgTotalCpuRank = 0;
		float avgBackendCpuRank = 0;
		float avgSystemRoundTripTime = 0;
		float normalizedRoundTripTime = 0;
		float avgBackendRoundTripTime = 0;

		for(Backend backendAvgRTT : backendController.getBackends()) {
			Collection<App> deployedApps = backendAvgRTT.getDeployedApps();
			for(App app : deployedApps) {
				if(app.getLastStatus() != null) {
					AppStatus appS = app.getLastStatus();
					avgBackendRoundTripTime += appS.getAvgRoundTripTime();
				}
			}
			if(deployedApps.size() > 0) {
				avgBackendRoundTripTime /= deployedApps.size();
			}
			avgSystemRoundTripTime += avgBackendRoundTripTime;
		}
		avgSystemRoundTripTime = avgSystemRoundTripTime / 
		backendController.getBackends().size();
		normalizedRoundTripTime = avgSystemRoundTripTime / 1000;
		System.out.println("average system roundtrip time: "+avgSystemRoundTripTime);
		//Memrank
		for (String backendId : backendMemRanks.keySet()) {
			avgBackendMemRank = backendMemRanks.get(backendId);
			avgTotalMemRank += avgBackendMemRank;
			//System.out.println("average backend rank: "+avgBackendMemRank);
		}
		avgTotalMemRank = avgTotalMemRank / backendMemRanks.size();  			
		System.out.println("average Total Memrank: "+avgTotalMemRank);  
		//Cpurank
		for (String backendId : backendCpuRanks.keySet()) {
			avgBackendCpuRank = backendCpuRanks.get(backendId);
			avgTotalCpuRank += avgBackendCpuRank;
			//System.out.println("average backend rank: "+avgBackendMemRank);
		}
		avgTotalCpuRank = avgTotalCpuRank / backendCpuRanks.size();  
		System.out.println("average Total CPU rank: "+avgTotalCpuRank);
		int scalingGracePeriod = Integer.parseInt(options.getProperty("scalingGracePeriod"));
		if ((System.currentTimeMillis() - backendController.getLastAddedBackendTime()) > (scalingGracePeriod * 1000)) {
			System.out.println("able up scale");
			
			// Add new backend if the average is over the threshold
			if (avgTotalMemRank > memTotalMin || avgTotalCpuRank > cpuTotalMin || normalizedRoundTripTime > 3) {
				statusRound++;
				System.out.println("Launching new instance");
				System.out.println("Waiting " + scalingGracePeriod + " seconds before checking again.");

				backendController.acquireLock();
				backendController.addInstances(cloudBuilder.launchInstances(1, groupId));
				backendController.unlock();

			}
		}
		
		
		//System.out.println("checking if a server is close to full hour");
		backendController.acquireLock();
		Vector<Backend> appServersEglibleForDownscaling = new Vector<Backend>();		

		/*
		 * Check if a backend is within 5 minutes of a new billing hour. If not we check
		 * if the load is low enough to host more applications (migrate to).
		 */
		for(Backend currentBackend : backendController.getBackends()) {
			long timeInSeconds = (System.currentTimeMillis() - currentBackend.getStartTimeInMilliseconds())/1000;
			//TODO: if((timeInSeconds % 3600) > 3300) {
			if(currentBackend.isRunning() && currentBackend.isControllerRunning() && (timeInSeconds % 3600) > 60) {	// lower timeout for testing purposes
				appServersEglibleForDownscaling.add(currentBackend);
				System.out.println("adding backend "+currentBackend.getInstanceId()+" " +
						"to serversEglibleForDownscaling, currently there are: "
						+appServersEglibleForDownscaling.size()+" backends in the list");
			}
			if(currentBackend.getCpuRank() < cpuTotalMin && currentBackend.getMemoryRank() < memTotalMin) {
				backendsEglibleForMigration.add(currentBackend);

			}
		}
		backendController.unlock();
		
		
		/* 
		 * Remove server:
		 * How low is too low load? What should the average cpu/mem be 
		 * for us to scale down? Talk to Adnan about this.
		 */
		//System.out.println("number of servers within closing margins: " +appServersEglibleForDownscaling.size());
		if(appServersEglibleForDownscaling.size() > 0 && minInstances < backendController.getBackends().size()) {
			
			if(avgTotalMemRank < memTotalMax && avgTotalCpuRank < cpuTotalMax && minInstances < backendController.getBackends().size()) {
				/* TODO: remove jinwon
				 * 
				 */
				System.out.println("adding "+appServersEglibleForDownscaling.get(0).getInstanceId()+" to downscalingInstances");
				//mark server with lowest load for removal
				boolean isInList = false;
				int downscalingInstanceNumber = appServersEglibleForDownscaling.size();
				System.out.println("downscalingInstanceNumber "+downscalingInstanceNumber);
				for(int j = 0; j < downscalingInstanceNumber; j++) {
					for(Backend backendIsInList: downscalingInstances) {
						if (backendIsInList == appServersEglibleForDownscaling.get(j)) {
							isInList = true;
						}
					}
					if(!isInList) {
						//remove marked server from active serverlist
						runningInstances.remove(appServersEglibleForDownscaling.get(j));
						boolean migrated = migrateApplications(appServersEglibleForDownscaling.get(j), backendsEglibleForMigration, appServersEglibleForDownscaling.get(j).getDeployedAppNames());
						if(migrated) {
							downscalingInstances.add(appServersEglibleForDownscaling.get(j));
							
							System.out.println("Migration successful");
							break;
						}						
					}
					else {
						System.out.println("No backend eglible for migration availible, " + "trying next eglible backend");
					}
				}

				/* 
				 * problems: 
				 * - what to do if a server reaches a full hour without all sessions being removed? 
				 * 		> Currently we close it anyway.
				 * - any limit to how many servers we can have being removed at once?
				 * 		> Currently only one, but can be changed quite easily
				 * - what happens if we are in the middle of shutting down a server and there is a sudden 
				 * spike in activity?
				 * 		> Nothing is being done about this at the moment
				 * 
				 */
			}
			else {
				System.out.println("Not downscaling due to memory: "+avgTotalMemRank+" and cpu: "+avgTotalCpuRank);
			}
		}
		else {
			System.out.println("Mininstances: "+minInstances+" serversEglibleForDownscaling: "+appServersEglibleForDownscaling.size());
		}
		return;
	}
	private static void checkSessions(Vector<Backend> backends) {
		System.out.println("Checking possibility to terminate appServers");
		for(Backend downScalingBackend: downscalingInstances) {		
			boolean noMoreSessions = checkBackendSessions(downScalingBackend);									
			if(noMoreSessions) {
				System.out.println("AppServer "+downScalingBackend.getInstanceId()+" has no sessions and will be terminated");
				backends.add(downScalingBackend);
			}
			/*
			 * If the time until a new hour will be billed is less than 
			 * X seconds the server is shut down with sessions left on it.
			 * 
			 * Currently set to 20 seconds
			 */
			else if(((System.currentTimeMillis() - 
					downScalingBackend.getStartTimeInMilliseconds())
					/1000)% 3600 < 20) {
				backends.add(downScalingBackend);
				System.out.println("AppServer "+downScalingBackend+" " +
				"is close to full billing and will be closed");
			}
		}
		for(Backend b : backends) {
			downscalingInstances.remove(b);
		}
		return;		
	}
	private static void checkLimitations(int activeInstances) throws RunNodesException {
		if (activeInstances < minInstances) {
			int requiredInstances = minInstances - activeInstances;
			System.out.println("backends less than minimum, adding " + requiredInstances);

			// Add another instance
			backendController.acquireLock();
			backendController.addInstances(cloudBuilder.launchInstances(requiredInstances, groupId));
			backendController.unlock();
		}	
		return;		
	}
	private static void checkAppServers(List<CloudInstance> runningInstances) {
		backendController.acquireLock();
		//System.out.println("looking for new application servers");
		
		for (Backend backend : backendController.getBackends()) {
			CloudInstance removeInstance = null;
			for (CloudInstance instance : runningInstances) {
				if (instance.getId().equals(backend.getInstanceId())) {
					removeInstance = instance;
					break;
				}
			}
			if (removeInstance != null) {
				runningInstances.remove(removeInstance);
			}
		}

		if (runningInstances.size() > 0) {
			for (CloudInstance instance : runningInstances) {
				/*if(masterInstances.size() > 0) {
					String data = "/updateMasterIp/"+masterInstances.get(0).getPrivateIpAddress();
					try {		            
						// Send the request
						URL url = new URL("http://"+instance.getPrivateIpAddress()+":5555"+data);
						URLConnection conn = url.openConnection();
						conn.setDoOutput(true);
						OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());

						//write parameters
						writer.write(data);
						writer.flush();

						// Get the response
						StringBuffer answer = new StringBuffer();
						BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
						String line;
						while ((line = reader.readLine()) != null) {
							answer.append(line);
						}
						writer.close();
						reader.close();

						//Output the response
						System.out.println("ANSWER FROM URL: "+answer.toString());

					} catch (MalformedURLException ex) {
						ex.printStackTrace();
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				}*/

			}
			backendController.addInstances(runningInstances);
		}
		backendController.unlock();
		return;		
	}
	
	/* broadcasts the address of arvuemaster to appserver instances
	 * TODO: This code is development code and not supposed to exist, make it so*/
	private static void broadcastMasterAddress() {
		String masterAddress;

		if(masterInstances.size() > 0) {
			masterAddress = masterInstances.get(0).getPrivateIpAddress();
		} else {
			masterAddress = ipAddress;
		}
		
		System.out.println("******************************");
		System.out.println("Master Address: " + masterAddress);
		System.out.println("******************************");
		String data = "/updateMasterIp/" + masterAddress;
		
		Vector<Backend> backendList = backendController.getBackends();
		
		Iterator<Backend> it = backendList.iterator();
		
		while(it.hasNext()) {
			Backend backend = it.next();
			if(masterInstances.contains(backend.getCloudInstance())) {
				it.remove();
			}
		}
		
		for(Backend backend : backendList) {
			try {
				CloudInstance inst = backend.getCloudInstance();
				String instanceAddress = inst.getPrivateIpAddress();
				if(instanceAddress == null) {
					instanceAddress = inst.getPublicDnsName();
				}
				
				System.out.println("Sending master1 address (" + masterAddress + ") to instance (" + instanceAddress + ")");
				// Send the request
				URL url = new URL("http://"+instanceAddress+":" + backend.getControllerPort() + data);
				URLConnection conn = url.openConnection();
				conn.setDoOutput(true);
				OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());

				//write parameters
				writer.write(data);
				writer.flush();

				// Get the response
				StringBuffer answer = new StringBuffer();
				BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String line;
				while ((line = reader.readLine()) != null) {
					answer.append(line);
				}
				writer.close();
				reader.close();

				//Output the response
				System.out.println("ANSWER FROM URL: "+answer.toString());

			} catch (MalformedURLException ex) {
				ex.printStackTrace();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	private static void updateInstancesAndBackends(SyslogServerInterface syslogServer) {
		backendController.acquireLock();
		
		if(backendController.hasBackend())
			backendController.updateInstanceStates(cloudBuilder.getRunningInstances());
		
		// Fetch the latest data (request RTT) from the syslog server and
		// update the backends
		syslogServer.update();
		
		for (Backend backend : backendController.getBackends()) {
			HashMap<String, Float> appRtts = syslogServer.getBackendAvgRtts(backend.getInstanceId());
			if (appRtts != null) {
				backend.updateAppRttTime(appRtts);
			}
		}
		backendController.unlock();
		return;		
	}
	private static void startLoadBalancer() {
		try {
			loadBalancerController.generateConfiguration(backendController
					.getBackends());
			loadBalancerController.startLoadBalancer();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Could not start load balancer");
			return;
		}
		return;
	}
	private static void setupControllers() {
		// Setup loadBalancerController
		String loadBalancerExecutable = options.getProperty("loadBalancerExecutable");
		int loadBalancerListeningPort = Integer.valueOf(options.getProperty("loadBalancerListeningPort"));
		
		loadBalancerController = new HAProxyController(loadBalancerExecutable, loadBalancerListeningPort);

		// Setup backendcontroller
		backendController = new BackendController(options);
		backendController.setLoadBalancerController(loadBalancerController);
		return;		
	}
	private static void setupHandlers() {
		Provisioner p = new Provisioner(options.getProperty("appRepoUrl"), backendController);
		System.out.println("appRepoUrl sent to provisioner: "+options.getProperty("appRepoUrl"));
		p.start();

		// Start the SessionHandlerInterface server
		try {
			int sessionHandlerPort = Integer.parseInt(options.getProperty("sessionPort"));
			System.out.println("Starting session handler on port "+sessionHandlerPort);
			SessionHandlerInterface sessionHandlerInterface = new SessionHandlerInterface(sessionHandlerPort);
			sessionHandlerInterface.setBackendController(backendController);
		} catch (RemoteException e) {

			// If this doesn't work it probably means the port cannot be
			// bound.
			e.printStackTrace();
			return;
		}
		return;
	}
	private static void setupCloudInterface() {
		try {
			//ec2Interface = new EC2Interface(options, maxInstances)
			//TODO 클래스 경로를 프로퍼티로 빼야함
			cloudBuilder = (CloudBuilder) Thread.currentThread().getContextClassLoader().loadClass(options.getProperty("jcloud.driver")).newInstance();
			cloudBuilder.config(options);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private static void parseCommandLine(String[] args) {
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-c")) {
				try {
					System.out.println("Loading properties from: "
							+ args[i + 1]);
					options.load(new FileInputStream(args[i + 1]));
					break;
				} catch (Exception e) {
					System.out.println("Failed to load properties!");
				}
			}
		}
		for (String arg : args) {
			try {
				options.load(new ByteArrayInputStream(arg.getBytes()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		iterationTime = Long.valueOf(options.getProperty("iterationTime")) * 1000;
		minInstances = Integer.valueOf(options.getProperty("minInstances"));
		maxInstances = Integer.valueOf(options.getProperty("maxInstances"));
		// Check application repository AWS credentials
		// Set to same as main credentials if not set
		if (options.getProperty("repoAccessKey").equals("")) {
			options.setProperty("repoAccessKey", options
					.getProperty("accessKey"));
		}
		if (options.getProperty("repoSecretKey").equals("")) {
			options.setProperty("repoSecretKey", options
					.getProperty("secretKey"));
		}
		
		System.out.println("Settings:");
		for (String setting : options.toString().substring(1,
				options.toString().length() - 1).split(", ")) {
			System.out.println(setting);
		}

		return;		
	}
	
	private static void setOptions() {
		options = new Properties();
		options.setProperty("groupId", "garuda");
		options.setProperty("appHistoryExpirationTime", "60");
		options.setProperty("iterationTime", "5");
		options.setProperty("runningBackendsList", "");
		options.setProperty("backendLocalControllerPort", "5555");
		options.setProperty("backendAppPort", "9999");
		options.setProperty("maxInstances", "-1");
		options.setProperty("minInstances", "1");
		options.setProperty("loadBalancerExecutable", "/usr/sbin/haproxy");
		options.setProperty("scalingGracePeriod", "300");
		options.setProperty("appRepoUrl", "localhost:4444");
		options.setProperty("entertainmentServerUrl", "localhost:6666");
		
		options.setProperty("accessKey", "ABCDEFGHIJKLMN");
		options.setProperty("secretKey", "a1b2c3d4e5f6g7h8i9j0");
		options.setProperty("imageId", "ami-46f4c232");
		options.setProperty("keypair", "gsg-keypair");
		
		options.setProperty("ec2InstanceType", "m1.small");
		options.setProperty("ec2EndPoint", "ec2.eu-west-1.amazonaws.com");
		
		options.setProperty("repoAccessKey", "");
		options.setProperty("repoSecretKey", "");
		
		options.setProperty("syslogServerHost", "localhost");
		options.setProperty("syslogServerPort", "2323");
		options.setProperty("sessionPort", "30001");
		options.setProperty("loadBalancerListeningPort", "80");
		options.setProperty("printOutPut", "/tmp/ArvueMasterOutPut");
		options.setProperty("iaasdriver", "org.garuda.garudamaster.DockerInterface");
		try {
			File file = new File("arvue.conf");
			if(file.exists())
				options.load(new FileInputStream("arvue.conf"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return;
	}
	
	private static void removeBackends(Vector<Backend> backendsForRemoval) {
		List<CloudInstance> currentRunningInstances = cloudBuilder.getRunningInstances(groupId);	

		if(currentRunningInstances.size() > minInstances)
		for(Backend removeBackend: backendsForRemoval) {
			System.out.println("Preparing backend "+removeBackend.getInstanceId()+" for removal");
			try {
				System.out.print("there are "+currentRunningInstances.size()+" running instances: ");

				for(CloudInstance in: currentRunningInstances) {
					System.out.print(in.getId()+" ");
					if (in.getId().equals(removeBackend.getInstanceId())) {
						cloudBuilder.teminateInstance(in);
						
						System.out.println("Removing instance: "+in+" (not activated)");
						break;
					}
				}	
				System.out.println("");
			}					
			catch(Exception e) {
				e.printStackTrace();
			}
			backendController.removeBackend(removeBackend.getInstanceId());
			downscalingInstances.remove(removeBackend);
		}
		return;
	}
	/**
	 * Check for sessions on a specific appServer, if the appServer has no sessions
	 * it can be terminated.
	 * @param downScalingBackend
	 * @return
	 */
	private static boolean checkBackendSessions(Backend downScalingBackend) {		
		int i = downScalingBackend.getSessionObjectList().size();
		if(i == 0) {
			return true;
		}
		else {
			return false;
		}		
	}
	/**
	 * Uses an external server to get the public IP address of this machine
	 * 
	 * @return Public IP address
	 */
	public static String getIP() {

		// The EC2 private IP of the EC2 instance can be requested from:
		// http://169.254.169.254/latest/meta-data/local-ipv4
		try {

			URL autoIP = new URL(
			"http://169.254.169.254/latest/meta-data/local-ipv4");
			BufferedReader in = new BufferedReader(new InputStreamReader(autoIP
					.openStream()));
			String ip_address = (in.readLine()).trim();
			
			return ip_address;

		} catch (Exception e) {
			// e.printStackTrace();
		}

		return LOCALHOST;
	}

}
