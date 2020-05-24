package com.wojto;

import com.wojto.calculations.StockPortoflio;
import com.wojto.importing.CsvFileImporter;
import com.wojto.importing.CsvFileTransactionParser;

import java.io.File;

public class Main {

    public static void main(String[] args) {

        File file = new File("C:\\Projects\\private\\stock-calculator-prototype\\resources\\Tests\\Test_Transactions.Csv");
        StockPortoflio portoflio = new CsvFileTransactionParser().createStockPortfolio(file);
        portoflio.printPortfolioPerformance();
        //TODO Sum of the whole portfolio
    }
}
