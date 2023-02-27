package com.ivan.my.thread.pool.service.delay;

import java.util.concurrent.Delayed;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

public class DelayTask extends FutureTask implements Delayed {

    private final long startTime;
    private final Runnable task;

    private final int count;

    public DelayTask(Runnable task, long delayTime) {
        super(task, null);  // null is the return value for the runnable tasks
        this.task = task;
        this.startTime = System.currentTimeMillis() + delayTime;
        this.count = 1;
    }

    public DelayTask(Runnable task, long delayTime, int count) {
        super(task, null);  // null is the return value for the runnable tasks
        this.task = task;
        this.startTime = System.currentTimeMillis() + delayTime;
        this.count = count;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        long diff = this.startTime - System.currentTimeMillis();
        return unit.convert(diff, TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
//        return Ints.saturatedCast(
//                this.startTime - ((DelayObject) o).startTime);
        return Long.compare(this.startTime, ((DelayTask) o).startTime);
    }

    public int getCount() {
        return count;
    }
}
