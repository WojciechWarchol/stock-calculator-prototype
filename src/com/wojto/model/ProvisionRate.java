package com.wojto.model;

import java.math.BigDecimal;
import java.math.MathContext;

public class ProvisionRate {

    private final BigDecimal rate;
    private final BigDecimal minimalProvision;

    public ProvisionRate(ProvisionRateBuilder builder) {
        this.rate = builder.rate;
        this.minimalProvision = builder.minimalProvision;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public BigDecimal getMinimalProvision() {
        return minimalProvision;
    }

    public static class ProvisionRateBuilder {

        private final MathContext precision = new MathContext(2);
        private BigDecimal rate;
        private BigDecimal minimalProvision;

        public ProvisionRateBuilder mBankGpwProvisionRate() {
            this.rate(0.39);
            this.minimalProvision(3.00);
            return this;
        }

        public ProvisionRateBuilder mBankInternationalStockProvisionRate() {
            this.rate(0.29);
            this.minimalProvision(19.00);
            return this;
        }

        public ProvisionRateBuilder rate(BigDecimal rate) {
            this.rate = rate.round(precision);
            return this;
        }

        public ProvisionRateBuilder rate(int rate) {
            this.rate = new BigDecimal(rate).round(precision);
            return this;
        }

        public ProvisionRateBuilder rate(double rate) {
            this.rate = new BigDecimal(rate).round(precision);
            return this;
        }

        public ProvisionRateBuilder minimalProvision(BigDecimal minimalProvision) {
            this.minimalProvision = minimalProvision.round(precision);
            return this;
        }

        public ProvisionRateBuilder minimalProvision(int minimalProvision) {
            this.minimalProvision = new BigDecimal(minimalProvision).round(precision);
            return this;
        }

        public ProvisionRateBuilder minimalProvision(double minimalProvision) {
            this.minimalProvision = new BigDecimal(minimalProvision).round(precision);
            return this;
        }

        public ProvisionRate build() {
            return new ProvisionRate(this);
        }

    }

}
