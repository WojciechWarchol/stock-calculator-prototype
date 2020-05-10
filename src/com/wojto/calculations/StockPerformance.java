package com.wojto.calculations;

import java.math.BigDecimal;
import java.util.Objects;

public class StockPerformance implements Comparable<StockPerformance> {

    private String stockSymbol;
    private BigDecimal openPositionValue = new BigDecimal("0");
    private int openPositionAmount = 0;
    private BigDecimal investmenResault = new BigDecimal("0");
    // TODO Sum up provisions


    public StockPerformance(String stockSymbol, BigDecimal investmenResault) {
        this.stockSymbol = stockSymbol;
        this.investmenResault = investmenResault;
    }

    public StockPerformance(String stockSymbol) {
        this.stockSymbol = stockSymbol;
    }

    public String getStockSymbol() {
        return stockSymbol;
    }

    public void setStockSymbol(String stockSymbol) {
        this.stockSymbol = stockSymbol;
    }

    public BigDecimal getOpenPositionValue() {
        return openPositionValue;
    }

    public void setOpenPositionValue(BigDecimal openPositionValue) {
        this.openPositionValue = openPositionValue;
    }

    public int getOpenPositionAmount() {
        return openPositionAmount;
    }

    public void setOpenPositionAmount(int openPositionAmount) {
        this.openPositionAmount = openPositionAmount;
    }

    public BigDecimal getInvestmenResault() {
        return investmenResault;
    }

    public void setInvestmenResault(BigDecimal investmenResault) {
        this.investmenResault = investmenResault;
    }

//    public void addPucheseToOpenPosition(BigDecimal amount) { openPositionValue = openPositionValue.add(amount); }

//    public void clearOpenPosition() {openPositionValue = BigDecimal.ZERO;}

//    public void addPurcheseToPerformacne(BigDecimal amount) {
//        investmenResault = investmenResault.subtract(amount);
//    }
//
//    public void addSaleToPerformance(BigDecimal amount) {
//        investmenResault = investmenResault.add(amount);
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StockPerformance)) return false;
        StockPerformance that = (StockPerformance) o;
        return stockSymbol.equals(that.stockSymbol) &&
                investmenResault.equals(that.investmenResault);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stockSymbol, investmenResault);
    }

    @Override
    public int compareTo(StockPerformance o) {
        if(this.investmenResault.compareTo(o.investmenResault) > 1) {
            return 1;
        } else if (this.investmenResault.compareTo(o.investmenResault) < 1) {
            return -1;
        } else {
            return 0;
        }
    }
}
