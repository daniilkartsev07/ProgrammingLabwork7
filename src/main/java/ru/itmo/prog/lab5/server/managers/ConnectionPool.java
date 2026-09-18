package ru.itmo.prog.lab5.server.managers;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Пул соединений с БД
 * при вызове connection.close соединение возрвращалось в пул, а не закрываоось
 */
public class ConnectionPool {
    private final BlockingQueue<Connection> pool;
    private final String url;
    private final String user;
    private final String password;
    private final int maxSize;

    public ConnectionPool(String host, String databaseName, String user, String password, int maxSize) {
        this.url = "jdbc:postgresql://" + host + "/" + databaseName;
        this.user = user;
        this.password = password;
        this.maxSize = maxSize;
        this.pool = new ArrayBlockingQueue<>(maxSize);

        for (int i = 0; i < maxSize; i++) {
            try {
                pool.put(createRealConnection());
            } catch (SQLException | InterruptedException e) {
                throw new RuntimeException("Не удалось инициализировать пул соединений", e);
            }
        }
    }

    private Connection createRealConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    public Connection getConnection() throws SQLException {
        try {
            Connection realConnection = pool.poll(5, TimeUnit.SECONDS);
            if (realConnection == null) {
                throw new SQLException("Таймаут ожидания свободного времени из пула");
            }
            if (realConnection.isClosed() || !realConnection.isValid(2)) {
                realConnection = createRealConnection();
            }
            return createProxy(realConnection);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SQLException("Ожидание соединения прервано", e);
        }
    }

    /**
     * Создает обертку над реальным соединением. Вызов close() - возвращает соединение в пул.
     * @param realConnection
     * @return
     */
    private Connection createProxy(Connection realConnection) {
        InvocationHandler handler = (proxy, method, args) -> {String methodName = method.getName();

            if ("close".equals(methodName)) {
                pool.offer(realConnection);
                return null;
            }
            if ("isClosed".equals(methodName)) {
                return false;
            }
            if ("isValid".equals(methodName)) {
                return realConnection.isValid((Integer) args[0]);
            }
            return method.invoke(realConnection, args);
        };
        return (Connection) Proxy.newProxyInstance(Connection.class.getClassLoader(), new Class<?>[]{Connection.class}, handler);

    }

    /**
     * Корректно закрывает все соединения при остановке сервера
     */
    public void shutdown() {
        for (Connection connection : pool) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException ignored) {

            }
        }
        pool.clear();
    }
}
