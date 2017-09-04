package manager;

import org.apache.log4j.Logger;

import java.sql.*;

public class ConnectionManager {
    private static final Logger LOG = Logger.getLogger(ConnectionManager.class);
    private static final String COM_MYSQL_JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private static final String DATABASE_CONNECTION_URL = "jdbc:mysql://localhost/lottery?user=root&password=root";
    private static ConnectionManager instance;
    private Connection connection = null;

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
            Class.forName(COM_MYSQL_JDBC_DRIVER);
            if (connection == null)
                connection = DriverManager.getConnection(DATABASE_CONNECTION_URL);

        } catch (ClassNotFoundException | SQLException e) {
            LOG.error(e);
        }
        return connection;
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
