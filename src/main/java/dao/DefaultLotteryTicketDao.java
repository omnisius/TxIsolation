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

    public void create(String buyerId) {
        PreparedStatement statement = null;
        try {
            statement = getConnection().prepareStatement("INSERT INTO lottery.ticket (number, buyer) VALUES (?, NULL)");
            statement.setString(1, buyerId);
            statement.executeUpdate();
        } catch (SQLException e) {
            LOG.error(e);
        } finally {
            disconnect(getConnection(), null, statement);
        }
    }

    public LotteryTicket getTicketForBuyer(String buyerId) {
        PreparedStatement preparedStatement = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            LotteryTicket lotteryTicket = new LotteryTicket();
            Connection connection = getConnection();
            connection.setAutoCommit(false);
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT number FROM lottery.tickets WHERE buyer is NULL LIMIT 1");
            while (resultSet.next()) {
                lotteryTicket.setNumber(resultSet.getCursorName());
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
            disconnect(getConnection(), resultSet, preparedStatement);
        }
    }

    public List<LotteryTicket> getAllTickets() {
        List<LotteryTicket> lotteryTickets = new LinkedList<>();
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = getConnection().createStatement();
            resultSet = statement.executeQuery("SELECT * FROM lottery.ticket");
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
            disconnect(getConnection(), resultSet, statement);
        }
        LOG.info(lotteryTickets);
        return lotteryTickets;
    }

    private void disconnect(Connection connection, ResultSet resultSet, Statement statement) {
        ConnectionManager.getInstance().disconnect(connection, resultSet, statement);
    }

    private Connection getConnection() {
        return ConnectionManager.getInstance().getConnection();
    }
}
