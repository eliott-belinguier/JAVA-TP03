package fr.belinguier.swing.db;

import fr.belinguier.swing.queue.TaskQueue;
import fr.belinguier.swing.queue.Task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.Objects;

public class DataBase {


    @FunctionalInterface
    public static interface SQLConsumer<T> {

        public void accept(final T t) throws SQLException;

        default SQLConsumer<T> andThen(SQLConsumer<? super T> after) {
            Objects.requireNonNull(after);
            return (T t) -> { accept(t); after.accept(t); };
        }

    }

    @FunctionalInterface
    public static interface BiSQLConsumer<T, U> {

        public void accept(final T t, final U u) throws SQLException;

        default BiSQLConsumer<T, U> andThen(BiSQLConsumer<? super T, ? super U> after) {
            Objects.requireNonNull(after);

            return (l, r) -> {
                accept(l, r);
                after.accept(l, r);
            };
        }

    }

    protected final TaskQueue taskQueue;
    private final String driver;
    private final String url;
    private final String user;
    private final String password;

    private Connection connection;

    public DataBase(final TaskQueue taskQueue, final String driver, final String url, final String user, final String password) {
        if (taskQueue == null)
            throw new IllegalArgumentException(new NullPointerException("Database taskQueue must be not null."));
        if (driver == null)
            throw new IllegalArgumentException(new NullPointerException("Database driver must be not null."));
        if (url == null)
            throw new IllegalArgumentException(new NullPointerException("Database url must be not null."));
        this.taskQueue = taskQueue;
        this.driver = driver;
        this.url = url;
        this.user = user;
        this.password = password;
    }

    public Connection createNewConnection() throws SQLException {
        if (this.connection != null && !this.connection.isClosed())
            this.connection.close();
        return this.connection = DriverManager.getConnection(String.format("jdbc:%s:%s", this.driver, this.url), user, password);
    }

    public Connection getConnection() throws SQLException {
        if (this.connection == null)
            return createNewConnection();
        return this.connection;
    }

    public Task<Boolean> execute(final String sql, final SQLConsumer<PreparedStatement> statementConsumer) {
        if (sql == null)
            return null;
        return this.taskQueue.createTask(() -> {
            final PreparedStatement preparedStatement = DataBase.this.getConnection().prepareStatement(sql);

            statementConsumer.accept(preparedStatement);
            return preparedStatement.execute();
        });
    }

    public Task<Void> executeSQL(final String sql) {
        System.out.println(sql);
        return this.taskQueue.createTask(() -> {
            final Statement statement = DataBase.this.getConnection().createStatement();

            statement.executeUpdate(sql);
            statement.close();
            return null;
        });
    }

    public Task<ResultSet> executeQuery(final String sql, final SQLConsumer<PreparedStatement> statementConsumer) {
        return this.taskQueue.createTask(() -> {
            final PreparedStatement preparedStatement = DataBase.this.getConnection().prepareStatement(sql);

            if (statementConsumer != null)
                statementConsumer.accept(preparedStatement);
            return preparedStatement.executeQuery();
        });
    }

    public Task<Integer> executeUpdate(final String sql, final SQLConsumer<PreparedStatement> statementConsumer) {
        return this.taskQueue.createTask(() -> {
            final PreparedStatement preparedStatement = DataBase.this.getConnection().prepareStatement(sql);

            statementConsumer.accept(preparedStatement);
            return preparedStatement.executeUpdate();
        });
    }

    public Task<Void> executeSQLInputStream(final InputStream inputStream) throws IOException {
        final BufferedReader reader;
        final StringBuilder stringBuilder;
        String line;


        if (inputStream == null)
            throw new NullPointerException("inputStream parameter must be not null.");
        reader = new BufferedReader(new InputStreamReader(inputStream));
        stringBuilder = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
            stringBuilder.append('\n');
        }
        return executeSQL(stringBuilder.toString());
    }

    public void close() throws SQLException {
        this.connection.close();
    }

}
