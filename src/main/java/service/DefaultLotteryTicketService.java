package service;

import dao.DefaultLotteryTicketDao;
import dao.LotteryTicketDao;
import model.LotteryTicket;

import java.util.List;

public class DefaultLotteryTicketService implements LotteryTicketService {

    private static DefaultLotteryTicketService instance;

    public static LotteryTicketService getInstance(){
        if (instance == null) {
            instance = new DefaultLotteryTicketService();
            return instance;
        } else {
            return instance;
        }
    }

    @Override
    public LotteryTicket buy(String buyerId) {
        return getDao().getTicketForBuyer(buyerId);
    }

    @Override
    public void createTickets(int quantity) {
        getDao().createLotteryTickets(quantity);
    }

    @Override
    public void deletePreviousLotteryResults() {
        getDao().deleteAllTickets();
    }

    @Override
    public List<LotteryTicket> getAllTicketsFromDB() {
        return getDao().getAllTickets();
    }

    private LotteryTicketDao getDao() {
        return DefaultLotteryTicketDao.getInstance();
    }
}
