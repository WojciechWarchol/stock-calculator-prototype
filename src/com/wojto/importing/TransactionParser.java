package com.wojto.importing;

import com.wojto.calculations.StockPortoflio;

import java.io.File;

public interface TransactionParser {

    public StockPortoflio createStockPortfolio(File file);

}
