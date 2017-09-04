package service;

import dao.DefaultLotteryTicketDao;
import dao.LotteryTicketDao;
import model.LotteryTicket;

public class DefaultLotteryTicketService implements LotteryTicketService {

    @Override
    public LotteryTicket buy(String buyerId) {
        return getDao().getTicketForBuyer(buyerId);
    }

    private LotteryTicketDao getDao() {
        return DefaultLotteryTicketDao.getInstance();
    }
}
