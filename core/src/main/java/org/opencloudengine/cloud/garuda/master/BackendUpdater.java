package org.opencloudengine.cloud.garuda.master;

public class BackendUpdater extends Thread {

    Backend backend;

    BackendUpdater(Backend backend) {
        this.backend = backend;
    }

    @Override
    public void run() {
        backend.acquireLock();
        backend.updateAppStatus();
        backend.unlock();
    }

}
