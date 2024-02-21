package fr.belinguier.swing.db;

import fr.belinguier.swing.queue.TaskQueue;
import fr.belinguier.swing.queue.Task;

import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;

public class SQLiteDataBase extends DataBase {

    private SQLiteDataBase(final TaskQueue taskQueue, final File dbFile) {
        super(taskQueue, "sqlite", dbFile.getPath(), null, null);
    }

    @Override
    public Connection createNewConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException exception) {
            throw new SQLException(exception);
        }
        return super.createNewConnection();
    }

    public static Task<DataBase> createAndSetup(final TaskQueue taskQueue, final InputStream sqlInputStream, final File dataBaseFile) {
        if (sqlInputStream == null)
            throw new NullPointerException("Database sqlInputStream parameter must be not null.");
        if (dataBaseFile == null)
            throw new NullPointerException("Database file parameter must be not null.");
        return taskQueue.createTask(() -> {
            final DataBase dataBase;

            if (!dataBaseFile.exists())
                dataBaseFile.createNewFile();
            dataBase = new SQLiteDataBase(taskQueue, dataBaseFile);
            dataBase.getConnection();
            dataBase.executeSQLInputStream(sqlInputStream).complete();
            return dataBase;
        });
    }

}
