package org.opencloudengine.cloud.garuda.master;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.net.InetAddress;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.util.StringTokenizer;
import java.util.Vector;

public class SessionHandlerInterface extends java.rmi.server.UnicastRemoteObject
implements RMIInterface {
	private BackendController backendController = null;
	private String address;
	private Registry registry;

	public SessionHandlerInterface(int port) throws RemoteException {

		try {
			address = (InetAddress.getLocalHost()).toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			registry = LocateRegistry.createRegistry(port);
			registry.rebind("ArvueMasterRmi", this);

		} catch (RemoteException e) {
			System.out.println("Remote warning");
			e.printStackTrace();

		}
	}
	protected SessionHandlerInterface(int port, RMIClientSocketFactory csf,
			RMIServerSocketFactory ssf) throws RemoteException {
		super(port, csf, ssf);
	}
	/*
	@Override
	public void update(String serverName, ArrayList<SessionObject> appSessionList)
			throws RemoteException {
		System.out.println("Updating sessions for server "+serverName);

        backendController.acquireLock();
		Backend b = backendController.getBackend(serverName);
		if(b != null) {
			b.setSessionObjectList(appSessionList);
		}
		else {
			System.out.println("Updating application session halted. AppServer name didn't match the name provided.");
		}
        backendController.unlock();		
	}
	 */
	public void setBackendController(BackendController backendController) {
		this.backendController = backendController;
	}
	@Override
	public void update(String string) throws RemoteException {		
		String serverName = "";
		String text [] = string.split("\n");
		Vector<SessionObject> appSessionList = new Vector<SessionObject>();
		Vector<SessionObject> removeSessionList = new Vector<SessionObject>();
		SessionObject jObject;		
		JSONObject json = null;
		for (int i = 0; i < text.length; i++) {
			//System.out.println("text"+i+": "+text[i]);
			try {				
				json = (JSONObject)new JSONParser().parse(text[i].toString());
				jObject = new SessionObject(json.get("servername").toString(),
						json.get("appname").toString(), 
						Float.parseFloat(json.get("cpu").toString()),
						Float.parseFloat(json.get("mem").toString()));
				appSessionList.add(jObject);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		for(SessionObject jo: appSessionList) {	
			//TODO: fix so that it checks that we have the correct server name
			/*
			System.out.println("ServerName: "+jo.getServerName());
			System.out.println("appName: "+jo.getAppName());
			System.out.println("cpu: "+jo.getCpu());
			System.out.println("mem: "+jo.getMem());
			*/
			if(!jo.getServerName().equals("")) {
				if(jo.getAppName().startsWith("Vaadin Application ThreadGroup")) {						
					String appName = jo.getAppName().replace("@", " ");
					appName = appName.replace(".", " ");
					appName = appName.replace("Application", " ");
					StringTokenizer st = new StringTokenizer(appName);
					int numberOfTokens = st.countTokens();
					for(int i = 1; i < numberOfTokens; i++) {
						appName = st.nextToken();
						if(i == numberOfTokens - 1) {
							appName = appName.toLowerCase();
							jo.setAppName(appName);
						}
					}						
				}
				else {
					removeSessionList.add(jo);
				}
			}
			if (serverName.equals("") && !jo.getServerName().equals("")) {
				serverName = jo.getServerName();
			}
			else if(!serverName.equals("") && jo.getServerName().equals(serverName)) {				
				//currently do nothing
			}
			else {
				try {
					throw new Exception();
				} catch (Exception e) {
					System.out.println("illegal serverName found");
					e.printStackTrace();
				}
			}	
		}	
		for(SessionObject sO: removeSessionList) {
			appSessionList.remove(sO);			
		}
		System.out.println("SessionObjectList for server "+serverName+" contains "+appSessionList.size()+" sessions.");		
		if(!serverName.equals("")) {
			try {
				backendController.acquireLock();
				boolean update = backendController.getBackend(serverName).
				setSessionObjectList(appSessionList);
				backendController.unlock();
				if(update) {
					System.out.print("Session update for server: "+serverName+" was successful");
				}
				else {
					System.out.print("Session update for server: "+serverName+" failed");
				}
			}
			catch(Exception e) {
				e.printStackTrace();
				System.out.println("ArvueMaster possibly not yet set up completely");
			}
		}

	}
}
