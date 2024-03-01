package service;

import com.vega.trading.system.constant.ExceptionMessages;
import com.vega.trading.system.enumeration.OrderStatus;
import com.vega.trading.system.enumeration.OrderType;
import com.vega.trading.system.model.FinancialInstrument;
import com.vega.trading.system.model.Order;
import com.vega.trading.system.service.OrderMatcherService;
import com.vega.trading.system.service.TradingSystem;
import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class TradingSystemTest {

  private TradingSystem tradingSystem;

  @BeforeEach
  void setUp() {
    tradingSystem = new TradingSystem();
    tradingSystem.setOrderMatcherService(new OrderMatcherService(tradingSystem));
  }

  @Test
  void addOrder_PositiveCase_AddBuyOrder() {
    Order buyOrder = new Order(123, OrderType.BUY, new BigDecimal("100.00"), 10, "AAPL");

    tradingSystem.addOrder(buyOrder);

    Assertions.assertEquals(1, tradingSystem.getOrderBook().size());
  }

  @Test
  void addOrder_PositiveCase_AddSellOrder() {
    Order sellOrder = new Order(456, OrderType.SELL, new BigDecimal("90.00"), 8, "AAPL");

    tradingSystem.addOrder(sellOrder);

    Assertions.assertEquals(1, tradingSystem.getOrderBook().size());
  }

  @Test
  void addOrder_NegativeCase_InvalidSymbol() {
    Order invalidOrder = new Order(789, OrderType.BUY, new BigDecimal("120.00"), 5, "");

    IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class,
        () ->
            tradingSystem.addOrder(invalidOrder));

    Assertions.assertEquals(ExceptionMessages.INVALID_ORDER, exception.getMessage());
  }

  @Test
  void addOrder_NegativeCase_NullOrder() {

    IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class,
        () ->
            tradingSystem.addOrder(null));

    Assertions.assertEquals(ExceptionMessages.INVALID_ORDER, exception.getMessage());
  }

  @Test
  void cancelOrder_PositiveCase_CancelExistingOrder() {
    Order orderToCancel = new Order(123, OrderType.BUY, new BigDecimal("100.00"), 10, "AAPL");
    tradingSystem.addOrder(orderToCancel);

    tradingSystem.cancelOrder(orderToCancel.getOrderId());

    Assertions.assertEquals(0, tradingSystem.getOrderBook().size());
  }

  @Test
  void addOrder_PartiallyExecutedStatus_Success() throws InterruptedException {

    Order buyOrder = new Order(1, OrderType.BUY, BigDecimal.valueOf(100.0), 5, "AAPL");
    Order sellOrder = new Order(2, OrderType.SELL, BigDecimal.valueOf(100.0), 3, "AAPL");

    CountDownLatch latch = new CountDownLatch(1);

    CompletableFuture.runAsync(() -> {
      tradingSystem.addOrder(buyOrder);
      tradingSystem.addOrder(sellOrder);
      latch.countDown();
    });

    // Wait for asynchronous execution to complete
    latch.await();
    Assertions.assertEquals(OrderStatus.PARTIALLY_EXECUTED, buyOrder.getOrderStatus());
    Assertions.assertEquals(OrderStatus.EXECUTED, sellOrder.getOrderStatus());
  }

  @Test
  void addOrder_ExecutedStatus_Success() throws InterruptedException {
    Order buyOrder = new Order(1, OrderType.BUY, BigDecimal.valueOf(100.0), 5, "AAPL");
    Order sellOrder = new Order(2, OrderType.SELL, BigDecimal.valueOf(100.0), 5, "AAPL");

    tradingSystem.addOrder(buyOrder);
    tradingSystem.addOrder(sellOrder);

    CountDownLatch latch = new CountDownLatch(1);

    CompletableFuture.runAsync(() -> {
      tradingSystem.addOrder(buyOrder);
      tradingSystem.addOrder(sellOrder);
      latch.countDown();
    });

    // Wait for asynchronous execution to complete
    latch.await();

    Assertions.assertEquals(OrderStatus.EXECUTED, sellOrder.getOrderStatus());
    Assertions.assertEquals(OrderStatus.EXECUTED, buyOrder.getOrderStatus());

  }

  @Test
  void getBestAvailableMarketPrice_PriceIsNull_UseMarketValue() throws InterruptedException {
    FinancialInstrument initialInstrument = new FinancialInstrument("AAPL",
        BigDecimal.valueOf(100.0));
    tradingSystem.financialInstrumentService.getFinancialInstruments()
        .put("AAPL", initialInstrument);

    Order buyOrder = new Order(1, OrderType.BUY, null, 10, "AAPL");
    Order sellOrder = new Order(2, OrderType.SELL, null, 10, "AAPL");

    CountDownLatch latch = new CountDownLatch(1);

    CompletableFuture.runAsync(() -> {
      tradingSystem.addOrder(buyOrder);
      tradingSystem.addOrder(sellOrder);
      latch.countDown();
    });

    // Wait for asynchronous execution to complete
    latch.await();
    //  Thread.sleep(20000);
    Assertions.assertEquals(OrderStatus.EXECUTED,
        tradingSystem.getOrderBook().get(buyOrder.getOrderId()).getOrderStatus());
  }

}


