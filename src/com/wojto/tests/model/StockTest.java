package com.wojto.tests.model;

import com.wojto.calculations.GpwStockCalculator;
import com.wojto.importing.CsvFileImporter;
import com.wojto.importing.CsvFileTransactionParser;
import com.wojto.model.StateOfPossesion;
import com.wojto.model.Stock;
import com.wojto.model.Transaction;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StockTest {

    static protected List<Transaction> transactionList = new ArrayList<>();
    static protected GpwStockCalculator calculator = new GpwStockCalculator();
    static protected Stock CLSDPROF1 = new Stock("CLSDPROF1");
    static protected Stock CLSDLOSS1 = new Stock("CLSDLOSS1");
    static protected Stock OPEN1 = new Stock("OPEN1");
    static protected Stock OPEN2 = new Stock("OPEN2");
    static protected Stock OPEN3 = new Stock("OPEN3");
    static protected Stock LACKS1 = new Stock("LACKS1");
    static protected Stock LACKS2 = new Stock("LACKS2");
    static protected Stock LASTDATE1 = new Stock("LASTDATE1");

    static protected Stock ADDTESTER = new Stock("ADDTESTER");

    @org.junit.jupiter.api.BeforeAll
    static void setUp() {
        File file = new File("C:\\Projects\\private\\stock-calculator-prototype\\resources\\Tests\\Test_Transactions.Csv");
        CsvFileImporter fileImporter = new CsvFileImporter();
        CsvFileTransactionParser transactionParser = new CsvFileTransactionParser();
        List<String> transactionStringList = fileImporter.importTransactionsFromFile(file);
        for (String transactionString: transactionStringList) {
            transactionList.add(transactionParser.createTransactionFromString(transactionString));
        }
        calculator = new GpwStockCalculator();
        for (Transaction transaction : transactionList) {
            CLSDPROF1.addTransaction(transaction);
            CLSDLOSS1.addTransaction(transaction);
            OPEN1.addTransaction(transaction);
            OPEN2.addTransaction(transaction);
            OPEN3.addTransaction(transaction);
            LACKS1.addTransaction(transaction);
            LACKS2.addTransaction(transaction);
            LASTDATE1.addTransaction(transaction);
        }

    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
    }

    @org.junit.jupiter.api.Test
    void addTransaction() {
        for (Transaction transaction : transactionList) {
            ADDTESTER.addTransaction(transaction);
        }
        assertEquals(2,ADDTESTER.getTransactions().size());
    }

    @org.junit.jupiter.api.Test
    void checkStateOfPossesion() {
        assertEquals(StateOfPossesion.CLOSED, CLSDPROF1.checkStateOfPossesion());
        assertEquals(StateOfPossesion.CLOSED, CLSDLOSS1.checkStateOfPossesion());
        assertEquals(StateOfPossesion.OPEN, OPEN1.checkStateOfPossesion());
        assertEquals(StateOfPossesion.OPEN, OPEN2.checkStateOfPossesion());
        assertEquals(StateOfPossesion.OPEN, OPEN3.checkStateOfPossesion());
        assertEquals(StateOfPossesion.LACKS_PURCHESE, LACKS1.checkStateOfPossesion());
        assertEquals(StateOfPossesion.LACKS_PURCHESE, LACKS2.checkStateOfPossesion());
    }

    @org.junit.jupiter.api.Test
    void getLastTransactionDate() {
        assertEquals(LocalDateTime.of(2020, 5, 5, 10, 20, 00), CLSDPROF1.getLastTransactionDate());
        assertEquals(LocalDateTime.of(2020, 5, 2, 9, 40, 00), CLSDLOSS1.getLastTransactionDate());
        assertEquals(LocalDateTime.of(2020, 4, 2, 9, 40, 00), OPEN1.getLastTransactionDate());
        assertEquals(LocalDateTime.of(2020, 4, 2, 9, 33, 15), OPEN2.getLastTransactionDate());
        assertEquals(LocalDateTime.of(2020, 4, 3, 9, 45, 00), OPEN3.getLastTransactionDate());
        assertEquals(LocalDateTime.of(2019, 4, 13, 15, 33, 15), LACKS1.getLastTransactionDate());
        assertEquals(LocalDateTime.of(2020, 4, 20, 11, 23, 10), LACKS2.getLastTransactionDate());
        assertEquals(LocalDateTime.of(2020, 4, 29, 9, 33, 15), LASTDATE1.getLastTransactionDate());
    }
}