package org.opencloudengine.garuda.action.task;

import org.junit.Test;

import java.util.Random;

/**
 * Created by swsong on 2015. 8. 5..
 */
public class TaskTest {

    @Test
    public void testTask() {
        Task task = new Task("long sum");
        final int timeToRest = 100;
        final int SIZE = 100;
        final Random r = new Random();

        task.addTodo(new Todo() {
            @Override
            public Object doing() throws Exception {
                for(int i = 0; i < SIZE; i++) {
                    System.out.println(String.format("[%d / %d] %d", sequence(), taskSize(), i));
                    Thread.sleep(r.nextInt(timeToRest));
                }
                return null;
            }
        });
        task.addTodo(new Todo() {
            @Override
            public Object doing() throws Exception {
                for(int i = SIZE; i < SIZE * 2; i++) {
                    System.out.println(String.format("[%d / %d] %d", sequence(), taskSize(), i));
                    Thread.sleep(r.nextInt(timeToRest));
                }
                return null;
            }
        });
        task.addTodo(new Todo() {
            @Override
            public Object doing() throws Exception {
                for(int i = SIZE * 2; i < SIZE * 3; i++) {
                    System.out.println(String.format("[%d / %d] %d", sequence(), taskSize(), i));
                    Thread.sleep(r.nextInt(timeToRest));
                }
                return null;
            }
        });

        task.start();

        TaskResult result = null;
        try {
            result = task.waitAndGetResult();
        } catch (TaskException e) {
            e.printStackTrace();
        }

        System.out.println(result.toString());
    }
}
