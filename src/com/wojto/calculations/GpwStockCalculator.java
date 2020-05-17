package com.wojto.calculations;

import com.wojto.model.Stock;
import com.wojto.model.Transaction;
import com.wojto.model.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class GpwStockCalculator implements StockCalculator {

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

    @Override
    public PortfolioPerformance calculatePortfolioPerformance(StockPortoflio stockPortoflio) {
        PortfolioPerformance portfolioPerformance = new PortfolioPerformance();
        StockPerformance stockPerformance = new StockPerformance();

        for (Stock stock : stockPortoflio.getStockList()) {
            stockPerformance = calculate(stock);
            portfolioPerformance.updatePortfolioResault(stockPerformance.getInvestmenResault());
        }

        return portfolioPerformance;
    }

    //TODO Extract to seperate class, seperate Lacked, Opened and Closed Transactions.
    //TODO Sort by performance
    //TODO Add date of last transaction
    //TODO Sum up (with privisions)

}
