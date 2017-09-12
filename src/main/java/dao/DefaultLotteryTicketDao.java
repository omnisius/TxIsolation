package dao;

import manager.ConnectionManager;
import model.LotteryTicket;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class DefaultLotteryTicketDao implements LotteryTicketDao {
    private static final Logger LOG = Logger.getLogger(DefaultLotteryTicketDao.class);
    private static LotteryTicketDao instance;
    private static Connection connection;


    public static LotteryTicketDao getInstance(){
        if (instance == null) {
            instance = new DefaultLotteryTicketDao();
            connection = getConnection();
            try {
                connection.setAutoCommit(true);
                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            } catch (SQLException e) {
               LOG.error(e);
            }
            return instance;
        } else {
            return instance;
        }
    }

    public void createLotteryTickets(int quantity) {
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement("INSERT INTO lottery.tickets (number, buyer) VALUES (?, NULL)");
            for (int i = 1; i < quantity; i++) {
                statement.setString(1, "OCM-" + i);
                statement.addBatch();
            }
            statement.executeBatch();
        } catch (SQLException e) {
            LOG.error(e);
        } finally {
            disconnect(null, null, statement);
        }
    }

    public LotteryTicket getTicketForBuyer(String buyerId) {
        PreparedStatement preparedStatement = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            LotteryTicket lotteryTicket = new LotteryTicket();
            long id;
            connection.setAutoCommit(false);
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM lottery.tickets WHERE buyer is NULL LIMIT 1 FOR UPDATE");
            if (resultSet.first()) {
                id = resultSet.getLong("id");
                lotteryTicket.setNumber(resultSet.getString("number"));
                lotteryTicket.setBuyerId(buyerId);

                preparedStatement = connection.prepareStatement("UPDATE lottery.tickets SET buyer=? WHERE id = ? and buyer is NULL");
                preparedStatement.setString(1, buyerId);
                preparedStatement.setLong(2, id);
                if (preparedStatement.executeUpdate() == 1) {
                    connection.commit();
                    return lotteryTicket;
                } else {
                    connection.rollback();
                    return null;
                }
            }
            return null;
        } catch (Exception e) {
            LOG.error(e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                LOG.error(e);
            }
            return null;
        } finally {
            try {
                statement.close();
            } catch (SQLException e) {
                LOG.error(e);
            }
            disconnect(null, resultSet, preparedStatement);
        }
    }

    public List<LotteryTicket> getAllTickets() {
        List<LotteryTicket> lotteryTickets = new LinkedList<>();
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM lottery.tickets");
            LotteryTicket lotteryTicket;
            while (resultSet.next()) {
                lotteryTicket = new LotteryTicket();
                lotteryTicket.setNumber(resultSet.getString("number"));
                lotteryTicket.setBuyerId(resultSet.getString("buyer"));
                lotteryTickets.add(lotteryTicket);
            }
        } catch (SQLException e) {
            LOG.error(e);
        } finally {
            disconnect(connection, resultSet, statement);
        }
        LOG.info(lotteryTickets);
        return lotteryTickets;
    }

    @Override
    public void deleteAllTickets() {
        Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.executeUpdate("TRUNCATE TABLE lottery.tickets");
        } catch (SQLException e) {
            LOG.error(e);
        } finally {
            disconnect(null, null, statement);
        }
    }

    @Override
    public boolean areThereTicketsToBuy() {
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM lottery.tickets WHERE buyer is NULL");
            return resultSet.next();
        } catch (SQLException e) {
            LOG.error(e);
            return true;
        } finally {
            disconnect(null, resultSet, statement);
        }
    }

    private void disconnect(Connection connection, ResultSet resultSet, Statement statement) {
        ConnectionManager.getInstance().disconnect(connection, resultSet, statement);
    }

    private static Connection getConnection() {
        return ConnectionManager.getInstance().getConnection();
    }
}
