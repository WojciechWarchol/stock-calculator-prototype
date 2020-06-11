package com.wojto.calculations;

import com.wojto.model.StateOfPossesion;
import com.wojto.model.Stock;
import com.wojto.model.Transaction;
import com.wojto.model.TransactionType;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class GpwStockCalculator implements StockCalculator {

    public GpwStockCalculator() {}

    @Override
    public StockPerformance calculate(Stock stock) {
        // TODO Include provisions
        int quantityOwned = 0;
        BigDecimal closedPositionsBuyValue = BigDecimal.ZERO;
        BigDecimal closedPositionsSellValue = BigDecimal.ZERO;
        BigDecimal presentValue = BigDecimal.ZERO;
        StockPerformance stockPerformance = new StockPerformance();

        if (isClosed(stock)) {
            BigDecimal investmentResault = BigDecimal.ZERO;
            BigDecimal transactionValue = BigDecimal.ZERO;
            TransactionType transactionType;

            for (Transaction transaction : stock.getTransactions()) {
                transactionValue = transaction.getTotalValue();
                transactionType = transaction.getTransactionType();
                if (isPurcheseTransaction(transactionType)) {
                    closedPositionsBuyValue = closedPositionsBuyValue.add(transactionValue);
                    investmentResault = investmentResault.subtract(transactionValue);
                } else if (isSellTransaction(transactionType)) {
                    closedPositionsSellValue = closedPositionsSellValue.add(transactionValue);
                    investmentResault = investmentResault.add(transactionValue);
                }
            }
            stockPerformance.updateInvestmentResault(investmentResault);
        }

//        for (Transaction transaction : stock.getTransactions()) {
//            if(transaction.getTransactionType() == TransactionType.BUY) {
//                quantityOwned += transaction.getAmount();
//                presentValue = presentValue.add(transaction.getTotalValue());
//                closedPositionsBuyValue = closedPositionsBuyValue.add(transaction.getTotalValue());
//            } else if(transaction.getTransactionType() == TransactionType.SELL) {
//                quantityOwned -= transaction.getAmount();
//                if (quantityOwned < 0) {
//                    Long quantityForCalculation = transaction.getAmount() + quantityOwned;
//                    BigDecimal partialValue = new BigDecimal(quantityForCalculation.toString()).multiply(transaction.getPrice());
//                    presentValue = presentValue.subtract(partialValue);
//                    stockPerformance.updateInvestmentResault(presentValue.negate());
//                    presentValue = BigDecimal.ZERO;
//                    quantityOwned = 0;
//                    closedPositionsSellValue = closedPositionsSellValue.add(partialValue);
//                } else if (quantityOwned == 0) {
//                    presentValue = presentValue.subtract(transaction.getTotalValue());
//                    stockPerformance.updateInvestmentResault(presentValue.negate());
//                    presentValue = BigDecimal.ZERO;
//                    closedPositionsSellValue = closedPositionsSellValue.add(transaction.getTotalValue());
//                } else {
//                    presentValue = presentValue.subtract(transaction.getTotalValue());
//                    closedPositionsSellValue = closedPositionsSellValue.add(transaction.getTotalValue());
//                }
//            }
//        }
        //TODO Change logic of summing transactions. Probably aggregate Transactions into POSITIONS. Sort transactions before adding.
        double earnedPercentage;
        if (closedPositionsSellValue.equals(BigDecimal.ZERO)) {
            earnedPercentage = 0.00;
        } else {
            try {
                BigDecimal calcStep = closedPositionsSellValue.multiply(new BigDecimal("100"));
                calcStep = calcStep.divide(closedPositionsBuyValue, 2, RoundingMode.HALF_UP);
                calcStep = calcStep.subtract(new BigDecimal("100"));
                earnedPercentage = calcStep.doubleValue();
            } catch (ArithmeticException e) {
                earnedPercentage = 0.00;
            }
        }

        stockPerformance.setEarnedPercent(earnedPercentage);
        stockPerformance.setOpenPositionValue(presentValue);
        stockPerformance.setOpenPositionAmount(quantityOwned);
        return stockPerformance;
    }

    private boolean isSellTransaction(TransactionType transactionType) {
        return transactionType == TransactionType.SELL;
    }

    private boolean isPurcheseTransaction(TransactionType transactionType) {
        return transactionType == TransactionType.BUY;
    }

    private boolean isClosed(Stock stock) {
        return stock.getStateOfPossesion() == StateOfPossesion.CLOSED;
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
