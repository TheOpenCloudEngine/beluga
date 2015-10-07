package org.opencloudengine.garuda.beluga.action.task;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by swsong on 2015. 8. 5..
 */
public class TaskResult {
    private List<TodoResult> todoResultList;
    private boolean isSuccess;

    public TaskResult() {
        todoResultList = new ArrayList<>();
    }

    public void addResult(TodoResult todoResult) {
        todoResultList.add(todoResult);
    }

    public List<TodoResult> getTodoResultList() {
        return todoResultList;
    }

    public TaskResult setSuccess() {
        this.isSuccess = true;
        return this;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    @Override
    public String toString() {
        return String.format("[%s] size[%d] success[%s]", getClass().getSimpleName(), todoResultList.size(), isSuccess);
    }
}
