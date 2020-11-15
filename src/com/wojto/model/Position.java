package com.wojto.model;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;

public class Position {

    private LocalDateTime firstTransactionDate;
    private LocalDateTime lastTransactionDate;
    private Year taxYear;
    private StateOfPossesion positionState;
    private List<ShareTransaction> boughtShareTransactions = new ArrayList<>();
    private List<ShareTransaction> soldShareTransactions = new ArrayList<>();

    public Position() {
        positionState = StateOfPossesion.OPEN;
    }

    public Position(Year taxYear) {
        this.taxYear = taxYear;
    }

    public void addShareTransactionList (List<ShareTransaction> list) {
        list.forEach(this::addShareTransaction);
    }

    public void addShareTransaction (ShareTransaction share) {
        LocalDateTime tempTransactionDate = share.getDate();
        TransactionType tempTransactionType = share.getType();

        if (tempTransactionType.equals(TransactionType.BUY)) {
            boughtShareTransactions.add(share);
            if (firstTransactionDate == null || tempTransactionDate.compareTo(firstTransactionDate) < 0) {
                firstTransactionDate = tempTransactionDate;
            }
        } else if (tempTransactionType.equals(TransactionType.SELL)) {
            soldShareTransactions.add(share);
            if (lastTransactionDate == null || tempTransactionDate.compareTo(lastTransactionDate) > 0) {
                lastTransactionDate = tempTransactionDate;
            }
            if (taxYear == null) taxYear = Year.from(tempTransactionDate);
        }
    }

    public int numberOfDaysPositionWasOpen() {
        int numberOfDays = (int)DAYS.between(firstTransactionDate,lastTransactionDate);
        return numberOfDays;
    }

    public int numberOfSharesNeededToClose() {
        int numberOfShares;
        if(positionState == StateOfPossesion.LACKS_PURCHESE) {
            numberOfShares = -1;
        } else if (positionState == StateOfPossesion.CLOSED) {
            numberOfShares = 0;
        } else {
            numberOfShares = boughtShareTransactions.size() - soldShareTransactions.size();
        }
        return numberOfShares;
    }

    public StateOfPossesion getPositionState() {
        if (boughtShareTransactions.size() > soldShareTransactions.size()) {
            positionState = StateOfPossesion.OPEN;
        } else if (boughtShareTransactions.size() == soldShareTransactions.size()) {
            positionState = StateOfPossesion.CLOSED;
        } else {
            positionState = StateOfPossesion.LACKS_PURCHESE;
        }
        return positionState;
    }

    public LocalDateTime getFirstTransactionDate() {
        return firstTransactionDate;
    }

    public LocalDateTime getLastTransactionDate() {
        return lastTransactionDate;
    }

    public Year getTaxYear() {
        return taxYear;
    }

    public List<ShareTransaction> getBoughtShareTransactions() {
        return boughtShareTransactions;
    }

    public List<ShareTransaction> getSoldShareTransactions() {
        return soldShareTransactions;
    }

    // This method probably won't be needed
    public void addTransaction(Transaction transaction) {
        LocalDateTime tempTransactionDate = transaction.getTransactionDate();
        TransactionType tempTransactionType = transaction.getTransactionType();

        if (tempTransactionType.equals(TransactionType.SELL)) {
            soldShareTransactions.addAll(ShareTransaction.createSharesFromTransaction(transaction));
            if (lastTransactionDate == null || tempTransactionDate.compareTo(lastTransactionDate) > 0) {
                lastTransactionDate = tempTransactionDate;
            }
        } else if (tempTransactionType.equals(TransactionType.BUY)) {
            boughtShareTransactions.addAll(ShareTransaction.createSharesFromTransaction(transaction));
            if (firstTransactionDate == null || tempTransactionDate.compareTo(firstTransactionDate) < 0) {
                firstTransactionDate = tempTransactionDate;
            }
        }
    }

}
