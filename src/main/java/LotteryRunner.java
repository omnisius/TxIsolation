import model.LotteryTicket;
import org.apache.log4j.Logger;
import service.DefaultLotteryTicketService;
import service.LotteryTicketService;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LotteryRunner {
    private static final int TICKETS_QUANTITY = 100;
    private static final int BUYERS_QUANTITY = 50;
    private static final Logger LOG = Logger.getLogger(LotteryRunner.class);

    public static void main(String[] args) {
        getLotteryService().deletePreviousLotteryResults();
        getLotteryService().createTickets(TICKETS_QUANTITY);

        final ExecutorService lottery = Executors.newFixedThreadPool(BUYERS_QUANTITY);
        ConcurrentHashMap<String, String> lotteryTicketsMap = new ConcurrentHashMap<>();
        for (int i = 0; i <= BUYERS_QUANTITY; i++) {
            int buyerNumber = i;
            lottery.execute(() -> {
                while(getLotteryService().areThereTicketsToBuy()) {
                    LotteryTicket ticket = getLotteryService().buy("Buyer-" + buyerNumber);
                    lotteryTicketsMap.put(ticket.getNumber(), ticket.getBuyerId());
                }
            } );
        }
        List<LotteryTicket> ticketsFromDb = getLotteryService().getAllTicketsFromDB();
        for (LotteryTicket ticket : ticketsFromDb) {
            if (!lotteryTicketsMap.get(ticket.getNumber()).equals(ticket.getBuyerId())) {
                LOG.info("Difference is for ticket " + ticket.getNumber());
            }
        }
    }

    private static LotteryTicketService getLotteryService() {
        return DefaultLotteryTicketService.getInstance();
    }
}
