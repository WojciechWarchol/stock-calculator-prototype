package com.wojto.calculations;

import com.wojto.model.StateOfPossesion;
import com.wojto.model.Stock;
import com.wojto.model.Transaction;
import com.wojto.output.consolePrinting.StockPerformancePrinter;

import java.util.ArrayList;
import java.util.List;

public class StockPortoflio {

    List<Stock> stockList = new ArrayList<>();

    public StockPortoflio(List<Stock> stockList) {
        this.stockList = stockList;
    }

    public StockPortoflio() {}

    public void addTransaction(Transaction transaction) {
        boolean portfolioContainsStockForTransaction = false;
        for (Stock stock : stockList) {
            // TODO Add other stocks in future
            if (stock.getStockName().equals(transaction.getStockSymbol())) {
                stock.addTransaction(transaction);
                portfolioContainsStockForTransaction = true;
            }
        }

        if (!portfolioContainsStockForTransaction) {
            Stock newStock = new Stock(transaction);
            stockList.add(newStock);
        }
    }

    //TODO Add Stock method

    public void printPortfolioPerformance() {
        StockPerformance currentStockPerformance = new StockPerformance();
        PortfolioPerformance currentPortfolioPerformance = new PortfolioPerformance();
        GpwStockCalculator gpwStockCalculator = new GpwStockCalculator();
        for (Stock stock : stockList) {
            currentStockPerformance = gpwStockCalculator.calculate(stock);
            StockPerformancePrinter.printAbsolutePerformanceOfStock(stock, currentStockPerformance);
        }
        currentPortfolioPerformance = gpwStockCalculator.calculatePortfolioPerformance(this);
        StockPerformancePrinter.printPortfolioResault(currentPortfolioPerformance);
    }

    private void sortStocksByStatusAndPerformance() {
        // TODO use streams to do this
        List<Stock> openPositions = new ArrayList<>();
        List<Stock> closedPostions = new ArrayList<>();
        List<Stock> lackingPositons = new ArrayList<>();
        getAllStocksOfThisState(openPositions, StateOfPossesion.OPEN);
        getAllStocksOfThisState(closedPostions, StateOfPossesion.CLOSED);
        getAllStocksOfThisState(lackingPositons, StateOfPossesion.LACKS_PURCHESE);
        List<List<Stock>> sortedList = new ArrayList<>();
        sortedList.add(openPositions);
        sortedList.add(closedPostions);
        sortedList.add(lackingPositons);
    }

    private void getAllStocksOfThisState(List<Stock> list, StateOfPossesion state) {
        for (Stock stock : stockList) {
            if ( stock.getStateOfPossesion() == state) {
                list.add(stock);
            }
        }
    }

    public Stock getStockFromSymbol(String string) {
        for (Stock stock : stockList) {
            if (stock.getStockName() == string) {
                return stock;
            }
        }

        return null;
    }

    public Stock getStockFromTransaction(Transaction transaction) {
        for (Stock stock : stockList) {
            if (stock.getStockName().equals(transaction.getStockSymbol())) {
                return stock;
            }
        }

        return null;
    }

    public List<Stock> getStockList() {
        return stockList;
    }

    public void setStockList(List<Stock> stockList) {
        this.stockList = stockList;
    }
}
