package service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.vega.trading.system.constant.ExceptionMessages;
import com.vega.trading.system.exception.MaxUnderlyingInstrumentsException;
import com.vega.trading.system.model.CompositeFinancialInstrument;
import com.vega.trading.system.model.FinancialInstrument;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

public class CompositeFinancialInstrumentTest {

  @Test
  void constructor_PositiveCase() {
    List<FinancialInstrument> underlyingInstruments = new ArrayList<>();
    underlyingInstruments.add(new FinancialInstrument( "StockA", BigDecimal.TEN));
    underlyingInstruments.add(new FinancialInstrument( "StockB", BigDecimal.TEN));

    CompositeFinancialInstrument compositeInstrument = new CompositeFinancialInstrument("Composite", underlyingInstruments);

    assertEquals("Composite", compositeInstrument.getSymbol());
    assertEquals(2, compositeInstrument.getUnderlyingInstruments().size());
    assertEquals(BigDecimal.TEN, compositeInstrument.getUnderlyingInstruments().get(0).getMarketPrice());
    assertEquals(BigDecimal.TEN, compositeInstrument.getUnderlyingInstruments().get(1).getMarketPrice());
  }

  @Test
  void constructor_MaxUnderlyingInstruments() {
    List<FinancialInstrument> underlyingInstruments = new ArrayList<>();
    underlyingInstruments.add(new FinancialInstrument( "StockA", BigDecimal.TEN));
    underlyingInstruments.add(new FinancialInstrument( "StockB", BigDecimal.TEN));
    underlyingInstruments.add(new FinancialInstrument( "StockC", BigDecimal.TEN));
    underlyingInstruments.add(new FinancialInstrument( "StockD", BigDecimal.TEN));

    // Attempting to create a composite instrument with more than 3 underlying instruments
    MaxUnderlyingInstrumentsException exception = assertThrows(MaxUnderlyingInstrumentsException.class, () ->
        new CompositeFinancialInstrument("Composite", underlyingInstruments));

    assertEquals(ExceptionMessages.MAX_UNDERLYING_INSTRUMENTS_EXCEPTION, exception.getMessage());
  }
}
