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
                    if (sharesNeededToClose == 0 && isClosed(openPosition) && openPosition.hasAnyShareTransactions()) {

                        closedPositions.add(openPosition);
                        openPosition = new Position();
                    }

                    // If all purchases have been close, an there are still sells, add them to lackingPosition
                    if (!tempShares.isEmpty()) {
                        List<ShareTransaction> lackingSellShareTransactions = new ArrayList<>();
                        while (tempShares.size() > 0) {
                            lackingSellShareTransactions.add(tempShares.pop());
                        }
                        lackingPosition.addShareTransactionList(lackingSellShareTransactions);
                    }
                }
            }
        }
        lackingPosition.getPositionState();
        // DECISION Adding positions, and than calculating on them is a bit sketchy
        stock.addPosition(lackingPosition);
        stock.addPosition(closedPositions);
        stock.addPosition(openPosition);

        // TODO Proper Calculating here - PRIORITY - For even easier calculations, I should consider - after initial sorting into positions - extracting a closed position from openPosition. This will make calculating in the next step EASY, together with extracting exact data for stock performance (clear sums for bought&sold, and bought&open) which will help in calculating % for stock and whole portfolio. Should also make it clear how to calc provisions. But this will make a closed and open position overlap. An alternative is to make a subtype of Open - Partially closed? Maybe positions should be subtypes?
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
        // TODO Not satisfied that I calculate Open positions just because they have a null tax year. Might be prone to error.
        if (position.getTaxYear() != null && !taxYearsToCalculate.contains(position.getTaxYear())){
            return;
        }

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
        if (!isLacking(position)) {
            totalSoldValue = totalSoldValue.add(sellSum);
        }
        BigDecimal positionResult = sellSum.subtract(purcheseSum);

        if (isClosed(position)) {
            currentStockPerformance.updateInvestmentResault(positionResult);
            currentStockPerformance.updatePaidProvisions(paidProvisionSum);
        } else if (isOpen(position)) {
            BigDecimal stillOpenSum = BigDecimal.ZERO;
            for (ShareTransaction shareTransaction : boughtShareTransactions) {
                stillOpenSum = stillOpenSum.add(shareTransaction.getPrice());
                // DECISION calculate paid provisions now, or on position close? Some Kind of flag maybe?
                paidProvisionSum = paidProvisionSum.add(shareTransaction.getProvision());
            }
            // TODO Create na "updates" method (though it might be redundant since there should be only one Open position, and there should always be recalculation
            paidProvisionSum = paidProvisionSum.setScale(2, RoundingMode.HALF_UP);

            currentStockPerformance.setOpenPositionValue(stillOpenSum);
            currentStockPerformance.setOpenPositionAmount(boughtShareTransactions.size());
            currentStockPerformance.updateInvestmentResault(positionResult);
            currentStockPerformance.updatePaidProvisions(paidProvisionSum);
        }
        if (isLacking(position)) {
            currentStockPerformance.updateLackingSellsValue(positionResult);
        }
    }

    private boolean isLacking(Position position) {
        return position.getPositionState().equals(StateOfPossesion.LACKS_PURCHESE);
    }

    private boolean isOpen(Position position) {
        return position.getPositionState().equals(StateOfPossesion.OPEN);
    }

    private boolean isClosed(Position position) {
        return position.getPositionState().equals(StateOfPossesion.CLOSED);
    }

    private boolean isSellTransaction(TransactionType transactionType) {
        return transactionType == TransactionType.SELL;
    }

    private boolean isPurcheseTransaction(TransactionType transactionType) {
        return transactionType == TransactionType.BUY;
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

    //TODO Sort by performance
    //TODO Add date of last transaction

}
