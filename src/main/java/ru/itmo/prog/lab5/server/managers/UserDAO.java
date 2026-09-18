package ru.itmo.prog.lab5.server.managers;
import ru.itmo.prog.lab5.common.utilites.PasswordHasher;
import ru.itmo.prog.lab5.common.models.User;
import java.sql.*;

/**
 * Data Access Object для работы с таблицей пользователей.
 * Отвечает за регистрацию и аутентификацию пользователей.
 */

public class UserDAO {
    private final DataBaseManager dataBaseManager;

    public UserDAO(DataBaseManager dataBaseManager) {
        this.dataBaseManager = dataBaseManager;
    }

        /**
         * При успехе возвращает пользователя, иначе null, если логин занят.
         */

        public User register(String login, String password) throws SQLException {
            String sql = "INSERT INTO users(login, password_hash) VALUES (?,?) RETURNING id";
            try (Connection c = dataBaseManager.getConnection();
                 PreparedStatement preparedStatement = c.prepareStatement(sql)) {
                preparedStatement.setString(1, login);
                preparedStatement.setString(2, PasswordHasher.hash(password));
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) return new User(login, resultSet.getInt(1), login, null);
                    }
                } catch (SQLException e) {
                    if ("23505".equals(e.getSQLState())) return null;
                    throw e;
                }
                return null;
            }

    /**
     * Аутентификация пользователя по логину и паролю.
     * @param login
     * @param password
     * @return Объект User при успешной аутентификации
     * @throws SQLException
     */

        public User authenticate(String login, String password) throws SQLException {
            String sql = "SELECT id, password_hash FROM users WHERE login = ?";
            try (Connection c = dataBaseManager.getConnection();
            PreparedStatement preparedStatement = c.prepareStatement(sql)) {
                preparedStatement.setString(1, login);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        String storedHash = resultSet.getString("password_hash");
                        if (PasswordHasher.verify(password, storedHash))
                    return new User(login, resultSet.getInt("id"), login, null);
                    }
                }
            }
            return null;
        }
    }


