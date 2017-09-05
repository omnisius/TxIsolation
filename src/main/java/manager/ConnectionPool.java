package manager;

import org.apache.log4j.Logger;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Queue;

class ConnectionPool {
    private Queue<Connection> connectionFreeQueue;
    private Queue<Connection> connectionBusyQueue;

    private static final String COM_MYSQL_JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private static final String DATABASE_CONNECTION_URL = "jdbc:mysql://localhost/lottery?user=root&password=root";

    {
        try {
            Class.forName(COM_MYSQL_JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            Logger.getLogger(ConnectionPool.class.getName());
        }
    }

    ConnectionPool() {
        connectionFreeQueue = new LinkedList<>();
        connectionBusyQueue = new LinkedList<>();
    }

    Connection getConnection() throws SQLException {
        if (connectionBusyQueue.size() < 100000) {
            if (connectionFreeQueue.isEmpty()) {
                Connection connection = DriverManager.getConnection(DATABASE_CONNECTION_URL);
                connectionFreeQueue.add(connection);
            }
            Connection connection = connectionFreeQueue.poll();
            connectionBusyQueue.add(connection);
            return connection;
        } else {
            throw new SQLException("Number of connections exceeded");
        }
    }
}