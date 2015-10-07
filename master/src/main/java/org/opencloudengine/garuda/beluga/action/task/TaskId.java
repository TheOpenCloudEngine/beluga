package org.opencloudengine.garuda.beluga.action.task;

/**
 * Created by swsong on 2015. 8. 5..
 */
public class TaskId {

    private String name;

    private TaskId() {}
    protected TaskId(String name) {
        this.name = name;
    }

    @Override
    public String toString(){
        return String.format("[%s] %s", getClass().getSimpleName(), name != null ? name : "");
    }
}
