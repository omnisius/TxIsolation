package dao;

import model.LotteryTicket;

import java.util.List;

public interface LotteryTicketDao {

    void createLotteryTickets(int quantity);

    LotteryTicket getTicketForBuyer(String buyerId);

    List<LotteryTicket> getAllTickets();

    void deleteAllTickets();

    boolean areThereTicketsToBuy();
}
