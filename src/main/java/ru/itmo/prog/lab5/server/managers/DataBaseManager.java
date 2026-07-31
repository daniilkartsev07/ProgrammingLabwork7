package ru.itmo.prog.lab5.server.managers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Класс для управления базой данных.
 */

public class DataBaseManager {
    private final String url;
    private final String user;
    private final String password;

    public DataBaseManager(String host, String DataBaseName, String user, String password) {
        this.url = "jdbc:postgresql://" + host + "/" + DataBaseName;
        this.user = user;
        this.password = password;
    }

    /**
     * Новое соединение на каждый вызов.
     * @return
     * @throws SQLException
     */

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}

