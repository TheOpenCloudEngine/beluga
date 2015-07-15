package org.opencloudengine.cloud.garuda.master;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

public class S3BasedAppRepositoryInterface extends
        AbstractAppRepositoryInterface {

    private String repositoryURL;

    @Override
    public boolean appExists(String appName) {

    	if (appName == null) {
			return false;
		}
		URL repository = null;
		try {
			if(!repositoryURL.startsWith("http")) {
				repository = new URL("http://"+repositoryURL);
			}
			else {
				repository = new URL(repositoryURL);
			}
			System.out.println("appName "+ appName);
			System.out.println("repositoryurl "+ "http://"+repositoryURL);
		} catch (MalformedURLException e) {
			//e.printStackTrace();
			System.out.println("Invalid repository URL");
		}
		BufferedReader inStream = null;
		try {
			inStream = new BufferedReader(
					new InputStreamReader(
							repository.openStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		String inputLine, input = null;
		try {
			while ((inputLine = inStream.readLine()) != null)
				input += inputLine;
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		//Find bundle by checking for <Key>appName</Key> tags
		if(input.contains("<Key>"+appName+".jar</Key>")) {
			return true;
		}
		return false;
    }

    @Override
    public Vector<String> getAvailableApps() {
    	// TODO: implement this
    	
        return null;
    }

	@Override
	public void setLocation(Object location) {
		this.repositoryURL = (String) location;
	}

   
}
