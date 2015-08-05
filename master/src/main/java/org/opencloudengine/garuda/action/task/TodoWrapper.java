package org.opencloudengine.garuda.action.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

/**
 * Created by swsong on 2015. 8. 5..
 */
public class TodoWrapper extends Thread {
    private Logger logger = LoggerFactory.getLogger(TodoWrapper.class);

    private Task task;
    private Todo target;
    private TodoResult todoResult;
    private CountDownLatch[] countDownLatchHolder;

    public TodoWrapper(Task task, Todo todo, TodoResult todoResult, CountDownLatch[] countDownLatchHolder) {
        this.task = task;
        this.target = todo;
        this.todoResult = todoResult;
        this.countDownLatchHolder = countDownLatchHolder;
    }

    public Todo getTarget() {
        return target;
    }

    @Override
    public void run() {
        try {
            todoResult.setResult(target.doing());
        } catch (Throwable t) {
            todoResult.setException(t);
            logger.error("error while todo " + task.getName(), t);
        } finally {
            countDownLatchHolder[0].countDown();
            logger.debug("Todo[{} / {}] of Task [{}] Done.", target.sequence(), target.taskSize(), task.getName());
        }
    }

}
