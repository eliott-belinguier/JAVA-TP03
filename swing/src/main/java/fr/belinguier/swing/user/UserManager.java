package fr.belinguier.swing.user;

import fr.belinguier.swing.Main;
import fr.belinguier.swing.queue.Task;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class UserManager {

    private static User userFromResultSet(final ResultSet resultSet) throws SQLException {
        return new User(resultSet.getLong(1), resultSet.getString(2));
    }

    public static Task<Set<User>> getUser(final String name) {
        return Main.getTaskQueue().createTask(() -> {
            final HashSet<User> users = new HashSet<User>();
            final ResultSet resultSet = Main.getDataBase().executeQuery("SELECT * FROM user WHERE name = ?", preparedStatement -> {
                preparedStatement.setString(1, name);
            }).complete();

            while (resultSet.next()) {
                users.add(userFromResultSet(resultSet));
            }
            return !users.isEmpty() ? users : null;
        });
    }

    public static Task<Boolean> containUser(final String name) {
        return Main.getTaskQueue().createTask(() -> {
            final Set<User> users = getUser(name).complete();

            return users != null && !users.isEmpty();
        });
    }

    public static Task<User> addUser(final String name) {
        return Main.getTaskQueue().createTask(() -> {
            final boolean added = Main.getDataBase().execute("INSERT INTO user (name) VALUES (?) RETURNING id", preparedStatement -> {
                preparedStatement.setString(1, name);
            }).complete();
            if (!added)
                return null;
            final ResultSet resultSet = Main.getDataBase().executeQuery("SELECT LAST_INSERT_ID()", null).complete();
            if (!resultSet.next())
                return null;
            return new User(resultSet.getLong(1), name);
        });
    }

    public static Task<User> addUser(final long id, final String name) {
        return Main.getTaskQueue().createTask(() -> {
            final boolean added = Main.getDataBase().execute("INSERT INTO user (id, name) VALUES (?, ?) RETURNING id", preparedStatement -> {
                preparedStatement.setLong(1, id);
                preparedStatement.setString(2, name);
            }).complete();
            if (!added)
                return null;
            return new User(id, name);
        });
    }

    public static Task<Boolean> deleteUser(final long id) {
        return Main.getDataBase().execute("DELETE FROM user WHERE id = ?", preparedStatement -> {
            preparedStatement.setLong(1, id);
        });
    }

    public static Task<Boolean> deleteUser(final String name) {
        return Main.getDataBase().execute("DELETE FROM user WHERE name = ?", preparedStatement -> {
           preparedStatement.setString(1, name);
        });
    }

}
