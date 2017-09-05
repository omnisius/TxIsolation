package service;

import model.LotteryTicket;

import java.util.List;

public interface LotteryTicketService {

    LotteryTicket buy(String buyerId);

    void createTickets(int quantity);

    void deletePreviousLotteryResults();

    List<LotteryTicket> getAllTicketsFromDB();

}