package net.media.autotemplate.bean;

public class TimedTask<T> {
    TimedTaskInterface<T> timedTask;
    private long timeTaken;

    public TimedTask(TimedTaskInterface<T> timedTask) {
        this.timedTask = timedTask;
    }

    public T doTask() throws Exception {
        long startTime = System.currentTimeMillis();
        T retObj = timedTask.task();
        long endTime = System.currentTimeMillis();
        this.timeTaken = (endTime - startTime);
        return retObj;
    }

    public long getTimeTaken() {
        return timeTaken;
    }
}


