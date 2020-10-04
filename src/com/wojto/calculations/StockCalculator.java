package com.wojto.calculations;

import com.wojto.model.Stock;

import java.time.Year;
import java.util.List;

public interface StockCalculator {

    public StockPerformance calculate(Stock stock, List<Year> taxYears);
    public PortfolioPerformance calculatePortfolioPerformance(StockPortoflio stockPortoflio, List<Year> taxYears);

}
