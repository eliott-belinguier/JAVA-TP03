package fr.belinguier.swing;

import fr.belinguier.swing.db.DataBase;
import fr.belinguier.swing.db.SQLiteDataBase;
import fr.belinguier.swing.frame.JFAdduser;
import fr.belinguier.swing.queue.TaskQueue;

import java.io.File;
import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

public class Main {

    private static TaskQueue taskQueue;
    private static DataBase dataBase;

    public static void main(String[] args) {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final JFAdduser jfAdduser;

        taskQueue = new TaskQueue(1, Executors.defaultThreadFactory());
        try {
            Main.dataBase = SQLiteDataBase.createAndSetup(taskQueue, Main.class.getResourceAsStream("/create.sql"), new File("local.db")).complete();
        } catch (final Exception exception) {
            exception.printStackTrace();
            return;
        }
        jfAdduser = new JFAdduser(countDownLatch);
        jfAdduser.setVisible(true);
        try {
            countDownLatch.await();
        } catch (final InterruptedException ignored) {}
        try {
            Main.dataBase.close();
        } catch (final SQLException exception) {
            exception.printStackTrace();
        }
    }

    public static DataBase getDataBase() {
        return Main.dataBase;
    }

    public static TaskQueue getTaskQueue() {
        return Main.taskQueue;
    }
}
