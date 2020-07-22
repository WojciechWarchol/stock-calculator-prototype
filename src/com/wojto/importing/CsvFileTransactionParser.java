package com.wojto.importing;

import com.wojto.calculations.StockPortoflio;
import com.wojto.model.Transaction;
import com.wojto.model.TransactionType;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CsvFileTransactionParser implements TransactionParser {

    private static final String CSV_SEPARATOR = ";";

    // TODO Generalize to StockCalculator
    @Override
    public StockPortoflio createStockPortfolio(File file) {
        StockPortoflio stockPortfolio = new StockPortoflio();
        CsvFileImporter fileImporter = new CsvFileImporter();
        List<String> transactionsString = fileImporter.importTransactionsFromFile(file);

        for (String transactionString : transactionsString) {
            Transaction transaction = createTransactionFromString(transactionString);
            stockPortfolio.addTransaction(transaction);
        }

        return stockPortfolio;
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
