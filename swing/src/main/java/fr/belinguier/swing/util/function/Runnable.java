package fr.belinguier.swing.util.function;

public interface Runnable extends java.lang.Runnable {

    default Runnable andThen(final java.lang.Runnable after) {
        if (after == null)
            return this;
        return () -> {
            run();
            after.run();
        };
    }

}
