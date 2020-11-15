package com.wojto.calculations;

import com.wojto.model.*;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

public class GpwStockCalculator implements StockCalculator {

    private static MathContext precision = new MathContext(3);

    private BigDecimal tempProvisionValue = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    private ProvisionRate provisionRate;
    private Transaction previousTransaction;
    private StockPerformance currentStockPerformance;
    private List<Year> taxYearsToCalculate;

    public GpwStockCalculator() {}

    @Override
    public StockPerformance calculate(Stock stock, List<Year> taxYears) {
        taxYearsToCalculate = taxYears;
        provisionRate = stock.getProvisionRate();
        currentStockPerformance = new StockPerformance();
        List<ShareTransaction> tempShares = new ArrayList<>();

        // These are probably not needed when Positions are used
        BigDecimal closedPositionsBuyValue = BigDecimal.ZERO;
        BigDecimal closedPositionsSellValue = BigDecimal.ZERO;
        // Need to create: 1.List of closed Positions 2. An Open position 3. a LACKS position if needed
        List<Position> closedPositions = new ArrayList<>();
        Position openPosition = new Position();
        Position lackingPosition = new Position();

        // Do i still need these three?
        TransactionType currentTransactionType;
        List<ShareTransaction> ownedShareTransactions = new ArrayList<>();
        previousTransaction = stock.getTransactions().get(0);

        // Since Provisions are calculated at this point per share, calculating them is redundant.
        // This loop should mainly separate Transactions into positions

        // Calculating should be done afterwards for every position.
        // Will probably need a PositionPerformance class

        for (Transaction transaction : stock.getTransactions()) {
            currentTransactionType = transaction.getTransactionType();
            tempShares.clear();
            tempShares = ShareTransaction.createSharesFromTransaction(transaction);

            if (isPurcheseTransaction(currentTransactionType)) {
                openPosition.addShareTransactionList(tempShares);
            } else if (isSellTransaction(currentTransactionType)) {
                // TODO Check first Tax Year and divide Positions, after that everything works the same.
                int sharesNeededToClose = openPosition.numberOfSharesNeededToClose();
                if (sharesNeededToClose > 0
                        && sharesNeededToClose >= tempShares.size()
                        && openPosition.getTaxYear().equals(Year.from(transaction.getTransactionDate()))
                        || openPosition.getTaxYear() == null) {
                    openPosition.addShareTransactionList(tempShares);
                    if (openPosition.getPositionState() == StateOfPossesion.CLOSED) {
                        closedPositions.add(openPosition);
                        openPosition = new Position();
                    }
                } else if (sharesNeededToClose > 0
                        && sharesNeededToClose >= tempShares.size()
                        && !openPosition.getTaxYear().equals(Year.from(transaction.getTransactionDate()))) {
                    // TODO divide position into two, due to tax Year change (maybe a method in position? Maybe here)
                }

//                List<ShareTransaction> tempListOfSoldShareTransactions = new ArrayList<>();
//                tempListOfSoldShareTransactions.addAll(ShareTransaction.createSharesFromTransaction(transaction));
//                int currentlyProcessedSoldShare = 0;
//                BigDecimal sellResult = BigDecimal.ZERO;
//                try {
//                    while (currentlyProcessedSoldShare < tempListOfSoldShareTransactions.size()) {
//                        ShareTransaction ownedShareTransaction = ownedShareTransactions.get(0);
//                        ShareTransaction soldShareTransaction = tempListOfSoldShareTransactions.get(currentlyProcessedSoldShare);
//                        closedPositionsBuyValue = closedPositionsBuyValue.add(ownedShareTransaction.getPrice());
//                        closedPositionsSellValue = closedPositionsSellValue.add(soldShareTransaction.getPrice());
//                        if(taxYearsToCalculate == null || taxYearsToCalculate.contains(Year.of(transaction.getTransactionDate().getYear()))) {
//                            sellResult = sellResult.subtract(ownedShareTransaction.getPrice().subtract(soldShareTransaction.getPrice()));
//                        }
//                        ownedShareTransactions.remove(0);
//                        currentlyProcessedSoldShare++;
//                    }
//                } catch (IndexOutOfBoundsException e) {
//                    BigDecimal lackingSellValue = BigDecimal.ZERO;
//                    while (currentlyProcessedSoldShare < tempListOfSoldShareTransactions.size()) {
//                        ShareTransaction lackingSoldShareTransaction = tempListOfSoldShareTransactions.get(currentlyProcessedSoldShare);
//                        lackingSellValue = lackingSellValue.add(lackingSoldShareTransaction.getPrice());
//                        currentlyProcessedSoldShare++;
//                    }
//                    currentStockPerformance.updateLackingSellsValue(lackingSellValue);
//                }
//                calculateProvisionForTransaction(transaction);
//                currentStockPerformance.updateInvestmentResault(sellResult);
            }
        }

        // Proper Calculating here

        applyTempProvisionsToStockPerformance();

        currentStockPerformance.setOpenPositionAmount(ownedShareTransactions.size());
        BigDecimal valueOfOpenShares = BigDecimal.ZERO;
        for (ShareTransaction shareTransaction : ownedShareTransactions) {
            valueOfOpenShares = valueOfOpenShares.add(shareTransaction.getPrice());
        }
        currentStockPerformance.setOpenPositionValue(valueOfOpenShares);

        //TODO Sort stock performance by date

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

    // TODO temp solution applied. final solution should be based on POSITIONS
    private void calculateProvisionForTransaction(Transaction currentTransaction) {
        if (currentTransaction.getTransactionType().equals(TransactionType.BUY)) {
            addTransactionProvisionToTempProvisionValue(currentTransaction);
        } else if (taxYearsToCalculate != null && !taxYearsToCalculate.contains(Year.of(currentTransaction.getTransactionDate().getYear()))) {
            subtractTransactionProvisionFromTempProvisionValue(currentTransaction);
        } else {
            addTransactionProvisionToTempProvisionValue(currentTransaction);
        }
    }

    private void addTransactionProvisionToTempProvisionValue(Transaction currentTransaction) {
        tempProvisionValue = tempProvisionValue.add(currentTransaction.getTotalValue().multiply(provisionRate.getRate().divide(BigDecimal.valueOf(100))));
        tempProvisionValue = tempProvisionValue.setScale(2, RoundingMode.HALF_UP);
    }

    private void subtractTransactionProvisionFromTempProvisionValue(Transaction currentTransaction) {
        tempProvisionValue = tempProvisionValue.subtract(currentTransaction.getTotalValue().multiply(provisionRate.getRate().divide(BigDecimal.valueOf(100))));
        tempProvisionValue = tempProvisionValue.setScale(2, RoundingMode.HALF_UP);
        if (tempProvisionValue.compareTo(BigDecimal.ZERO) < 0) tempProvisionValue = BigDecimal.ZERO;
    }

    private void applyTempProvisionsToStockPerformance() {
        if (tempProvisionValue.compareTo(provisionRate.getMinimalProvision()) < 0) {
            tempProvisionValue = provisionRate.getMinimalProvision();
        }
        currentStockPerformance.updatePaidProvisions(tempProvisionValue);
        tempProvisionValue = BigDecimal.ZERO;
    }


    @Override
    public PortfolioPerformance calculatePortfolioPerformance(StockPortoflio stockPortoflio, List<Year> taxYears) {
        PortfolioPerformance portfolioPerformance = new PortfolioPerformance();
        StockPerformance stockPerformance;

        for (Stock stock : stockPortoflio.getStockList()) {
            stockPerformance = calculate(stock, taxYears);
            portfolioPerformance.updatePortfolioResault(stockPerformance.getInvestmenResault());
            portfolioPerformance.updatePaidProvisions(stockPerformance.getPaidProvisions());
            portfolioPerformance.updateLackingIncome(stockPerformance.getLackingSellsValue());
        }

        return portfolioPerformance;
    }

    //TODO Extract to seperate class, seperate Lacked, Opened and Closed Transactions. (obsolete I think)
    //TODO Sort by performance
    //TODO Add date of last transaction

}
