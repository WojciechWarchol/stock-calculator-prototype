package com.wojto.calculations;

import com.wojto.model.Stock;
import com.wojto.model.Transaction;

import java.util.ArrayList;
import java.util.List;

public class StockPortoflio {

    List<StockCalculator> stockCalculators = new ArrayList<>();

    public StockPortoflio(List<StockCalculator> stockCalculators) {
        this.stockCalculators = stockCalculators;
    }

    public StockPortoflio() {}

    public void addTransaction(Transaction transaction) {
        boolean portfolioContainsStockForTransaction = false;
        for (StockCalculator stockCalculator : stockCalculators) {
            // TODO Add other stocks in future
            GpwStockCalculator gpwStockCalculator = (GpwStockCalculator) stockCalculator;
            if (gpwStockCalculator.getStock().getStockName().equals(transaction.getStockSymbol())) {
                ((GpwStockCalculator) stockCalculator).getStock().addTransaction(transaction);
                portfolioContainsStockForTransaction = true;
            }
        }

        if (!portfolioContainsStockForTransaction) {
            Stock newStock = new Stock(transaction);
            GpwStockCalculator newGpwStockCalculator = new GpwStockCalculator(newStock);
            stockCalculators.add(newGpwStockCalculator);
        }
    }

    public void printPortfolioPerformance() {
        for (StockCalculator stockCalculator : stockCalculators) {
            stockCalculator.calculate();
            ((GpwStockCalculator) stockCalculator).printPerformanceOfStock();
        }
    }

    public StockCalculator getStockFromSymbol(String string) {
        for (StockCalculator stockCalculator : stockCalculators) {
            GpwStockCalculator gpwStockCalculator = (GpwStockCalculator) stockCalculator;
            if (gpwStockCalculator.getStock().getStockName() == string) {
                return gpwStockCalculator;
            }
        }

        return null;
    }

    public StockCalculator getStockFromTransaction(Transaction transaction) {
        for (StockCalculator stockCalculator : stockCalculators) {
            GpwStockCalculator gpwStockCalculator = (GpwStockCalculator) stockCalculator;
            if (gpwStockCalculator.getStock().getStockName().equals(transaction.getStockSymbol())) {
                return gpwStockCalculator;
            }
        }

        return null;
    }

    public List<? extends StockCalculator> getStockCalculators() {
        return stockCalculators;
    }

    public void setStockCalculators(List<StockCalculator> stockCalculators) {
        this.stockCalculators = stockCalculators;
    }
}
