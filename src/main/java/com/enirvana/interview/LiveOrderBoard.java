package com.enirvana.interview;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.enirvana.interview.OrderType.BUY;

/**
 * Explanation for the interview:
 * <p>
 * I made an educated guess here that the speed of registering/cancelling orders are as important as sorting and displaying the prices,
 * so use ConcurrentSkipListMap - it has log(n) time cost for almost all the operations, which is a good trade-off based on my above assumption.
 * Furthermore, the total number of buy/sell prices (i.e. "n" in the above) should be small so little impact to the overall performance
 * (unlikely to beyond 1,000 - especially we remove prices if position is 0, which means only prices within a period are kept in the memory)
 * <p>
 * register/cancel operations are not synchronized because lock is expensive.
 * The summary doesn't rely on the sequence of orders, so the target is here is "eventually up-to-date" within a reasonable short period
 */
public class LiveOrderBoard {

    private AtomicInteger currentOrderId = new AtomicInteger(0);
    private ConcurrentHashMap<Integer, Order> orders = new ConcurrentHashMap<>();
    private ConcurrentSkipListMap<Double, Double> buyPrices = new ConcurrentSkipListMap<>();
    private ConcurrentSkipListMap<Double, Double> sellPrices = new ConcurrentSkipListMap<>();

    public Integer registerOrder(Order order) {
        Integer orderId = currentOrderId.getAndIncrement();
        orders.put(orderId, order);
        ConcurrentSkipListMap<Double, Double> prices = order.type().equals(BUY) ? buyPrices : sellPrices;
        prices.compute(order.price(), (price, quantity) -> quantity == null ? order.quantity() : (order.quantity() + quantity));

        return orderId;
    }

    public void cancelOrder(Integer orderId, Order order) {
        if (orders.remove(orderId, order)) {
            ConcurrentSkipListMap<Double, Double> prices = order.type().equals(BUY) ? buyPrices : sellPrices;
            prices.computeIfPresent(order.price(), (price, quantity) -> quantity.equals(order.quantity()) ? null : (quantity - order.quantity()));
        }
    }

    public Summary getSummary() {
        return ImmutableSummary.builder().
                addAllBuyOrders(getPricesInOrder(buyPrices.descendingMap())).
                addAllSellOrders(getPricesInOrder(sellPrices)).
                build();
    }

    private List<SummaryEntry> getPricesInOrder(ConcurrentNavigableMap<Double, Double> prices) {
        return prices.entrySet().stream().map(entry -> ImmutableSummaryEntry.of(entry.getValue(), entry.getKey())).collect(Collectors.toList());
    }
}
