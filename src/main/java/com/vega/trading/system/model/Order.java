package com.vega.trading.system.model;

import com.vega.trading.system.enumeration.OrderStatus;
import com.vega.trading.system.enumeration.OrderType;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/* This class represents an order in the trading system.
 It is currently a traditional class, but we can refactor it to a record in the future. */
public class Order {

  private final UUID orderId;
  private final int traderId;
  private final OrderType orderType;
  private final String symbol;
  private final BigDecimal price; // Optional
  private int quantity;
  private OrderStatus orderStatus;

  private  List<Order> underlyingOrders;  // List of underlying orders for Composite Orders

  // Constructor for normal orders
  public Order(int traderId, OrderType orderType, BigDecimal price, int quantity,String symbol) {
    this.orderId = UUID.randomUUID();
    this.traderId = traderId;
    this.orderType = orderType;
    this.price = price;
    this.quantity = quantity;
    this.symbol = symbol;
    this.orderStatus = OrderStatus.PLACED;
    this.underlyingOrders = null;
  }
  // Constructor for composite orders
  public Order(UUID orderId, int traderId, String symbol, List<Order> underlyingOrders) {
    this.orderId = orderId;
    this.symbol = symbol;
    this.orderType = OrderType.COMPOSITE;
    this.price = BigDecimal.ZERO;  // Default price for composite orders
    this.quantity = 0;  // Default quantity for composite orders
    this.traderId = traderId;  // Default traderId for composite orders
    this.orderStatus = OrderStatus.PLACED;  // Default order status for composite orders
    this.underlyingOrders = underlyingOrders;
  }

  public UUID getOrderId() {
    return orderId;
  }

  public int getTraderId() {
    return traderId;
  }

  public OrderType getOrderType() {
    return orderType;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public int getQuantity() {
    return quantity;
  }

  public String getSymbol() {return symbol;}

  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }

  public OrderStatus getOrderStatus() {return orderStatus;}

  public void setOrderStatus(OrderStatus orderStatus) {this.orderStatus = orderStatus;}

  public List<Order> getUnderlyingOrders() {
    return underlyingOrders;
  }

  @Override
  public String toString() {
    return "Order{" +
        "orderId=" + orderId +
        ", traderId=" + traderId +
        ", orderType=" + orderType +
        ", symbol='" + symbol + '\'' +
        ", price=" + price +
        ", quantity=" + quantity +
        ", orderStatus=" + orderStatus +
        '}';
  }
}

