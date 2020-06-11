package com.wojto.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Share {

    private final BigDecimal price;

    public Share(BigDecimal price){
        this.price = price;
    }

    public static List<Share> createSharesFromTransaction(Transaction transaction) {
        List<Share> shares = new ArrayList<>();
        BigDecimal price = transaction.getPrice();
        for (int i = 0 ; i < transaction.getAmount() ; i++) {
            shares.add(new Share(price));
        }
        return shares;
    }

    public BigDecimal subtractSharesValue(Share share) {
        BigDecimal subtractionResault = BigDecimal.ZERO;
        subtractionResault = price.subtract(share.getPrice());
        return subtractionResault;
    }

    public BigDecimal getPrice() {
        return price;
    }
}
