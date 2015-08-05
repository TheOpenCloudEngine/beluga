package org.opencloudengine.garuda.action.task;

/**
 * Created by swsong on 2015. 8. 5..
 */
public abstract class Todo {
    private int sequence;
    private int taskSize;

    public int sequence() {
        return sequence;
    }
    public int taskSize() {
        return taskSize;
    }
    public void set(int sequence, int taskSize) {
        this.sequence = sequence;
        this.taskSize = taskSize;
    }
    public abstract Object doing() throws Exception;
}
