package manager;

import org.apache.log4j.Logger;

import java.sql.*;

public class ConnectionManager {
    private static final Logger LOG = Logger.getLogger(ConnectionManager.class);
    private static ConnectionManager instance;
    private ConnectionPool connectionPool = new ConnectionPool();

    public static ConnectionManager getInstance(){
        if (instance == null) {
            instance = new ConnectionManager();
            return instance;
        } else {
            return instance;
        }
    }

    public Connection getConnection(){
        try {
            return connectionPool.getConnection();
        } catch (SQLException e) {
            LOG.error(e);
            throw new RuntimeException();
        }
    }

    public void disconnect(Connection connection, ResultSet resultSet, Statement statement) {
        try {
            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (Exception e) {
            LOG.error(e);
        }
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }
}
