package com.ivan.my.thread.pool.service.delay;

import org.springframework.stereotype.Service;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.Future;

@Service
public class DelayedScheduler {
    private final DelayQueue<DelayTask> queue;

    public DelayedScheduler() {
        this.queue = new DelayQueue<>();
        this.startExecute();
    }

    public Future schedule(Runnable task, long delayTime) {
        DelayTask newTask = new DelayTask(task, delayTime);
        this.queue.offer(newTask);
        return newTask;
    }

    private void startExecute() {
        Runnable execute = () -> {
            while (true) {
                try {
                    DelayTask task = this.queue.take();
                    task.run();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(execute, "Executor").start();
    }

}
