package com.wojto.output.consolePrinting;

import com.wojto.calculations.PortfolioPerformance;
import com.wojto.calculations.StockPerformance;
import com.wojto.model.StateOfPossesion;
import com.wojto.model.Stock;

import java.math.BigDecimal;

public class StockPerformancePrinter {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BOLD = "\u001B[1m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_MAGENTA = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";


    public static final String FORMAT_OPEN =   "%s%s%s%s%s%s%s%s%s%s%s%s%s%s%n";
    public static final String FORMAT_CLOSED = "%s%s%s%s%s%s%s%s%s%s%n";
    public static final String FORMAT_PORTFOLIO_RESULT = "%-59s%-42s%-17s%s%20s%s%n";

    public static void printAbsolutePerformanceOfStock(Stock stock, StockPerformance stockPerformance) {
        StateOfPossesion stateOfThisStock = stock.getStateOfPossesion();
        if(stateOfThisStock.equals(StateOfPossesion.OPEN)){
            System.out.printf(FORMAT_OPEN, "Stock: ",
                    boldStockName(stock.getStockName()),
                    "Status: ",
                    colorInvestmentStatus(stock.getStateOfPossesion()),
                    "Performance: ",
                    colorInvestmentResault(stockPerformance.getInvestmenResault()),
                    "  Percentage: ",
                    colorInvestmentPercentage(stockPerformance.getEarnedPercent()),
                    "  Paid provisions: ",
                    colorPaidProvisions(stockPerformance.getPaidProvisions().doubleValue()),
                    "  Currently owned: ",
                    colorOwnedStockAmmount(stockPerformance.getOpenPositionAmount()),
                    "  Value on purchase: ",
                    colorInvestmentResault(stockPerformance.getOpenPositionValue())
            );
        } else if (stateOfThisStock.equals(StateOfPossesion.CLOSED) || stateOfThisStock.equals(StateOfPossesion.LACKS_PURCHESE)) {
            System.out.printf(FORMAT_CLOSED, "Stock: ",
                    boldStockName(stock.getStockName()),
                    "Status: ",
                    colorInvestmentStatus(stock.getStateOfPossesion()),
                    "Performance: ",
                    colorInvestmentResault(stockPerformance.getInvestmenResault()),
                    "  Percentage: ",
                    colorInvestmentPercentage(stockPerformance.getEarnedPercent()),
                    "  Paid provisions: ",
                    colorPaidProvisions(stockPerformance.getPaidProvisions().doubleValue())
            );
        }
    }

    public static void printPortfolioResult(PortfolioPerformance portfolioPerformance) {
        System.out.printf(FORMAT_PORTFOLIO_RESULT,
                "Portfollio performance: ",
                colorInvestmentResault(portfolioPerformance.getPortfolioResault()),
                "Total prov.:",
                colorPaidProvisions(portfolioPerformance.getPaidProvisions().doubleValue()),
                "Total minus prov.:",
                colorInvestmentResault(portfolioPerformance.calculateTotalResaultMinusProvisions())
        );
    }

    private static String colorInvestmentResault(BigDecimal investmentResault) {
        String coloredResault = String.format("%10s", investmentResault);
        if (investmentResault.compareTo(BigDecimal.ZERO) > 0) {
            coloredResault = ANSI_GREEN + coloredResault + ANSI_RESET;
        } else {
            coloredResault = ANSI_RED + coloredResault + ANSI_RESET;
        }
        return coloredResault;
    }

    private static String colorInvestmentPercentage(double percentage) {
        String formattedPercentage = String.format("%.2f", percentage);
        String coloredResault = String.format("%7s", formattedPercentage);
        if (percentage > 0) {
            coloredResault = ANSI_GREEN + coloredResault + ANSI_RESET;
        } else {
            coloredResault = ANSI_RED + coloredResault + ANSI_RESET;
        }
        return coloredResault;
    }

    private static String colorInvestmentStatus(StateOfPossesion state) {
        String coloredResault = String.format("%-16s", state);
        if (state.equals(StateOfPossesion.OPEN)) {
            coloredResault = ANSI_BLUE + coloredResault + ANSI_RESET;
        } else if (state.equals(StateOfPossesion.CLOSED)) {
            coloredResault = ANSI_CYAN + coloredResault + ANSI_RESET;
        } else {
            coloredResault = ANSI_YELLOW + coloredResault + ANSI_RESET;
        }
        return coloredResault;
    }

    private static String colorOwnedStockAmmount(int ammount) {
        String coloredResault = String.format("%5s", ammount);
        coloredResault = ANSI_BLUE + coloredResault + ANSI_RESET;
        return coloredResault;
    }

    private static String colorPaidProvisions(double ammount) {
        String formattedProvision = String.format("%.2f", ammount);
        String coloredResault = String.format("%7s", ammount);
        coloredResault = ANSI_YELLOW + coloredResault + ANSI_RESET;
        return coloredResault;
    }

    private static String boldStockName(String stockName) {
        String boldedResault = String.format("%-15s", stockName);
        boldedResault = ANSI_BOLD + boldedResault + ANSI_RESET;
        return boldedResault;
    }

}
