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
import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GpwStockCalculatorTest {

    static protected List<Transaction> transactionList = new ArrayList<>();
    static protected GpwStockCalculator calculator = new GpwStockCalculator();
    static protected StockPortoflio PORTFOLIO = new StockPortoflio();
    static protected CsvFileTransactionParser transactionParser = new CsvFileTransactionParser();
    static private final List<Year> TEST_YEAR = Arrays.asList(Year.of(2020));

    @AfterEach
    void tearDown() {
        transactionList.clear();
        PORTFOLIO = new StockPortoflio();
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/com/wojto/tests/calculations/resources/Test_Transactions_Calculator_Expected.csv", numLinesToSkip = 1, delimiter = ';')
    void parameterizedCalculateTest(String stockSymbol, String openValue, int openAmount, String investmentResult, double earnedPercentage, String paidProvisions) {

        setUpForCalculationAndPerformanceTest();

        StockPerformance performanceOfStock = calculator.calculate(PORTFOLIO.getStockFromSymbol(stockSymbol), TEST_YEAR);

        checkPerformance(performanceOfStock, openValue, openAmount, investmentResult, earnedPercentage, paidProvisions);
    }

    @Test
    void calculatePortfolioPerformance() {

        setUpForCalculationAndPerformanceTest();

        PortfolioPerformance performance = calculator.calculatePortfolioPerformance(PORTFOLIO, TEST_YEAR);
        assertEquals(new BigDecimal("194.50"), performance.getPortfolioResault());
        assertEquals(new BigDecimal("78.23"), performance.getPaidProvisions());
        assertEquals(new BigDecimal("2050.00"), performance.getLackingIncome());
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/com/wojto/tests/calculations/resources/Test_Tax_Year_Expected.Csv", numLinesToSkip = 1, delimiter = ';')
    void ParameterizedCheckTaxYearTest(String stockSymbol, String profit, String provision) {

        setUpForTaxYearTest();

        StockPerformance performanceOfStock = calculator.calculate(PORTFOLIO.getStockFromSymbol(stockSymbol), TEST_YEAR);

        assertEquals(new BigDecimal(profit), performanceOfStock.getInvestmenResault());
        assertEquals(new BigDecimal(provision), performanceOfStock.getPaidProvisions());
    }

    private void checkPerformance(StockPerformance performanceOfStock, String openValue, int openAmount, String investmentResult, double earnedPercentage, String paindProvisions) {
        assertEquals(new BigDecimal(openValue), performanceOfStock.getOpenPositionValue());
        assertEquals(openAmount, performanceOfStock.getOpenPositionAmount());
        assertEquals(new BigDecimal(investmentResult), performanceOfStock.getInvestmenResault());
        assertEquals(earnedPercentage, performanceOfStock.getEarnedPercent());
        assertEquals(new BigDecimal(paindProvisions), performanceOfStock.getPaidProvisions());
    }

    void setUpForCalculationAndPerformanceTest() {
        //TODO Test should also use previous years that will not be calculated
        File file = new File(".\\resources\\Tests\\Test_Transactions_Calculator.Csv");
        fillTransactionListAndPortfolio(file);
    }

    void setUpForTaxYearTest() {
        File file = new File(".\\resources\\Tests\\Test_Transactions_Year.Csv");
        fillTransactionListAndPortfolio(file);
    }

    private void fillTransactionListAndPortfolio(File file) {
        PORTFOLIO = transactionParser.createStockPortfolio(file);
    }

}