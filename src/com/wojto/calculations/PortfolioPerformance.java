package com.wojto.calculations;

import java.math.BigDecimal;
import java.util.Objects;

public class PortfolioPerformance {

    private BigDecimal portfolioResault = BigDecimal.ZERO;
    private BigDecimal lackingIncome = BigDecimal.ZERO;

    public PortfolioPerformance() {}

    public BigDecimal getPortfolioResault() {
        return portfolioResault;
    }

    public void setPortfolioResault(BigDecimal portfolioResault) {
        this.portfolioResault = portfolioResault;
    }

    public void updatePortfolioResault(BigDecimal amount) {
        portfolioResault = portfolioResault.add(amount);
    }

    public BigDecimal getLackingIncome() {
        return lackingIncome;
    }

    public void setLackingIncome(BigDecimal lackingIncome) {
        this.lackingIncome = lackingIncome;
    }

    public void updateLackingIncome(BigDecimal amount) {
        lackingIncome = lackingIncome.add(amount);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PortfolioPerformance)) return false;
        PortfolioPerformance that = (PortfolioPerformance) o;
        return Objects.equals(portfolioResault, that.portfolioResault);
    }

    @Override
    public int hashCode() {
        return Objects.hash(portfolioResault);
    }
}
