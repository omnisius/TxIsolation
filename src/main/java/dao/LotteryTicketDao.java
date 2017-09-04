package dao;

import model.LotteryTicket;

import java.util.List;

public interface LotteryTicketDao {

    void create(String ticket);

    void setBuyerId(String buyerId);

    List<LotteryTicket> getAllTickets();
}
