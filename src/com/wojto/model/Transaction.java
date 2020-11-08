package com.wojto.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class Transaction implements Comparable<Transaction>{

    // Parameters created from csv
    private LocalDateTime transactionDate;
    private String stockSymbol;
    private String marketSymbol;
    private TransactionType transactionType;
    private long amount;
    private BigDecimal price;
    private BigDecimal totalValue;

    // Calculated parameters
    private BigDecimal provision;

// DECISION Market, currency of price and total, provision

    public Transaction(LocalDateTime transactionDate, String stockSymbol, String marketSymbol, TransactionType transactionType, long amount, BigDecimal price, BigDecimal totalValue) {
        this.transactionDate = transactionDate;
        this.stockSymbol = stockSymbol;
        this.marketSymbol = marketSymbol;
        this.transactionType = transactionType;
        this.amount = amount;
        this.price = price;
        this.totalValue = totalValue;
    }

    public Transaction(LocalDateTime transactionDate, String stockSymbol, String marketSymbol, TransactionType transactionType, long amount, BigDecimal price, BigDecimal totalValue, BigDecimal provision) {
        this.transactionDate = transactionDate;
        this.stockSymbol = stockSymbol;
        this.marketSymbol = marketSymbol;
        this.transactionType = transactionType;
        this.amount = amount;
        this.price = price;
        this.totalValue = totalValue;
        this.provision = provision;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getStockSymbol() {
        return stockSymbol;
    }

    public void setStockSymbol(String stockSymbol) {
        this.stockSymbol = stockSymbol;
    }

    public String getMarketSymbol() {
        return marketSymbol;
    }

    public void setMarketSymbol(String marketSymbol) {
        this.marketSymbol = marketSymbol;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(BigDecimal totalValue) {
        this.totalValue = totalValue;
    }

    public BigDecimal getProvision() {
        return provision;
    }

    public void setProvision(BigDecimal provision) {
        this.provision = provision;
    }

    public boolean isSameDay(Transaction otherTransaciton) {
        return this.transactionDate.toLocalDate().equals(otherTransaciton.transactionDate.toLocalDate());
    }

    public boolean isSameDayAndTypeAs(Transaction otherTransaction) {
        return (this.transactionDate.toLocalDate().equals(otherTransaction.transactionDate.toLocalDate()) && this.transactionType.equals(otherTransaction.getTransactionType()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Transaction)) return false;
        Transaction that = (Transaction) o;
        return amount == that.amount &&
                transactionDate.equals(that.transactionDate) &&
                stockSymbol.equals(that.stockSymbol) &&
                transactionType == that.transactionType &&
                price.equals(that.price) &&
                totalValue.equals(that.totalValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transactionDate, stockSymbol, transactionType, amount, price, totalValue);
    }

    @Override
    public int compareTo(Transaction otherTransaction) {
        if (this.transactionDate.compareTo(otherTransaction.transactionDate) < 0) {
            return -1;
        } else if (this.transactionDate.compareTo(otherTransaction.transactionDate) > 0){
            return 1;
        } else {
            return 0;
        }
    }
}
