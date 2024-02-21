package fr.belinguier.swing.queue;

import java.util.function.Consumer;

@FunctionalInterface
public interface TaskConsumer<T> {

    public void accept(final T t) throws Throwable;

    default TaskConsumer<T> andThen(Consumer<? super T> after) {
        if (after == null)
            return this;
        return (T t) -> { accept(t); after.accept(t); };
    }

}
