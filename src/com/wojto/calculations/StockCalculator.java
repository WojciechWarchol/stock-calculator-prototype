package com.wojto.calculations;

import com.wojto.model.Stock;

public interface StockCalculator {

    public StockPerformance calculate(Stock stock);
    public PortfolioPerformance calculatePortfolioPerformance(StockPortoflio stockPortoflio);

}
