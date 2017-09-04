package dao;

import model.LotteryTicket;

import java.util.List;

public interface LotteryTicketDao {

    void create(String ticket);

    LotteryTicket getTicketForBuyer(String buyerId);

    List<LotteryTicket> getAllTickets();
}
