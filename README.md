# Trading System

This project implements a simplified trading system for a financial exchange, capable of handling both individual and composite financial instruments.

## Components

### Financial Instrument

- Represents an individual financial asset.
- Properties: Identifier, Symbol, Market Price.

### Composite Financial Instrument

- Represents a combination of individual financial instruments.
- Properties: Identifier, Symbol, Market Price, List of Underlying Instruments.

### Order

- Represents a buy or sell order within the trading system.
- Properties: Order Identifier, Trader Identifier, Symbol, Order Type (buy or sell), Price, Quantity.

### Trading System

- Core of the trading system, managing instruments and the order book.
- Methods: Add Orders, Cancel Orders, Match Orders, Execute Trades.

### In Scope:

### Normal Order Execution:

- The trading system supports the execution of normal market orders.
Buy and sell orders with specified prices are matched and executed based on the market conditions.

### Basket Orders:

- Basket orders involving up to three financial instruments are supported.
- The system allows the execution of trades involving multiple instruments in a single order.

### Trigger Point Orders (GTT - Good Till Trigger):

- The system handles trigger point orders where execution occurs when a certain condition or trigger is met.

### Market Price Execution:

- If a specific price is not provided in the order, the trade will be executed at the prevailing market price.

### Out of Scope:

### Intraday (MIS) Orders:

- The trading system does not currently support intraday or Margin Intraday Square Off (MIS) orders.

### Trader Fund Management:

- Adding or removing funds from a trader's account is not within the scope of the current implementation. The system assumes that traders have sufficient funds to place orders but does not provide functionality for fund management.

### Trader Watchlist Configuration:

- The system does not include features related to configuring or managing a trader's watchlist. Watchlist management, where traders can customize and monitor specific instruments, is not part of the current scope.

### Trader Holding and Position Management:

- Tracking and managing a trader's holdings and positions over time are not considered in the current implementation. The system focuses on order placement, matching, and execution rather than providing features related to portfolio management.

### Preconfigured Baskets:
- The system does not support preconfigured baskets where traders can directly select predefined or recommended baskets for placing orders. The current implementation assumes that traders configure and place orders for specific financial instruments individually rather than using preconfigured baskets. Traders are responsible for specifying the details of each order independently.

### Assumptions:

### Initial Price of Financial Instruments:

- The current implementation does not consider the initial price of each financial instrument. Prices are updated based on market conditions.

### Market Price Updates:

- To update the market price for each financial instrument, consideration is required for implementing a mechanism based on the latest trade execution. The actual implementation may vary based on specific requirements.

### Match Order Execution Timing:

The current implementation triggers match order execution whenever an addOrder call occurs. Real-time execution timing may vary based on specific requirements and considerations.

### Future enhancement

- Database Integration
- User Authentication and Authorization
- Advanced Order Type
- Notification System
- Integration with External Systems such as payment gateway
- Mobile and Web Interfaces
- Regulatory Compliance
- Risk Assessment

