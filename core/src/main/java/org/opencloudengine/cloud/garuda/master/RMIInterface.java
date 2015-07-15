package org.opencloudengine.cloud.garuda.master;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIInterface extends Remote {
	//void update(String string, ArrayList<SessionObject> appSessionList) throws RemoteException;
	void update(String string) throws RemoteException;
}
