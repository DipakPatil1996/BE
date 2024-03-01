package com.vega.trading.system.service;

import com.vega.trading.system.model.CompositeFinancialInstrument;
import com.vega.trading.system.model.FinancialInstrument;
import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FinancialInstrumentService {


  // Map to store financial instruments
  private Map<String, FinancialInstrument> financialInstruments;


  // Map to store composite financial instruments
  private Map<String, CompositeFinancialInstrument> compositeFinancialInstruments;


  public FinancialInstrumentService() {
    this.financialInstruments = new ConcurrentHashMap<>();
    this.compositeFinancialInstruments = new ConcurrentHashMap<>();
  }

  // Method to update composite financial instruments and underlying financial instruments based on the executed trade
  public void updateCompositeFinancialInstruments(String symbol, BigDecimal tradePrice) {
    for (CompositeFinancialInstrument compositeInstrument : compositeFinancialInstruments.values()) {
      if (containsSymbol(compositeInstrument, symbol)) {
        compositeInstrument.getUnderlyingInstruments().forEach(underlying -> {
          if (underlying.getSymbol().equals(symbol)) {
            log.info("Updated market price - Symbol: {}, New Price: {}",
                new Object[]{symbol, tradePrice});
            underlying.updateMarketPrice(tradePrice);
          }
        });
        compositeInstrument.updateMarketPrice();  // Recalculate aggregated market price
        log.info("Recalculated aggregated market price for Composite Instrument: {0}",
            compositeInstrument);
      }
    }
  }

  // Helper method to update financial instruments based on the executed trade
  public void updateFinancialInstruments(String symbol, BigDecimal tradePrice) {
    FinancialInstrument financialInstrument = financialInstruments.get(symbol);
    if (financialInstrument != null) {
      log.info("Updated market price for Financial Instrument - Symbol: {}, New Price: {}",
          new Object[]{symbol, tradePrice});
      financialInstrument.updateMarketPrice(tradePrice);
    }
  }

  // Utility method to check if a composite financial instrument contains a given symbol
  public boolean containsSymbol(CompositeFinancialInstrument compositeInstrument, String symbol) {
    return compositeInstrument.getUnderlyingInstruments().stream()
        .anyMatch(instrument -> instrument.getSymbol().equals(symbol));
  }


  public Map<String, FinancialInstrument> getFinancialInstruments() {
    return financialInstruments;
  }
}
