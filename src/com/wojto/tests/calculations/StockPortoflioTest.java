package com.wojto.tests.calculations;

import com.wojto.calculations.StockPortoflio;
import com.wojto.importing.CsvFileImporter;
import com.wojto.importing.CsvFileTransactionParser;
import com.wojto.model.Stock;
import com.wojto.model.Transaction;
import com.wojto.model.TransactionType;
import org.junit.jupiter.api.BeforeAll;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StockPortoflioTest {

    static private List<Transaction> transactionList = new ArrayList<>();
    static private StockPortoflio stockPortoflio = new StockPortoflio();
    static private Transaction additionalTransaction = new Transaction(LocalDateTime.now(), "STOCK3", TransactionType.BUY, 1, new BigDecimal("10.0"), new BigDecimal("10.0"));
    static private Stock STOCK3 = new Stock("STOCK3");


    @BeforeAll
    static void setUp() {
        File file = new File(".\\resources\\Tests\\Test_Transactions_Portfolio.Csv");
        CsvFileImporter fileImporter = new CsvFileImporter();
        CsvFileTransactionParser transactionParser = new CsvFileTransactionParser();
        List<String> transactionStringList = fileImporter.importTransactionsFromFile(file);
        for (String transactionString : transactionStringList) {
            transactionList.add(transactionParser.createTransactionFromString(transactionString));
        }
        for (Transaction transaction : transactionList) {
            stockPortoflio.addTransaction(transaction);
        }
        STOCK3.addTransaction(additionalTransaction);
    }

    @org.junit.jupiter.api.Test
    void addTransactionAndGetStockList() {
        stockPortoflio.addTransaction(additionalTransaction);
        assertEquals(3, stockPortoflio.getStockList().size());
    }

    @org.junit.jupiter.api.Test
    void getStockFromSymbol() {
        assertEquals(STOCK3, stockPortoflio.getStockFromSymbol("STOCK3"));
    }

    @org.junit.jupiter.api.Test
    void getStockFromTransaction() {
        assertEquals(STOCK3, stockPortoflio.getStockFromTransaction(additionalTransaction));
    }

}