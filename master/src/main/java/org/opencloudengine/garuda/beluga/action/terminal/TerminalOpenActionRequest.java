package org.opencloudengine.garuda.beluga.action.terminal;

import org.opencloudengine.garuda.beluga.action.ActionRequest;
import org.opencloudengine.garuda.beluga.action.RunnableAction;
import org.opencloudengine.garuda.beluga.action.webapp.DeployWebAppAction;
import org.opencloudengine.garuda.beluga.action.webapp.WebAppContextFile;

import java.util.List;
import java.util.Map;

/**
 * Created by swsong on 2015. 8. 6..
 */
public class TerminalOpenActionRequest extends ActionRequest {

    private String clusterId;
    private String image;
    private String container;
    private String host;

    public TerminalOpenActionRequest(String clusterId, String image, String container, String host) {
        this.clusterId = clusterId;
        this.image = image;
        this.container = container;
        this.host = host;
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

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    @Override
    public boolean compareUnique(ActionRequest other) {
        if(other instanceof TerminalOpenActionRequest) {
            return clusterId.equalsIgnoreCase(((TerminalOpenActionRequest) other).clusterId);
        } else {
            return false;
        }
    }

    @Override
    public RunnableAction createAction() {
        return new TerminalOpenAction(this);
    }
}
