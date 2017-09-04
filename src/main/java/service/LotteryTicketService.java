package service;

import model.LotteryTicket;

public interface LotteryTicketService {

    LotteryTicket buy(String buyerId);

}