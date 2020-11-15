package com.wojto.importing;

import com.wojto.calculations.StockPortoflio;
import com.wojto.model.ProvisionRate;
import com.wojto.model.Transaction;
import com.wojto.model.TransactionType;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class CsvFileTransactionParser implements TransactionParser {

    private static final String CSV_SEPARATOR = ";";

    @Override
    public StockPortoflio createStockPortfolio(File file) {
        StockPortoflio stockPortfolio = new StockPortoflio();
        CsvFileImporter fileImporter = new CsvFileImporter();
        List<String> transactionsStrings = fileImporter.importTransactionsFromFile(file);

        List<Transaction> transactionsList = transactionsStrings.stream().map(s -> createTransactionFromString(s)).collect(Collectors.toList());
        Collections.sort(transactionsList);
        Map<LocalDate, List<Transaction>> transactionsAggregatedIntoSingleDay = new HashMap<>();

        for (Transaction transaction : transactionsList) {
            transactionsAggregatedIntoSingleDay.computeIfAbsent(transaction.getTransactionDate().toLocalDate(),
                    k -> new ArrayList<>()).add(transaction);
        }

//        List<Transaction> transactionListWithProvision = new ArrayList<>();

        for (List<Transaction> transactionsForThisDay : transactionsAggregatedIntoSingleDay.values()) {
            transactionsForThisDay = calculateProvisions(transactionsForThisDay);
            transactionsForThisDay.forEach(stockPortfolio::addTransaction);
        }

        return stockPortfolio;
    }

    //TODO This functionality should be in a different class, maybe a service
    private List<Transaction> calculateProvisions(List<Transaction> transactions) {
        Map<Transaction, BigDecimal> calculatedProvisions = new HashMap<>();

        // TODO change the way this param is assigned
        ProvisionRate provisionRate = new ProvisionRate.ProvisionRateBuilder().mBankGpwProvisionRate().build();
        BigDecimal provisionRateValue = provisionRate.getRate();

        BigDecimal tempTotalProvisionValue = BigDecimal.ZERO;
        int totalShares = 0;
        for (Transaction transaction : transactions) {
            BigDecimal calculatedProvision = calculateProvisionForTransaction(provisionRateValue, transaction);
            transaction.setProvision(calculatedProvision);
            tempTotalProvisionValue = tempTotalProvisionValue.add(calculatedProvision);
            totalShares += transaction.getAmount();
        }

        transactions = recalculateProvisionIfBelowMinimalValue(transactions, tempTotalProvisionValue, totalShares, provisionRate);

        return transactions;
    }

    private List<Transaction> recalculateProvisionIfBelowMinimalValue(List<Transaction> transactions, BigDecimal tempTotalProvisionValue, int totalShares, ProvisionRate provisionRate) {
        if (calculatedProvisionIsBelowMinimalValue(tempTotalProvisionValue, provisionRate)) {
            BigDecimal provisionValuePerShare = provisionRate.getMinimalProvision().divide(BigDecimal.valueOf(totalShares));
            for (Transaction transaction : transactions) {
                BigDecimal provisionForThisTransaction = calculateProvisionForTransactionWhenBelowMinimal(provisionValuePerShare, transaction);
                transaction.setProvision(provisionForThisTransaction);
            }
        }

        return transactions;
    }

    private BigDecimal calculateProvisionForTransaction(BigDecimal provisionRateValue, Transaction transaction) {
        BigDecimal calculatedProvision = transaction.getTotalValue().multiply(provisionRateValue.divide(BigDecimal.valueOf(100)));
        calculatedProvision = calculatedProvision.setScale(2, RoundingMode.HALF_UP);
        return calculatedProvision;
    }

    private BigDecimal calculateProvisionForTransactionWhenBelowMinimal(BigDecimal provisionValuePerShare, Transaction transaction) {
        BigDecimal provisionForThisTransaction = provisionValuePerShare.multiply(BigDecimal.valueOf(transaction.getAmount()));
        provisionForThisTransaction = provisionForThisTransaction.setScale(2, RoundingMode.HALF_UP);
        return provisionForThisTransaction;
    }

    private boolean calculatedProvisionIsBelowMinimalValue(BigDecimal totalProvision, ProvisionRate provisionRate) {
        boolean isOverMinimalValue = totalProvision.compareTo(provisionRate.getMinimalProvision()) < 0;
        return isOverMinimalValue;
    }

    public Transaction createTransactionFromString(String transactionString) {
        String[] transactionArguments = splitTransactionString(transactionString);
        Transaction transaction = new Transaction(
                createLocalDateTimeFromString(transactionArguments[0]),
                transactionArguments[1],
                transactionArguments[2],
                determineTransactionType(transactionArguments[3]),
                Long.parseLong(eliminateWhitespaceCharacter(transactionArguments[4])),
                new BigDecimal(replaceCommaWithDot(transactionArguments[5])),
                new BigDecimal(replaceCommaWithDot(transactionArguments[7])));
        return transaction;
    }

    private String[] splitTransactionString(String transactionString) {
        String[] transactionArguments;
        transactionArguments = transactionString.split(CSV_SEPARATOR);
        return transactionArguments;
    }

    private TransactionType determineTransactionType(String argument) {
        if (argument.equals("K")) {
            return TransactionType.BUY;
        } else if (argument.equals("S")) {
            return TransactionType.SELL;
        } else {
            return null;
        }
    }

    private String eliminateWhitespaceCharacter(String transactionArgument) {
        return transactionArgument.replace(" ", "");
    }

    private String replaceCommaWithDot(String transactionArgument) {
        return transactionArgument.replace(',', '.').replace(" ","");
    }

    private LocalDateTime createLocalDateTimeFromString(String stringDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(stringDate, formatter);
        return dateTime;
    }
}
