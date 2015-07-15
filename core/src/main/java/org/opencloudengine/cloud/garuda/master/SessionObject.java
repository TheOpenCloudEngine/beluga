package org.opencloudengine.cloud.garuda.master;

public class SessionObject implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8755797119364138508L;
	private String serverName;
	private String appName;
	private float cpu;
	private float mem;
	public SessionObject(String serverName, String appName, float cpu, float mem) {
		this.setServerName(serverName);
		this.setAppName(appName);
		this.setCpu(cpu);
		this.setMem(mem);
	}
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(256);
		
		sb
			.append("servername: ")
			.append(getServerName())
			.append(" appName: ")
			.append(getAppName())
			.append(" cpu: ")
			.append(getCpu())
			.append(" mem: ")
			.append(getMem());
		
		return sb.toString();
	}
	private void setCpu(float cpu) {
		this.cpu = cpu;
	}
	public float getCpu() {
		return cpu;
	}
	private void setServerName(String serverName) {
		this.serverName = serverName;
	}
	public String getServerName() {
		return serverName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public String getAppName() {
		return appName;
	}
	private void setMem(float mem) {
		this.mem = mem;
	}
	public float getMem() {
		return mem;
	}

}