package fr.belinguier.swing.db;

import fr.belinguier.swing.queue.Task;
import fr.belinguier.swing.util.function.Runnable;

public interface DataBasePersistable {

    public Task load(final Runnable doneRunnable);
    public void save(final Runnable doneRunnable);

}
