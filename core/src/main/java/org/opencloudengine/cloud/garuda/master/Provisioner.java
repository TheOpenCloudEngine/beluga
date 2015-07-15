package org.opencloudengine.cloud.garuda.master;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Provisioner that recieves requests from HAProxy. The provisioner 
 * checks if the application that was requested is deployed and exists. 
 * If its not deployed it will be deployed and if it is already deployed 
 * the request is forwarded to correct server.
 * 
 * @author totte
 *
 */
public class Provisioner extends Thread {
	private final static int port = 8080;
	private static ServerSocket socket;
	String entertainmentServerAddress = "localhost:6666";
	String repositoryURL = "";
	private BackendController backendController = null;
	
	public Provisioner(String repositoryURL, BackendController back) {
		this.repositoryURL = repositoryURL;
		backendController = back;
		
	}
	
	public void setEntertainmentServerAddress(String entertainmentServerAddress) {
		this.entertainmentServerAddress = entertainmentServerAddress;
	}
	@Override
	public void run() {
		// Try to open socket
		try {
			socket = new ServerSocket(port);
		} catch (IOException e1) {
			e1.printStackTrace();
		}	
		System.out.println("Starting provisioner on port " + port);
		
		// Create an interface for the app repository
		AbstractAppRepositoryInterface appRepository = new S3BasedAppRepositoryInterface();
		appRepository.setLocation(repositoryURL);

		// Start listening
		try {
			while (true) {
				Socket listen_socket = socket.accept();

                RequestHandler r = new RequestHandler(listen_socket, entertainmentServerAddress);
                r.setAppRepositoryInterface(appRepository);
                r.setBackendController(backendController);                
                r.start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}