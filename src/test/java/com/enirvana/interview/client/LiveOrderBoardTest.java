package com.enirvana.interview.client;

import com.enirvana.interview.*;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LiveOrderBoardTest {

    @Test
    void register_a_buy_and_a_sell() {
        Order buyOrder = ImmutableOrder.builder().userId("test").quantity(100d).price(310.00).type(OrderType.BUY).build();
        Order sellOrder = ImmutableOrder.builder().userId("test").quantity(100d).price(310.00).type(OrderType.SELL).build();
        LiveOrderBoard sut = new LiveOrderBoard();

        sut.registerOrder(buyOrder);
        sut.registerOrder(sellOrder);

        Summary actual = sut.getSummary();
        assertThat(actual.buyOrders()).containsExactly(ImmutableSummaryEntry.of(100d, 310.00));
        assertThat(actual.sellOrders()).containsExactly(ImmutableSummaryEntry.of(100d, 310.00));
    }

    @Test
    void register_two_buys_at_same_price_and_another_buy_at_different_price() {
        Order order1 = ImmutableOrder.builder().userId("test1").quantity(100d).price(310.00).type(OrderType.BUY).build();
        Order order2 = ImmutableOrder.builder().userId("test2").quantity(100d).price(310.00).type(OrderType.BUY).build();
        Order order3 = ImmutableOrder.builder().userId("test3").quantity(100d).price(320.00).type(OrderType.BUY).build();
        LiveOrderBoard sut = new LiveOrderBoard();

        sut.registerOrder(order1);
        sut.registerOrder(order2);
        sut.registerOrder(order3);

        Summary actual = sut.getSummary();
        assertThat(actual.buyOrders()).containsExactly(ImmutableSummaryEntry.of(100d, 320.00), ImmutableSummaryEntry.of(200d, 310.00));
        assertThat(actual.sellOrders()).isEmpty();
    }

    @Test
    void register_two_sells_at_same_price_and_another_sell_at_different_price() {
        Order order1 = ImmutableOrder.builder().userId("test1").quantity(100d).price(310.00).type(OrderType.SELL).build();
        Order order2 = ImmutableOrder.builder().userId("test2").quantity(100d).price(310.00).type(OrderType.SELL).build();
        Order order3 = ImmutableOrder.builder().userId("test3").quantity(100d).price(320.00).type(OrderType.SELL).build();
        LiveOrderBoard sut = new LiveOrderBoard();

        sut.registerOrder(order1);
        sut.registerOrder(order2);
        sut.registerOrder(order3);

        Summary actual = sut.getSummary();
        assertThat(actual.sellOrders()).containsExactly(ImmutableSummaryEntry.of(200d, 310.00), ImmutableSummaryEntry.of(100d, 320.00));
        assertThat(actual.buyOrders()).isEmpty();
    }

    @Test
    void cancel_one_existing_order() {
        Order order = ImmutableOrder.builder().userId("test").quantity(100d).price(310.00).type(OrderType.BUY).build();
        LiveOrderBoard sut = new LiveOrderBoard();

        Integer orderId = sut.registerOrder(order);
        sut.cancelOrder(orderId, order);

        Summary actual = sut.getSummary();
        assertThat(actual.buyOrders()).isEmpty();
        assertThat(actual.sellOrders()).isEmpty();
    }

    @Test
    void cancel_an_order_not_exist() {
        Order order = ImmutableOrder.builder().userId("test").quantity(100d).price(310.00).type(OrderType.BUY).build();
        LiveOrderBoard sut = new LiveOrderBoard();

        Integer orderId = sut.registerOrder(order);
        sut.cancelOrder(orderId + 1, order);

        Summary actual = sut.getSummary();
        assertThat(actual.buyOrders()).containsExactly(ImmutableSummaryEntry.of(100d, 310.00));
        assertThat(actual.sellOrders()).isEmpty();
    }

    @Test
    void cancel_an_order_with_wrong_attributes() {
        Order order1 = ImmutableOrder.builder().userId("test").quantity(100d).price(310.00).type(OrderType.BUY).build();
        Order order2 = ImmutableOrder.builder().userId("test1").quantity(100d).price(310.00).type(OrderType.BUY).build();
        LiveOrderBoard sut = new LiveOrderBoard();

        Integer orderId = sut.registerOrder(order1);
        sut.cancelOrder(orderId, order2);

        Summary actual = sut.getSummary();
        assertThat(actual.buyOrders()).containsExactly(ImmutableSummaryEntry.of(100d, 310.00));
        assertThat(actual.sellOrders()).isEmpty();
    }
}