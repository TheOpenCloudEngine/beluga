package org.opencloudengine.cloud.garuda.master;

import java.util.Vector;

public abstract class AbstractAppRepositoryInterface {
	/**
	 * Method that checks if the application that is requested 
	 * actually exists in the repository.
	**/
    public abstract boolean appExists(String appName);
    
    /**
     * 
     * @param location
     */
    public abstract void setLocation(Object location);
    
    /**
     * 
     * @return
     */
    public abstract Vector<String> getAvailableApps();

}
