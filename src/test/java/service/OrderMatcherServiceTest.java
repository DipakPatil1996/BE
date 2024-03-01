package service;

import com.vega.trading.system.enumeration.OrderStatus;
import com.vega.trading.system.enumeration.OrderType;
import com.vega.trading.system.model.Order;
import com.vega.trading.system.service.OrderMatcherService;
import com.vega.trading.system.service.TradingSystem;
import java.math.BigDecimal;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class OrderMatcherServiceTest {

  private OrderMatcherService orderMatcherService;

  private TradingSystem tradingSystem;

  @BeforeEach
  public void setUp() {
    tradingSystem = new TradingSystem();
    orderMatcherService = new OrderMatcherService(tradingSystem);
  }

  @Test
  public void matchOrders_ShouldMatchAndExecuteTrades() {
    Order buyOrder = new Order(1, OrderType.BUY, BigDecimal.valueOf(100.0), 10, "AAPL");
    Order sellOrder = new Order(2, OrderType.SELL, BigDecimal.valueOf(100), 10, "AAPL");

    tradingSystem.getOrderBook().put(buyOrder.getOrderId(), buyOrder);
    tradingSystem.getOrderBook().put(sellOrder.getOrderId(), sellOrder);

    orderMatcherService.matchOrders();

    Assertions.assertEquals(OrderStatus.EXECUTED,tradingSystem.getOrderBook().get(buyOrder.getOrderId()).getOrderStatus());
    Assertions.assertEquals(OrderStatus.EXECUTED,tradingSystem.getOrderBook().get(sellOrder.getOrderId()).getOrderStatus());

  }


}
