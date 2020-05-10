package com.wojto.calculations;

import com.wojto.model.Stock;
import com.wojto.model.Transaction;
import com.wojto.model.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class GpwStockCalculator implements StockCalculator {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String FORMAT = "%s%-15s%s%-16s%s%10s%n";

    private Stock stock;
    private StockPerformance stockPerformance;

    public GpwStockCalculator(Stock stock, StockPerformance stockPerformance) {
        this.stock = stock;
        this.stockPerformance = stockPerformance;
    }

    public GpwStockCalculator(Stock stock) {
        this.stock = stock;
        this.stockPerformance = new StockPerformance(stock.getStockName());
    }

    @Override
    public void calculateAbsolute() {
        // TODO Include provisions
        int quantityOwned = 0;
        BigDecimal presentValue = BigDecimal.ZERO;
        // TODO use presentValue, and when quantityOwned == 0, add tu performance. On en this value -> Open positon

        for (Transaction transaction : stock.getTransactions()) {
            if(transaction.getTransactionType() == TransactionType.BUY) {
//                stockPerformance.addPucheseToOpenPosition(transaction.getTotalValue());
                quantityOwned += transaction.getAmount();
                presentValue = presentValue.add(transaction.getTotalValue());
            } else if(transaction.getTransactionType() == TransactionType.SELL) {
                quantityOwned -= transaction.getAmount();
                if (quantityOwned < 0) {

                } else if (quantityOwned == 0) {
                    presentValue = presentValue.subtract(transaction.getTotalValue());
                    stockPerformance.setInvestmenResault(presentValue.negate());
                    presentValue = BigDecimal.ZERO;
                } else {
                    presentValue = presentValue.subtract(transaction.getTotalValue());
                }
            }
        }

        stockPerformance.setOpenPositionValue(presentValue);
        stockPerformance.setOpenPositionAmount(quantityOwned);
    }

    //TODO Extract to seperate class, seperate Lacked, Opened and Closed Transactions.
    //TODO Sort by performance
    //TODO Add date of last transaction
    //TODO Sum up (with privisions)

    public void printAbsolutePerformanceOfStock() {
        System.out.printf(FORMAT, "Stock: ", stock.getStockName(),
                "Status: ", stock.getStateOfPossesion(),
                "Performance: ", colorInvestmentResault(stockPerformance.getInvestmenResault()));
    }

    public LocalDateTime getLastTransactionDate() {
        LocalDateTime lastDate = LocalDateTime.MIN;
        LocalDateTime dateToCheck;
        LocalDateTime lastTransactionDate = null;
        for (Transaction transaction : stock.getTransactions()) {
            dateToCheck = transaction.getTransactionDate();
            lastTransactionDate = dateToCheck.compareTo(lastDate) > 0 ? dateToCheck : lastDate;
            lastDate = dateToCheck;
        }
        return lastTransactionDate;
    }

    public String colorInvestmentResault(BigDecimal investmentResault) {
        String coloredResault = String.format("%10s", investmentResault);
        if (investmentResault.compareTo(BigDecimal.ZERO) > 0) {
            coloredResault = ANSI_GREEN + coloredResault + ANSI_RESET;
        } else {
            coloredResault = ANSI_RED + coloredResault + ANSI_RESET;
        }
        return coloredResault;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public StockPerformance getStockPerformance() {
        return stockPerformance;
    }

    public void setStockPerformance(StockPerformance stockPerformance) {
        this.stockPerformance = stockPerformance;
    }


}
