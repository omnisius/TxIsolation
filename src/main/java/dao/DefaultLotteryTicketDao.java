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


    public static LotteryTicketDao getInstance(){
        if (instance == null) {
            instance = new DefaultLotteryTicketDao();
            return instance;
        } else {
            return instance;
        }
    }

    public void createLotteryTickets(int quantity) {
        PreparedStatement statement = null;
        Connection connection = getConnection();
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
            disconnect(connection, null, statement);
        }
    }

    public LotteryTicket getTicketForBuyer(String buyerId) {
        PreparedStatement preparedStatement = null;
        Connection connection = getConnection();
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            LotteryTicket lotteryTicket = new LotteryTicket();
            connection.setAutoCommit(false);
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM lottery.tickets WHERE buyer is NULL LIMIT 1");
            while (resultSet.next()) {
                lotteryTicket.setNumber(resultSet.getString("number"));
                lotteryTicket.setBuyerId(buyerId);
            }
            statement.close();
            preparedStatement = connection.prepareStatement("UPDATE lottery.tickets SET buyer=? WHERE buyer is NULL LIMIT 1");
            preparedStatement.setString(1, buyerId);
            preparedStatement.executeUpdate();
            connection.commit();
            return lotteryTicket;
        } catch (SQLException e) {
            LOG.error(e);
            throw new RuntimeException();
        } finally {
            disconnect(connection, resultSet, preparedStatement);
        }
    }

    public List<LotteryTicket> getAllTickets() {
        List<LotteryTicket> lotteryTickets = new LinkedList<>();
        Connection connection = getConnection();
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
        Connection connection = getConnection();
        try {
            statement = connection.createStatement();
            statement.executeUpdate("DELETE FROM lottery.tickets");
        } catch (SQLException e) {
            LOG.error(e);
        } finally {
            disconnect(connection, null, statement);
        }
    }

    @Override
    public boolean areThereTicketsToBuy() {
        Connection connection = getConnection();
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
            disconnect(connection, resultSet, statement);
        }
    }

    private void disconnect(Connection connection, ResultSet resultSet, Statement statement) {
        ConnectionManager.getInstance().disconnect(connection, resultSet, statement);
    }

    private Connection getConnection() {
        return ConnectionManager.getInstance().getConnection();
    }
}
