package com.wojto.tests.calculations;

import com.wojto.calculations.GpwStockCalculator;
import com.wojto.calculations.PortfolioPerformance;
import com.wojto.calculations.StockPerformance;
import com.wojto.calculations.StockPortoflio;
import com.wojto.importing.CsvFileImporter;
import com.wojto.importing.CsvFileTransactionParser;
import com.wojto.model.Stock;
import com.wojto.model.Transaction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

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
    static protected StockPortoflio PORTFOLIO = new StockPortoflio();

    @BeforeAll
    static void setUp() {
        File file = new File(".\\resources\\Tests\\Test_Transactions_Calculator.Csv");
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
            PORTFOLIO.addTransaction(transaction);
        }
    }

    @AfterEach
    void tearDown() {
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/com/wojto/tests/calculations/Test_Transactions_Calculator_Expected.csv", numLinesToSkip = 1, delimiter = ';')
    void parameterizedCalculateTest(String stockSymbol, String openValue, int openAmount, String investmentResult, double earnedPercentage, String paidProvisions) {

        StockPerformance performanceOfStock = calculator.calculate(PORTFOLIO.getStockFromSymbol(stockSymbol));

        checkPerformance(performanceOfStock, openValue, openAmount, investmentResult, earnedPercentage, paidProvisions);
    }

    private void checkPerformance(StockPerformance performanceOfStock, String openValue, int openAmount, String investmentResult, double earnedPercentage, String paindProvisions) {
        assertEquals(new BigDecimal(openValue), performanceOfStock.getOpenPositionValue());
        assertEquals(openAmount, performanceOfStock.getOpenPositionAmount());
        assertEquals(new BigDecimal(investmentResult), performanceOfStock.getInvestmenResault());
        assertEquals(earnedPercentage, performanceOfStock.getEarnedPercent());
        assertEquals(new BigDecimal(paindProvisions), performanceOfStock.getPaidProvisions());
    }

    @Test
    void calculatePortfolioPerformance() {
        PortfolioPerformance performance = calculator.calculatePortfolioPerformance(PORTFOLIO);
        assertEquals(new BigDecimal("194.50"), performance.getPortfolioResault());
        assertEquals(new BigDecimal("2050.00"), performance.getLackingIncome());
    }
}