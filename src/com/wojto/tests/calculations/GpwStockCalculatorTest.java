package com.wojto.tests.calculations;

import com.wojto.calculations.GpwStockCalculator;
import com.wojto.calculations.PortfolioPerformance;
import com.wojto.calculations.StockPerformance;
import com.wojto.calculations.StockPortoflio;
import com.wojto.importing.CsvFileImporter;
import com.wojto.importing.CsvFileTransactionParser;
import com.wojto.model.Transaction;
import org.junit.jupiter.api.AfterEach;
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
    static protected StockPortoflio PORTFOLIO = new StockPortoflio();

    //TODO Add test for testing Tax year, rewrite this class, add Expected.csv file, and write test

    @AfterEach
    void tearDown() {
        transactionList.clear();
        PORTFOLIO = new StockPortoflio();
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/com/wojto/tests/calculations/resources/Test_Transactions_Calculator_Expected.csv", numLinesToSkip = 1, delimiter = ';')
    void parameterizedCalculateTest(String stockSymbol, String openValue, int openAmount, String investmentResult, double earnedPercentage, String paidProvisions) {

        setUpForCalculationAndPerformanceTest();

        StockPerformance performanceOfStock = calculator.calculate(PORTFOLIO.getStockFromSymbol(stockSymbol));

        checkPerformance(performanceOfStock, openValue, openAmount, investmentResult, earnedPercentage, paidProvisions);
    }

    @Test
    void calculatePortfolioPerformance() {

        setUpForCalculationAndPerformanceTest();

        PortfolioPerformance performance = calculator.calculatePortfolioPerformance(PORTFOLIO);
        assertEquals(new BigDecimal("194.50"), performance.getPortfolioResault());
        assertEquals(new BigDecimal("86.22"), performance.getPaidProvisions());
        assertEquals(new BigDecimal("2050.00"), performance.getLackingIncome());
    }

    private void checkPerformance(StockPerformance performanceOfStock, String openValue, int openAmount, String investmentResult, double earnedPercentage, String paindProvisions) {
        assertEquals(new BigDecimal(openValue), performanceOfStock.getOpenPositionValue());
        assertEquals(openAmount, performanceOfStock.getOpenPositionAmount());
        assertEquals(new BigDecimal(investmentResult), performanceOfStock.getInvestmenResault());
        assertEquals(earnedPercentage, performanceOfStock.getEarnedPercent());
        assertEquals(new BigDecimal(paindProvisions), performanceOfStock.getPaidProvisions());
    }

    void setUpForCalculationAndPerformanceTest() {
        File file = new File(".\\resources\\Tests\\Test_Transactions_Calculator.Csv");
        CsvFileImporter fileImporter = new CsvFileImporter();
        CsvFileTransactionParser transactionParser = new CsvFileTransactionParser();

        List<String> transactionStringList = fileImporter.importTransactionsFromFile(file);
        for (String transactionString : transactionStringList) {
            transactionList.add(transactionParser.createTransactionFromString(transactionString));
        }
        for (Transaction transaction : transactionList) {
            PORTFOLIO.addTransaction(transaction);
        }
    }
}