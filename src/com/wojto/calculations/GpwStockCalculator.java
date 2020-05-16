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

    public GpwStockCalculator() {}

    @Override
    public StockPerformance calculate(Stock stock) {
        // TODO Include provisions
        int quantityOwned = 0;
        BigDecimal presentValue = BigDecimal.ZERO;
        StockPerformance stockPerformance = new StockPerformance();

        for (Transaction transaction : stock.getTransactions()) {
            if(transaction.getTransactionType() == TransactionType.BUY) {
                quantityOwned += transaction.getAmount();
                presentValue = presentValue.add(transaction.getTotalValue());
            } else if(transaction.getTransactionType() == TransactionType.SELL) {
                quantityOwned -= transaction.getAmount();
                if (quantityOwned < 0) {

                } else if (quantityOwned == 0) {
                    presentValue = presentValue.subtract(transaction.getTotalValue());
                    stockPerformance.updateInvestmentResault(presentValue.negate());
                    presentValue = BigDecimal.ZERO;
                } else {
                    presentValue = presentValue.subtract(transaction.getTotalValue());
                }
            }
        }

        stockPerformance.setOpenPositionValue(presentValue);
        stockPerformance.setOpenPositionAmount(quantityOwned);
        return stockPerformance;
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

    public String colorInvestmentResault(BigDecimal investmentResault) {
        String coloredResault = String.format("%10s", investmentResault);
        if (investmentResault.compareTo(BigDecimal.ZERO) > 0) {
            coloredResault = ANSI_GREEN + coloredResault + ANSI_RESET;
        } else {
            coloredResault = ANSI_RED + coloredResault + ANSI_RESET;
        }
        return coloredResault;
    }


}
