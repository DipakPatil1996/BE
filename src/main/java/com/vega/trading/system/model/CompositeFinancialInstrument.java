package com.vega.trading.system.model;

import com.vega.trading.system.constant.ExceptionMessages;
import com.vega.trading.system.exception.MaxUnderlyingInstrumentsException;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class CompositeFinancialInstrument {
  private final UUID id;
  private final String symbol;
  private BigDecimal marketPrice;
  private List<FinancialInstrument> underlyingInstruments;

  public CompositeFinancialInstrument(String symbol, List<FinancialInstrument> underlyingInstruments) {
    if (underlyingInstruments.size() > 3) {
      throw new MaxUnderlyingInstrumentsException(ExceptionMessages.MAX_UNDERLYING_INSTRUMENTS_EXCEPTION);
    }
    this.id = UUID.randomUUID();
    this.symbol = symbol;
    this.underlyingInstruments = underlyingInstruments;
    this.marketPrice = calculateMarketPrice();
  }

  public UUID getId() {
    return id;
  }

  public String getSymbol() {
    return symbol;
  }

  public BigDecimal getMarketPrice() {
    return marketPrice;
  }

  public List<FinancialInstrument> getUnderlyingInstruments() {
    return underlyingInstruments;
  }

  public void updateMarketPrice() {
    this.marketPrice = calculateMarketPrice();
  }

  private BigDecimal calculateMarketPrice() {
    // Logic to calculate market price based on underlying instruments
    // This logic may vary based on specific requirements
    // For simplicity,market price is the sum of individual instrument prices
    return underlyingInstruments.stream()
        .map(FinancialInstrument::getMarketPrice)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }
}

