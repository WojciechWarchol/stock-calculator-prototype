package com.wojto.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Stock {

    private static final String GPW_SYMBOL = "WWA-GPW";
    private static final List<String> FOREIGN_STOCK_SYMBOLS = Arrays.asList("DEU-XETRA", "GBR-LSE", "USA-NYSE", "USA-NASDAQ");

    private String stockName;
    private List<Transaction> transactions = new ArrayList<>();
    //TODO add functionality associated with positions, getting OPEN, and LACKS, assuring only one of each exists.
    private List<Position> positions = new ArrayList<>();
    private StateOfPossesion stateOfPossesion;
    private ProvisionRate provisionRate;

    public Stock(String stockName, List<Transaction> transactions, StateOfPossesion stateOfPossesion) {
        this.stockName = stockName;
        this.transactions = transactions;
        this.stateOfPossesion = stateOfPossesion;
    }

    public Stock(String stockName){
        this.stockName = stockName;
    }

    public Stock(Transaction transaction) {
        this.stockName = transaction.getStockSymbol();
        this.provisionRate = determineProvisionRateForStock(transaction.getMarketSymbol());
        addTransaction(transaction);
    }

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public void addTransaction(Transaction transaction) {
        if (this.stockName.equals(transaction.getStockSymbol()) ) {
            transactions.add(transaction);
            transactions.sort(Transaction::compareTo);
        }

        stateOfPossesion = checkStateOfPossesion();
    }

    private ProvisionRate determineProvisionRateForStock(String marketSymbol) {
        if (marketSymbol.equals(GPW_SYMBOL)) {
            return new ProvisionRate.ProvisionRateBuilder().mBankGpwProvisionRate().build();
        } else if (FOREIGN_STOCK_SYMBOLS.contains(marketSymbol)) {
            return new ProvisionRate.ProvisionRateBuilder().mBankInternationalStockProvisionRate().build();
        } else {
            return new ProvisionRate.ProvisionRateBuilder().rate(0).minimalProvision(0).build();
        }
    }

    public StateOfPossesion getStateOfPossesion() {
        return stateOfPossesion;
    }

    public void setStateOfPossesion(StateOfPossesion stateOfPossesion) {
        this.stateOfPossesion = stateOfPossesion;
    }

    public StateOfPossesion checkStateOfPossesion() {
        int bought = 0;
        int sold = 0;
        for (Transaction transaction : transactions) {
            if (transaction.getTransactionType() == TransactionType.BUY) {
                bought += transaction.getAmount();
            } else if (transaction.getTransactionType() == TransactionType.SELL) {
                sold += transaction.getAmount();
            } else {
                System.out.println("Unknown transaction type.");
            }
        }

        if (bought == sold) {
            return StateOfPossesion.CLOSED;
        } else if (bought > sold) {
            return StateOfPossesion.OPEN;
        } else {
            return StateOfPossesion.LACKS_PURCHESE;
        }
        //TODO mark OPEN as LACKS if it has sells before first purchese
    }

    public ProvisionRate getProvisionRate() {
        return provisionRate;
    }

    public void setProvisionRate(ProvisionRate provisionRate) {
        this.provisionRate = provisionRate;
    }

    public LocalDateTime getLastTransactionDate() {
        LocalDateTime lastDate = LocalDateTime.MIN;
        LocalDateTime dateToCheck;
        LocalDateTime lastTransactionDate = null;
        for (Transaction transaction : transactions) {
            dateToCheck = transaction.getTransactionDate();
            lastTransactionDate = dateToCheck.compareTo(lastDate) > 0 ? dateToCheck : lastDate;
            lastDate = dateToCheck;
        }
        return lastTransactionDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Stock stock = (Stock) o;
        return Objects.equals(stockName, stock.stockName) &&
                Objects.equals(transactions, stock.transactions) &&
                stateOfPossesion == stock.stateOfPossesion;
    }

    @Override
    public int hashCode() {
        return Objects.hash(stockName, transactions, stateOfPossesion);
    }
}
