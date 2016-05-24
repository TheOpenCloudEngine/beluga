package org.opencloudengine.garuda.beluga.action.terminal;

import org.opencloudengine.garuda.beluga.action.ActionRequest;
import org.opencloudengine.garuda.beluga.action.RunnableAction;

/**
 * Created by swsong on 2015. 8. 6..
 */
public class TerminalCommitActionRequest extends ActionRequest {

    private String clusterId;
    private String image;
    private String container;
    private String cmd;
    private int port;

    public TerminalCommitActionRequest(String clusterId, String image, String container, String cmd, int port) {
        this.clusterId = clusterId;
        this.image = image;
        this.container = container;
        this.cmd = cmd;
        this.port = port;
    }

    public String getClusterId() {
        return clusterId;
    }

    public void setClusterId(String clusterId) {
        this.clusterId = clusterId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getContainer() {
        return container;
    }

    public void setContainer(String container) {
        this.container = container;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public boolean compareUnique(ActionRequest other) {
        if (other instanceof TerminalCommitActionRequest) {
            return clusterId.equalsIgnoreCase(((TerminalCommitActionRequest) other).clusterId);
        } else {
            return false;
        }
    }

    @Override
    public RunnableAction createAction() {
        return new TerminalCommitAction(this);
    }
}
