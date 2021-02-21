package com.wojto.calculations;

import com.wojto.model.*;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.Year;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class GpwStockCalculator implements StockCalculator {

    private static MathContext precision = new MathContext(3);

    private BigDecimal tempProvisionValue = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    private ProvisionRate provisionRate;
    private Transaction previousTransaction;
    private StockPerformance currentStockPerformance;
    private List<Year> taxYearsToCalculate;

    // Temporary solution before these can be taken straight from Positions
    private BigDecimal totalPurchesedAndClosedValue = BigDecimal.ZERO;
    private BigDecimal totalSoldValue = BigDecimal.ZERO;

    public GpwStockCalculator() {}

    @Override
    public StockPerformance calculate(Stock stock, List<Year> taxYears) {
        taxYearsToCalculate = taxYears;
        provisionRate = stock.getProvisionRate();
        currentStockPerformance = new StockPerformance();
        totalPurchesedAndClosedValue = BigDecimal.ZERO;
        totalSoldValue = BigDecimal.ZERO;
        LinkedList<ShareTransaction> tempShares = new LinkedList<>();

        // Need to create: 1.List of closed Positions 2. An Open position 3. a LACKS position if needed
        List<Position> closedPositions = new ArrayList<>();
        Position openPosition = new Position();
        Position lackingPosition = new Position();

        TransactionType currentTransactionType;

        for (Transaction transaction : stock.getTransactions()) {
            currentTransactionType = transaction.getTransactionType();
            Year currentTransactionTaxYear = Year.from(transaction.getTransactionDate());

            tempShares.clear();
            tempShares.addAll(ShareTransaction.createSharesFromTransaction(transaction));

            if (isPurcheseTransaction(currentTransactionType)) {
                openPosition.addShareTransactionList(tempShares);
            } else if (isSellTransaction(currentTransactionType)) {

                if (openPosition.getTaxYear() != null && openPosition.getTaxYear().compareTo(currentTransactionTaxYear) < 0) {
                    Position newlyClosedPosition = openPosition.extractClosedTaxYearAsClosedPosition(currentTransactionTaxYear);
                    closedPositions.add(newlyClosedPosition);
                }

                if (openPosition.getTaxYear() == null || openPosition.getTaxYear().equals(currentTransactionTaxYear)) {
                    int sharesNeededToClose = openPosition.numberOfSharesNeededToClose();

                    // Isolate sell ShareTransactions, that have a purchase counterpart
                    List<ShareTransaction> nonLackingSellShareTransactions = new ArrayList<>();
                    for (; sharesNeededToClose > 0 && !tempShares.isEmpty(); sharesNeededToClose--) {
                        nonLackingSellShareTransactions.add(tempShares.pop());
                    }

                    // Appply those ShareTransactions to the openPosition
                    openPosition.addShareTransactionList(nonLackingSellShareTransactions);

                    // Close the position if all purchases have a sell
                    if (sharesNeededToClose == 0 && openPosition.getPositionState() == StateOfPossesion.CLOSED) {

                        closedPositions.add(openPosition);
                        openPosition = new Position();

                        // If all purchases have been close, an there are still sells, add them to lackingPosition
                        if (!tempShares.isEmpty()) {
                            List<ShareTransaction> lackingSellShareTransactions = new ArrayList<>();
                            for (; sharesNeededToClose > 0; sharesNeededToClose--) {
                                lackingSellShareTransactions.add(tempShares.pop());
                            }
                            lackingPosition.addShareTransactionList(lackingSellShareTransactions);
                        }
                    }
                }
            }
        }
        // DECISION Adding positions, and than calculating on them is a bit sketchy
        stock.addPosition(lackingPosition);
        stock.addPosition(closedPositions);
        stock.addPosition(openPosition);

        // TODO Proper Calculating here - PRIORITY
        for (Position position : stock.getPositions()) {
            processPositionAndAddToStockPerformance(position);
        }

        //TODO Sort stock performance by date

        double earnedPercentage;
        if (totalSoldValue.equals(BigDecimal.ZERO)) {
            earnedPercentage = 0.00;
        } else {
            try {
                BigDecimal calcStep = totalSoldValue.multiply(new BigDecimal("100"));
                calcStep = calcStep.divide(totalPurchesedAndClosedValue, 2, RoundingMode.HALF_UP);
                calcStep = calcStep.subtract(new BigDecimal("100"));
                earnedPercentage = calcStep.doubleValue();
            } catch (ArithmeticException e) {
                earnedPercentage = 0.00;
            }
        }

        currentStockPerformance.setEarnedPercent(earnedPercentage);
        return currentStockPerformance;
    }

    private void processPositionAndAddToStockPerformance(Position position) {
        BigDecimal purcheseSum = BigDecimal.ZERO;
        BigDecimal sellSum = BigDecimal.ZERO;
        BigDecimal paidProvisionSum = BigDecimal.ZERO;

        LinkedList<ShareTransaction> boughtShareTransactions = new LinkedList<>(position.getBoughtShareTransactions());
        LinkedList<ShareTransaction> soldShareTransactions = new LinkedList<>(position.getSoldShareTransactions());
        LinkedList<ShareTransaction> boughtAndSoldShareTransactions = new LinkedList<>();

        //TODO Transform to use numberOfSharesNeededToClose() method and extract the purchese and sell sums directly from Position
        if (boughtShareTransactions.size() > soldShareTransactions.size()) {
            for (int soldSharesLeft = soldShareTransactions.size(); soldSharesLeft > 0; soldSharesLeft--) {
                boughtAndSoldShareTransactions.add(boughtShareTransactions.pollFirst());
            }
        } else if (boughtShareTransactions.size() == soldShareTransactions.size()) {
            boughtAndSoldShareTransactions = boughtShareTransactions;
            boughtShareTransactions = new LinkedList<>();
        }

        for (ShareTransaction shareTransaction : boughtAndSoldShareTransactions) {
            purcheseSum = purcheseSum.add(shareTransaction.getPrice());
            paidProvisionSum = paidProvisionSum.add(shareTransaction.getProvision());
        }
        for (ShareTransaction shareTransaction : soldShareTransactions) {
            sellSum = sellSum.add(shareTransaction.getPrice());
            paidProvisionSum = paidProvisionSum.add(shareTransaction.getProvision());
        }

        paidProvisionSum = paidProvisionSum.setScale(2, RoundingMode.HALF_UP);
        totalPurchesedAndClosedValue = totalPurchesedAndClosedValue.add(purcheseSum);
        totalSoldValue = totalSoldValue.add(sellSum);
        BigDecimal positionResult = sellSum.subtract(purcheseSum);

        StateOfPossesion positionState = position.getPositionState();
        if (positionState.equals(StateOfPossesion.CLOSED)) {
            currentStockPerformance.updateInvestmentResault(positionResult);
            currentStockPerformance.updatePaidProvisions(paidProvisionSum);
        } else if (positionState.equals(StateOfPossesion.OPEN)) {
            BigDecimal stillOpenSum = BigDecimal.ZERO;
            for (ShareTransaction shareTransaction : boughtShareTransactions) {
                stillOpenSum = stillOpenSum.add(shareTransaction.getPrice());
                // DECISION calculate paid provisions now, or on position close?
                paidProvisionSum = paidProvisionSum.add(shareTransaction.getProvision());
            }
            // TODO Create na "updates" method (though it might be redundant since there should be only one Open position, and there should always be recalculation
            paidProvisionSum = paidProvisionSum.setScale(2, RoundingMode.HALF_UP);

            currentStockPerformance.setOpenPositionValue(stillOpenSum);
            currentStockPerformance.setOpenPositionAmount(boughtShareTransactions.size());
            currentStockPerformance.updateInvestmentResault(positionResult);
            currentStockPerformance.updatePaidProvisions(paidProvisionSum);
        }
        if (position.getPositionState().equals(StateOfPossesion.LACKS_PURCHESE)) {
            currentStockPerformance.updateLackingSellsValue(positionResult);
        }
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
