package fr.belinguier.swing.queue;

import java.util.concurrent.*;


public class TaskQueue {

    protected final ExecutorService executorService;
    protected final ScheduledExecutorService executorTimeOutService;

    public TaskQueue(final int nThreads, final ThreadFactory threadFactory) {
        this.executorService = Executors.newFixedThreadPool(nThreads, threadFactory);
        this.executorTimeOutService = Executors.newSingleThreadScheduledExecutor(threadFactory);
    }

    public TaskQueue(final int nThreads) {
        this(nThreads, Executors.defaultThreadFactory());
    }

    public <T> Task<T> createTask(final Callable<T> callable) {
        return new Task<T>(this, callable);
    }

}
