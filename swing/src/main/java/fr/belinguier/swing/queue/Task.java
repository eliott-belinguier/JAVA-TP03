package fr.belinguier.swing.queue;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class Task<T> {

    private final TaskQueue taskQueue;
    private final Callable<T> callable;
    private long timeout;

    protected Task(final TaskQueue taskQueue, final Callable<T> callable) {
        this.taskQueue = taskQueue;
        this.callable = callable;
        this.timeout = 5000;
    }

    public T complete() throws Exception {
        return this.callable != null ? this.callable.call() : null;
    }

    public void queue(final TaskConsumer<T> consumer, final Consumer<Throwable> exceptionConsumer) {
        final Future<?> future = this.taskQueue.executorService.submit(() -> {
            final T value;

            try {
                value = Task.this.callable.call();
                if (consumer != null)
                    consumer.accept(value);
            } catch (final Throwable exception) {
                if (exceptionConsumer != null)
                    exceptionConsumer.accept(exception);
            }
        });
        if (this.timeout > 0) {
            this.taskQueue.executorTimeOutService.schedule(() -> {
                if (!future.isDone())
                    future.cancel(true);
            }, this.timeout, TimeUnit.MILLISECONDS);
        }
    }

    public long getTimeout() {
        return this.timeout;
    }

    public Task<T> setTimeout(final long timeout) {
        this.timeout = timeout;
        return this;
    }

}
