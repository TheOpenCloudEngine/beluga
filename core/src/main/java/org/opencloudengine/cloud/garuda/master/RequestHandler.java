package org.opencloudengine.cloud.garuda.master;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.URL;
import java.util.regex.Pattern;

/**
 * Class that recieves requests from provisioner and validates 
 * the requests for existing applications and that the string is 
 * valid in the first place before redirecting it to the correct location. 
 *
 */
public class RequestHandler extends Thread {
	private Socket socket = null;
	//private String repositoryURL = "";
	private String eServerAddress = null;
	private BackendController backendController = null;
	private Pattern pattern = Pattern.compile("/(\\w+)");
	//private String format = ".jar";
	//private String prefix = "com.arvue.";
	
	private static AbstractAppRepositoryInterface appRepository = null;

	public RequestHandler(Socket socket, String eServerAddress) {
		this.socket = socket;
		this.eServerAddress = eServerAddress;
		//this.repositoryURL = repositoryURL;
	}
	
	public void setAppRepositoryInterface(AbstractAppRepositoryInterface appRepository) {
		RequestHandler.appRepository = appRepository;
	}
	
	public void setBackendController(BackendController backendController) {
		this.backendController = backendController;
	}
	
	/**
	 * (non-Javadoc)
	 * @see Thread#run()
	 * 
	 * The requests are read and processed. If an application is 
	 * accesssed that is not deployed the application is deployed and 
	 * the request (and all requests following until the application 
	 * is deployed) to the entertainment server.
	 */
	@Override
	public void run() {
		PrintStream out = null;				
		// Parse request
		String line ="";
		String appName = null;
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintStream(socket.getOutputStream());
			line = in.readLine();
			while (line.length() > 0) {
				// Get app name
				if (line.startsWith("GET")) {
					/*
					Matcher matcher = pattern.matcher(line.split("\\s+")[1]);
					matcher.find();
					appName = matcher.group(1);*/
					System.out.println("RequestHandler line: " + line);
					appName = line.split("\\s+")[1];
					appName = appName.substring(1);

				}
				line = in.readLine();
			}
		}
		catch(IOException e) {
			e.printStackTrace();
		}	
		catch(NullPointerException e) {
			return;
		}
		System.out.println("Received request for app: " + appName);

		
		if(appRepository.appExists(appName)) {			

			System.out.println("app exists!");
			String response = "";											
			try {
				URL entertainmentServer;
				if(!eServerAddress.startsWith("http")) {
					entertainmentServer = new URL("http://" + eServerAddress + "/" + appName);
					System.out.println("redirecting to: "+entertainmentServer);
				}
				else {
					entertainmentServer = new URL(eServerAddress + "/" + appName);
				}
				//URL entertainmentServer = new URL("http://"
				//		+ eServerAddress + "/" + appName);
				BufferedReader reader = new BufferedReader(new InputStreamReader(entertainmentServer.openConnection().getInputStream()));
				while ((line = reader.readLine()) != null) {
					response += line;
				}
				System.out.println("response from entertainment: "+response);
				reader.close();
			} catch (Exception e) {
				System.out.println("could not connect to entertainment server at: " + eServerAddress);
			}
			// Tell ArvueMaster to deploy app
			try {
				if (backendController != null) {
					backendController.acquireLock();
					backendController.deployApp(appName);
					backendController.unlock();
					
					out.println("HTTP/1.0 200 OK");
					out.println("Content-type: " + "text/html" + "\n\n");
					out.println(response);
					socket.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}			
		}		
		//The application requested does not exist, return error page
		else {
			System.out.println("The requested application does not exist");
			out.println("HTTP/1.0 404 OK");
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
