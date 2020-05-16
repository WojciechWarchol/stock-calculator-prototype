package com.wojto.calculations;

import com.wojto.model.StateOfPossesion;
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
            ((GpwStockCalculator) stockCalculator).printAbsolutePerformanceOfStock();
        }
    }

    private void sortStocksByStatusAndPerformance() {
        // TODO use streams to do this
        List<GpwStockCalculator> openPositions = new ArrayList<>();
        List<GpwStockCalculator> closedPostions = new ArrayList<>();
        List<GpwStockCalculator> lackingPositons = new ArrayList<>();
        getAllStocksOfThisState(openPositions, StateOfPossesion.OPEN);
        getAllStocksOfThisState(closedPostions, StateOfPossesion.CLOSED);
        getAllStocksOfThisState(lackingPositons, StateOfPossesion.LACKS_PURCHESE);
        List<StockCalculator> sortedList = new ArrayList<>();
        sortedList.add(openPositions);
        sortedList.add(closedPostions);
        sortedList.add(lackingPositons);
    }

    private void getAllStocksOfThisState(List<GpwStockCalculator> list, StateOfPossesion state) {
        for (StockCalculator stockCalculator : stockCalculators) {
            GpwStockCalculator gpwStockCalculator = (GpwStockCalculator) stockCalculator;
            if ( gpwStockCalculator.getStock().getStateOfPossesion() == state) {
                list.add(gpwStockCalculator);
            }
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
