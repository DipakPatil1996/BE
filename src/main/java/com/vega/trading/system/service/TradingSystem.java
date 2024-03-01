package com.vega.trading.system.service;

import com.vega.trading.system.constant.ExceptionMessages;
import com.vega.trading.system.enumeration.OrderStatus;
import com.vega.trading.system.enumeration.OrderType;
import com.vega.trading.system.exception.MaxUnderlyingInstrumentsException;
import com.vega.trading.system.model.FinancialInstrument;
import com.vega.trading.system.model.Order;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TradingSystem {

  // Map to store buy and sell orders by order ID
  public final Map<UUID, Order> orderBook;

  public FinancialInstrumentService financialInstrumentService;

  public OrderMatcherService orderMatcherService;

  public TradingSystem() {
    this.financialInstrumentService = new FinancialInstrumentService();
    this.orderBook = new ConcurrentHashMap<>();
  }

  // Method to add an order to the order book
  public void addOrder(Order order) {
    validateOrder(order);
    if (order.getOrderType() == OrderType.COMPOSITE) {
      // Handle composite order
      handleCompositeOrder(order);
    } else {
      // Handle normal order
      orderBook.put(order.getOrderId(), order);
      log.info("Order added: {}", order);
      CompletableFuture.runAsync(() -> orderMatcherService.matchOrders())
          .thenAcceptAsync(unused -> log.info("Order matching process initiated asynchronously."));
    }
  }

  // Method to handle composite order
  private void handleCompositeOrder(Order compositeOrder) {
    // Extracting individual orders from the composite order and add them to the order book
    List<Order> underlyingOrders = compositeOrder.getUnderlyingOrders();
    if (underlyingOrders.size() > 3) {
      throw new MaxUnderlyingInstrumentsException(
          ExceptionMessages.MAX_UNDERLYING_INSTRUMENTS_EXCEPTION);
    }
    for (Order underlyingOrder : underlyingOrders) {
      orderBook.put(underlyingOrder.getOrderId(), underlyingOrder);
      log.info("Order added: {}", underlyingOrder);
    }
    CompletableFuture.runAsync(() -> orderMatcherService.matchOrders())
        .thenAcceptAsync(unused -> log.info("Order matching process initiated asynchronously."));
  }

  private void validateOrder(Order order) {
    if (order == null || order.getSymbol().isEmpty()) {
      throw new IllegalArgumentException(ExceptionMessages.INVALID_ORDER);
    }
  }

  // Method to cancel an order from the order book
  public void cancelOrder(UUID orderId) {
    validateOrderId(orderId);
    log.info("Cancelling order: {}", orderId);

    Order orderToRemove = orderBook.get(orderId);

    if (orderToRemove == null) {
      log.warn(ExceptionMessages.ORDER_NOT_FOUND, orderId);
      return;
    }

    if (orderToRemove.getOrderType() == OrderType.COMPOSITE) {
      cancelCompositeOrder(orderToRemove);
    } else {
      cancelSingleOrder(orderToRemove);
    }
  }

  // Helper method to cancel a composite order
  private void cancelCompositeOrder(Order compositeOrder) {
    log.info("Cancelling composite order: {}", compositeOrder.getOrderId());
    for (Order underlyingOrder : compositeOrder.getUnderlyingOrders()) {
      cancelSingleOrder(underlyingOrder);
    }
  }

  // Helper method to cancel a single order
  private void cancelSingleOrder(Order order) {
    log.info("Cancelling order: {}", order.getOrderId());
    order.setOrderStatus(OrderStatus.CANCELLED);
    orderBook.remove(order.getOrderId());
  }

  // Utility method to validate order ID
  private void validateOrderId(UUID orderId) {
    if (orderId == null) {
      throw new IllegalArgumentException(ExceptionMessages.INVALID_ORDER_ID);
    }
  }


  // Method to execute a trade between a buy and sell order
  public void executeTrade(Order buyOrder, Order sellOrder) {

    int tradedQuantity = Math.min(buyOrder.getQuantity(), sellOrder.getQuantity());
    BigDecimal tradePrice = getBestAvailableMarketPrice(sellOrder);

    log.info("Trade executed - Buy Order: {}, Sell Order: {}, Quantity: {}, Trade Price: {}",
        new Object[]{buyOrder, sellOrder, tradedQuantity, tradePrice});

    // Update order book
    buyOrder.setQuantity(buyOrder.getQuantity() - tradedQuantity);
    sellOrder.setQuantity(sellOrder.getQuantity() - tradedQuantity);

    // Update order statuses based on remaining quantity
    updateOrderStatus(buyOrder);
    updateOrderStatus(sellOrder);

    // Update financial instruments and composite financial instruments based on the executed trade
    financialInstrumentService.updateFinancialInstruments(buyOrder.getSymbol(), tradePrice);
    financialInstrumentService.updateCompositeFinancialInstruments(buyOrder.getSymbol(),
        tradePrice);


  }

  //method to update order status based on remaining quantity
  private void updateOrderStatus(Order order) {
    if (order.getQuantity() == 0) {
      order.setOrderStatus(OrderStatus.EXECUTED);
    } else {
      order.setOrderStatus(OrderStatus.PARTIALLY_EXECUTED);
    }
  }

  // Utility method to get the best available market price
  public BigDecimal getBestAvailableMarketPrice(Order sellOrder) {
    // If sellOrder price is null, trade will execute based on market price from financial instrument
    if (sellOrder.getPrice() == null) {
      FinancialInstrument financialInstrument = financialInstrumentService.getFinancialInstruments()
          .get(sellOrder.getSymbol());
      if (financialInstrument != null) {
        return financialInstrument.getMarketPrice();
      } else {
        throw new IllegalArgumentException(
            "Financial instrument not found for symbol: " + sellOrder.getSymbol());
      }
    } else {
      return sellOrder.getPrice();
    }
  }

  public Map<UUID, Order> getOrderBook() {
    return orderBook;
  }

  public void setOrderMatcherService(
      OrderMatcherService orderMatcherService) {
    this.orderMatcherService = orderMatcherService;
  }
}
