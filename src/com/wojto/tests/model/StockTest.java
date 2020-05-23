package com.wojto.tests.model;

import com.wojto.calculations.StockPortoflio;
import com.wojto.importing.CsvFileImporter;
import com.wojto.importing.CsvFileTransactionParser;
import com.wojto.model.Transaction;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StockTest {

    protected List<Transaction> transactionList = new ArrayList<>();

    @org.junit.jupiter.api.BeforeAll
    void setUp() {
        File file = new File("C:\\Projects\\private\\stock-calculator-prototype\\resources\\Test_Transactinons.csv");
        CsvFileImporter fileImporter = new CsvFileImporter();
        CsvFileTransactionParser transactionParser = new CsvFileTransactionParser();
        List<String> transactionStringList = fileImporter.importTransactionsFromFile(file);
        for (String transactionString: transactionStringList) {
            transactionList.add(transactionParser.createTransactionFromString(transactionString));
        }
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
    }

    @org.junit.jupiter.api.Test
    void addTransaction() {
    }

    @org.junit.jupiter.api.Test
    void checkStateOfPossesion() {
    }

    @org.junit.jupiter.api.Test
    void getLastTransactionDate() {
    }
}