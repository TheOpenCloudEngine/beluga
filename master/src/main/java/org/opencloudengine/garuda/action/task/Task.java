package org.opencloudengine.garuda.action.task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by swsong on 2015. 8. 5..
 */
public class Task {

    private String name;
    private List<TodoWrapper> todoList;
    private TaskResult taskResult;
    private final CountDownLatch[] latchHolder = new CountDownLatch[1];

    public Task(String name) {
        this.name = name;
        todoList = new ArrayList<>();
        taskResult = new TaskResult();
    }

    public void addTodo(Todo todo) {
        TodoResult todoResult = new TodoResult();
        taskResult.addResult(todoResult);
        todoList.add(new TodoWrapper(this, todo, todoResult, latchHolder));
    }

    public void start() {
        latchHolder[0] = new CountDownLatch(todoList.size());
        int sequence = 0;
        for(TodoWrapper todo : todoList) {
            todo.getTarget().set(sequence++, todoList.size());
            todo.start();
        }
    }

    public TaskResult waitAndGetResult() throws TaskException {

        try {
            latchHolder[0].await();
        } catch (InterruptedException ignore) {
        }

        for(TodoResult r : taskResult.getTodoResultList()) {
            Throwable t = r.getException();
            if(t != null) {
                throw new TaskException(t);
            }
        }

        return taskResult.setSuccess();
    }

    @Override
    public String toString(){
        return String.format("[%s] %s", getClass().getSimpleName(), name != null ? name : "");
    }

    public String getName() {
        return name;
    }

    public List<TodoWrapper> getTodoList() {
        return todoList;
    }
}
