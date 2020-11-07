package com.wojto.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ShareTransaction implements Comparable<ShareTransaction> {

    private final BigDecimal price;
    private final BigDecimal provision;
    private final LocalDateTime date;
    private final TransactionType type;

    public ShareTransaction(BigDecimal price, BigDecimal provision, LocalDateTime date, TransactionType type) {
        this.price = price;
        this.provision = provision;
        this.date = date;
        this.type = type;
    }

    public static List<ShareTransaction> createSharesFromTransaction(Transaction transaction) {
        List<ShareTransaction> shareTransactions = new ArrayList<>();
        BigDecimal price = transaction.getPrice();
        // TODO calculate provison per share
        LocalDateTime date = transaction.getTransactionDate();
        TransactionType type = transaction.getTransactionType();
        for (int i = 0 ; i < transaction.getAmount() ; i++) {
            shareTransactions.add(new ShareTransaction(price, provision, date, type ));
        }
        return shareTransactions;
    }

    public BigDecimal subtractSharesValue(ShareTransaction shareTransaction) {
        BigDecimal subtractionResault = BigDecimal.ZERO;
        subtractionResault = price.subtract(shareTransaction.getPrice());
        return subtractionResault;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public TransactionType getType() {
        return type;
    }

    @Override
    public int compareTo(ShareTransaction o) {
        if (this.getDate().compareTo(o.getDate()) > 1) {
            return 1;
        } else if (this.getDate().compareTo(o.getDate()) < 1) {
            return -1;
        } else {
            return 0;
        }
    }
}
