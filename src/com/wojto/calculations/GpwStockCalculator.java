package com.wojto.calculations;

import com.wojto.model.*;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class GpwStockCalculator implements StockCalculator {

    private static MathContext precision = new MathContext(3);

    private BigDecimal tempProvisionValue = BigDecimal.ZERO;
    private ProvisionRate provisionRate;
    private Transaction previousTransaction;
    private StockPerformance currentStockPerformance;

    public GpwStockCalculator() {}

    @Override
    public StockPerformance calculate(Stock stock) {
        BigDecimal closedPositionsBuyValue = BigDecimal.ZERO;
        BigDecimal closedPositionsSellValue = BigDecimal.ZERO;
        currentStockPerformance = new StockPerformance();

        TransactionType transactionType;
        List<Share> ownedShares = new ArrayList<>();

        previousTransaction = stock.getTransactions().get(0);
        provisionRate = stock.getProvisionRate();

        for (Transaction transaction : stock.getTransactions()) {
            transactionType = transaction.getTransactionType();
            if (isPurcheseTransaction(transactionType)) {
                ownedShares.addAll(Share.createSharesFromTransaction(transaction));

                calculateProvisionForTransaction(transaction);
            } else if (isSellTransaction(transactionType)) {
                List<Share> tempListOfSoldShares = new ArrayList<>();
                tempListOfSoldShares.addAll(Share.createSharesFromTransaction(transaction));
                int currentlyProcessedSoldShare = 0;
                BigDecimal sellResault = BigDecimal.ZERO;
                try {
                    while (currentlyProcessedSoldShare < tempListOfSoldShares.size()) {
                        Share ownedShare = ownedShares.get(0);
                        Share soldShare = tempListOfSoldShares.get(currentlyProcessedSoldShare);
                        closedPositionsBuyValue = closedPositionsBuyValue.add(ownedShare.getPrice());
                        closedPositionsSellValue = closedPositionsSellValue.add(soldShare.getPrice());
                        sellResault = sellResault.subtract(ownedShare.getPrice().subtract(soldShare.getPrice()));
                        ownedShares.remove(0);
                        currentlyProcessedSoldShare++;
                    }
                } catch (IndexOutOfBoundsException e) {
                    BigDecimal lackingSellValue = BigDecimal.ZERO;
                    while (currentlyProcessedSoldShare < tempListOfSoldShares.size()) {
                        Share lackingSoldShare = tempListOfSoldShares.get(currentlyProcessedSoldShare);
                        lackingSellValue = lackingSellValue.add(lackingSoldShare.getPrice());
                        currentlyProcessedSoldShare++;
                    }
                    currentStockPerformance.updateLackingSellsValue(lackingSellValue);
                }
                calculateProvisionForTransaction(transaction);
                currentStockPerformance.updateInvestmentResault(sellResault);
            }
        }

        applyTempProvisionsToStockPerformance();

        currentStockPerformance.setOpenPositionAmount(ownedShares.size());
        BigDecimal valueOfOpenShares = BigDecimal.ZERO;
        for (Share share : ownedShares) {
            valueOfOpenShares = valueOfOpenShares.add(share.getPrice());
        }
        currentStockPerformance.setOpenPositionValue(valueOfOpenShares);

        //TODO Maybe aggregate Transactions into POSITIONS. Sort stock performance by date

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

        currentStockPerformance.setEarnedPercent(earnedPercentage);
        return currentStockPerformance;
    }

    private boolean isLacking(Stock stock) {
        return stock.getStateOfPossesion() == StateOfPossesion.LACKS_PURCHESE;
    }

    private boolean isOpen(Stock stock) {
        return stock.getStateOfPossesion() == StateOfPossesion.OPEN;
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

    private void calculateProvisionForTransaction(Transaction currentTransaction) {
        if (!currentTransaction.isSameDayAndTypeAs(previousTransaction)) {
            applyTempProvisionsToStockPerformance();
        }
        addTransactionProvisionToTempProvisionValue(currentTransaction);
    }

    private void addTransactionProvisionToTempProvisionValue(Transaction currentTransaction) {
        tempProvisionValue = tempProvisionValue.add(currentTransaction.getTotalValue().multiply(provisionRate.getRate().divide(BigDecimal.valueOf(100)))).round(precision);
    }

    private void applyTempProvisionsToStockPerformance() {
        if (tempProvisionValue.compareTo(provisionRate.getMinimalProvision()) < 0) {
            tempProvisionValue = provisionRate.getMinimalProvision();
        }
        currentStockPerformance.updatePaidProvisions(tempProvisionValue);
        tempProvisionValue = BigDecimal.ZERO;
    }


    @Override
    public PortfolioPerformance calculatePortfolioPerformance(StockPortoflio stockPortoflio) {
        PortfolioPerformance portfolioPerformance = new PortfolioPerformance();
        StockPerformance stockPerformance = new StockPerformance();

        for (Stock stock : stockPortoflio.getStockList()) {
            stockPerformance = calculate(stock);
            portfolioPerformance.updatePortfolioResault(stockPerformance.getInvestmenResault());
            portfolioPerformance.updateLackingIncome(stockPerformance.getLackingSellsValue());
        }

        return portfolioPerformance;
    }

    //TODO Extract to seperate class, seperate Lacked, Opened and Closed Transactions. (obsolete I think)
    //TODO Sort by performance
    //TODO Add date of last transaction
    //TODO Sum up (with privisions)

}
