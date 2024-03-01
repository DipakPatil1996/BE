package com.vega.trading.system.service;

import com.vega.trading.system.enumeration.OrderStatus;
import com.vega.trading.system.enumeration.OrderType;
import com.vega.trading.system.model.Order;
import java.util.Comparator;

public class OrderMatcherService {

  private TradingSystem tradingSystem;

  public OrderMatcherService(TradingSystem tradingSystem) {
    this.tradingSystem = tradingSystem;
  }

  // Method to match buy and sell orders and execute trades
  public void matchOrders() {
    tradingSystem.orderBook.values().stream()
        .filter(order -> order.getOrderType() == OrderType.BUY && (
            order.getOrderStatus() == OrderStatus.PLACED
                || order.getOrderStatus() == OrderStatus.PARTIALLY_EXECUTED))
        .sorted(Comparator.comparing(Order::getPrice, Comparator.nullsLast(Comparator.reverseOrder())))
        .forEach(this::matchAndExecuteTrades);
  }
//50,40,30
 //30,40,50
  private void matchAndExecuteTrades(Order buyOrder) {
    tradingSystem.orderBook.values().stream()
        .filter(sellOrder -> isMatchingSellOrder(buyOrder, sellOrder))
        .sorted(Comparator.comparing(Order::getPrice, Comparator.nullsFirst(Comparator.naturalOrder())))
        .forEach(sellOrder -> tradingSystem.executeTrade(buyOrder, sellOrder));
  }

  private boolean isMatchingSellOrder(Order buyOrder, Order sellOrder) {
    return sellOrder.getOrderType() == OrderType.SELL &&
        (sellOrder.getOrderStatus() == OrderStatus.PLACED ||
            sellOrder.getOrderStatus() == OrderStatus.PARTIALLY_EXECUTED) &&
        isSameFinancialInstrument(buyOrder, sellOrder);
  }


  // Utility method to check if two orders represent the same financial instrument
  private boolean isSameFinancialInstrument(Order order1, Order order2) {
    return order1.getSymbol().equals(order2.getSymbol());
  }


}
