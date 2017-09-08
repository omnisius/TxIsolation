import model.LotteryTicket;
import org.apache.log4j.Logger;
import service.DefaultLotteryTicketService;
import service.LotteryTicketService;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class LotteryRunner {
    private static final int TICKETS_QUANTITY = 100;
    private static final int BUYERS_QUANTITY = 50;
    private static final Logger LOG = Logger.getLogger(LotteryRunner.class);

    public static void main(String[] args) {
        getLotteryService().deletePreviousLotteryResults();
        getLotteryService().createTickets(TICKETS_QUANTITY);
        ConcurrentHashMap<String, String> lotteryResults = holdLottery();
        processLotteryResults(lotteryResults);
    }

    private static void processLotteryResults(ConcurrentHashMap<String, String> lotteryTicketsMap) {
        List<LotteryTicket> ticketsFromDb = getLotteryService().getAllTicketsFromDB();
        int differenceCounter = 0;
        for (LotteryTicket ticket : ticketsFromDb) {
            if (isThereAnyDifferenceBetweenMapAndDataFromDb(lotteryTicketsMap, ticket)) {
                LOG.info("Difference is for ticket " + ticket.getNumber() + ": from DB " + ticket.getBuyerId() +
                        " and from Map " + lotteryTicketsMap.get(ticket.getNumber()));
                differenceCounter++;
            }
        }
        LOG.warn("The quantity of differ tickets is " + differenceCounter + ". Tickets sold " + lotteryTicketsMap.size());
    }

    private static ConcurrentHashMap<String, String> holdLottery() {
        ConcurrentHashMap<String, String> lotteryTicketsMap = new ConcurrentHashMap<>();
        final ExecutorService lottery = sellTickets(lotteryTicketsMap);
        finishLottery(lottery);
        return lotteryTicketsMap;
    }

    private static ExecutorService sellTickets(ConcurrentHashMap<String, String> lotteryTicketsMap) {
        final ExecutorService lottery = Executors.newFixedThreadPool(BUYERS_QUANTITY);
        for (int i = 0; i <= BUYERS_QUANTITY; i++) {
            int buyerNumber = i;
            lottery.execute(() -> buyTicketsInLottery(lotteryTicketsMap, buyerNumber));
        }
        return lottery;
    }

    private static void finishLottery(ExecutorService lottery) {
        lottery.shutdown();
        try {
            lottery.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            LOG.error(e);
        }
    }

    private static boolean isThereAnyDifferenceBetweenMapAndDataFromDb(ConcurrentHashMap<String, String> lotteryTicketsMap, LotteryTicket ticket) {
        return lotteryTicketsMap.get(ticket.getNumber()) != null && !lotteryTicketsMap.get(ticket.getNumber()).equals(ticket.getBuyerId());
    }

    private static void buyTicketsInLottery(ConcurrentHashMap<String, String> lotteryTicketsMap, int buyerNumber) {
        while(getLotteryService().areThereTicketsToBuy()) {
            LotteryTicket ticket = getLotteryService().buy("Buyer-" + buyerNumber);
            if (ticket != null && ticket.getBuyerId() != null) {
                lotteryTicketsMap.put(ticket.getNumber(), ticket.getBuyerId());
            }
        }
    }

    private static LotteryTicketService getLotteryService() {
        return DefaultLotteryTicketService.getInstance();
    }
}
