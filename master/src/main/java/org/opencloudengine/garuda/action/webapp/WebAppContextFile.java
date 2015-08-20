package org.opencloudengine.garuda.action.webapp;

/**
 * Created by swsong on 2015. 8. 21..
 */
public class WebAppContextFile {

    private String webAppFile;
    private String context;

    public WebAppContextFile() {}
    public WebAppContextFile(String webAppFile, String context) {
        this.webAppFile = webAppFile;
        this.context = context;
    }

    public String getWebAppFile() {
        return webAppFile;
    }

    public void setWebAppFile(String webAppFile) {
        this.webAppFile = webAppFile;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }
}
