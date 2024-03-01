package com.vega.trading.system.model;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class FinancialInstrument {
  private final UUID id;
  private final String symbol;
  private BigDecimal marketPrice;

  public FinancialInstrument(String symbol, BigDecimal marketPrice) {
    this.id = UUID.randomUUID();
    this.symbol = symbol;
    this.marketPrice = marketPrice;
  }

  @Override
  public int hashCode() {
    return Objects.hash(symbol);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;
    FinancialInstrument other = (FinancialInstrument) obj;
    return Objects.equals(symbol, other.symbol);
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

  public void updateMarketPrice(BigDecimal newMarketPrice) {
    this.marketPrice = newMarketPrice;
  }
}

