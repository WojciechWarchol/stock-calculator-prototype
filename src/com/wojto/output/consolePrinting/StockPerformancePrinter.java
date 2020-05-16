package com.wojto.output.consolePrinting;

import com.wojto.calculations.StockPerformance;
import com.wojto.model.Stock;

import java.math.BigDecimal;

public class StockPerformancePrinter {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String FORMAT = "%s%-15s%s%-16s%s%10s%n";

    public static void printAbsolutePerformanceOfStock(Stock stock, StockPerformance stockPerformance) {
        System.out.printf(FORMAT, "Stock: ", stock.getStockName(),
                "Status: ", stock.getStateOfPossesion(),
                "Performance: ", colorInvestmentResault(stockPerformance.getInvestmenResault()));
    }

    public static String colorInvestmentResault(BigDecimal investmentResault) {
        String coloredResault = String.format("%10s", investmentResault);
        if (investmentResault.compareTo(BigDecimal.ZERO) > 0) {
            coloredResault = ANSI_GREEN + coloredResault + ANSI_RESET;
        } else {
            coloredResault = ANSI_RED + coloredResault + ANSI_RESET;
        }
        return coloredResault;
    }

}
