package com.wojto.model;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

public class Position {

    private LocalDateTime firstTransactionDate;
    private LocalDateTime lastTransactionDate;
    private Year taxYear;
    private StateOfPossesion positionState;
    private List<ShareTransaction> boughtShareTransactions = new ArrayList<>();
    private List<ShareTransaction> soldShareTransactions = new ArrayList<>();

    public Position() {};

    public Position(Year taxYear) {
        this.taxYear = taxYear;
    }

    public void addShareTransaction (ShareTransaction share) {
        LocalDateTime tempTransactionDate = share.getDate();
        TransactionType tempTransactionType = share.getType();

        if (tempTransactionType.equals(TransactionType.SELL)) {
            soldShareTransactions.add(share);
            if (lastTransactionDate == null || tempTransactionDate.compareTo(lastTransactionDate) > 0) {
                lastTransactionDate = tempTransactionDate;
            }
        } else if (tempTransactionType.equals(TransactionType.BUY)) {
            boughtShareTransactions.add(share);
            if (firstTransactionDate == null || tempTransactionDate.compareTo(firstTransactionDate) < 0) {
                firstTransactionDate = tempTransactionDate;
            }
        }
    }

    public boolean positionLacksPurchases() {
        boolean lacksPurchases;
        if (boughtShareTransactions.size() < soldShareTransactions.size()) {
            lacksPurchases = true;
        } else {
            lacksPurchases = false;
        }
        return lacksPurchases;
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
