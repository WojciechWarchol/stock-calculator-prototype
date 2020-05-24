package com.wojto.tests.calculations;

import com.wojto.calculations.GpwStockCalculator;
import com.wojto.calculations.StockPerformance;
import com.wojto.importing.CsvFileImporter;
import com.wojto.importing.CsvFileTransactionParser;
import com.wojto.model.Stock;
import com.wojto.model.Transaction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GpwStockCalculatorTest {

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

    @BeforeAll
    static void setUp() {
        File file = new File("C:\\Projects\\private\\stock-calculator-prototype\\resources\\Tests\\Test_Transactions.Csv");
        CsvFileImporter fileImporter = new CsvFileImporter();
        CsvFileTransactionParser transactionParser = new CsvFileTransactionParser();
        List<String> transactionStringList = fileImporter.importTransactionsFromFile(file);
        for (String transactionString : transactionStringList) {
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

    @AfterEach
    void tearDown() {
    }

    @Test
    void calculate() {
        StockPerformance performanceOfCLSDPROF1 = calculator.calculate(CLSDPROF1);
        StockPerformance performanceOfCLSDLOSS1 = calculator.calculate(CLSDLOSS1);
        StockPerformance performanceOfOPEN1 = calculator.calculate(OPEN1);
        StockPerformance performanceOfOPEN2 = calculator.calculate(OPEN2);
        StockPerformance performanceOfOPEN3 = calculator.calculate(OPEN3);
        StockPerformance performanceOfLACKS1 = calculator.calculate(LACKS1);
        StockPerformance performanceOfLACKS2 = calculator.calculate(LACKS2);

        assertEquals(new BigDecimal("0"), performanceOfCLSDPROF1.getOpenPositionValue());
        assertEquals(0, performanceOfCLSDPROF1.getOpenPositionAmount());
        assertEquals(new BigDecimal("100.00"), performanceOfCLSDPROF1.getInvestmenResault());

        assertEquals(new BigDecimal("0"), performanceOfCLSDLOSS1.getOpenPositionValue());
        assertEquals(0, performanceOfCLSDLOSS1.getOpenPositionAmount());
        assertEquals(new BigDecimal("-260.80"), performanceOfCLSDLOSS1.getInvestmenResault());

        assertEquals(new BigDecimal("10.00"), performanceOfOPEN1.getOpenPositionValue());
        assertEquals(10, performanceOfOPEN1.getOpenPositionAmount());
        assertEquals(new BigDecimal("0"), performanceOfOPEN1.getInvestmenResault());

        assertEquals(new BigDecimal("346.00"), performanceOfOPEN2.getOpenPositionValue());
        assertEquals(200, performanceOfOPEN2.getOpenPositionAmount());
        //TODO No investment resault if position not closed...
        assertEquals(new BigDecimal("0"), performanceOfOPEN2.getInvestmenResault());

        assertEquals(new BigDecimal("1100.00"), performanceOfOPEN3.getOpenPositionValue());
        assertEquals(110, performanceOfOPEN3.getOpenPositionAmount());
        assertEquals(new BigDecimal("-100.00"), performanceOfOPEN3.getInvestmenResault());

        assertEquals(new BigDecimal("0"), performanceOfLACKS1.getOpenPositionValue());
        assertEquals(-100, performanceOfLACKS1.getOpenPositionAmount());
        assertEquals(new BigDecimal("0"), performanceOfLACKS1.getInvestmenResault());

        assertEquals(new BigDecimal("0"), performanceOfLACKS2.getOpenPositionValue());
        assertEquals(0, performanceOfLACKS2.getOpenPositionAmount());
        assertEquals(new BigDecimal("100.00"), performanceOfLACKS2.getInvestmenResault());
    }

    @Test
    void calculatePortfolioPerformance() {
    }
}