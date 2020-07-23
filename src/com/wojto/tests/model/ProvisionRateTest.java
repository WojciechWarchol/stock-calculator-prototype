package com.wojto.tests.model;

import com.wojto.model.ProvisionRate;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.MathContext;

import static org.junit.jupiter.api.Assertions.*;

class ProvisionRateTest {

    public static final MathContext PRECISION = new MathContext(2);
    public static final BigDecimal GPW_RATE = new BigDecimal(0.39).round(PRECISION);
    public static final BigDecimal INTERNATIONAL_STOCK_RATE = new BigDecimal(0.29).round(PRECISION);
    public static final BigDecimal TEST_RATE = new BigDecimal(0.69).round(PRECISION);
    public static final BigDecimal GPW_MINIMAL_PROVISION = new BigDecimal("3");
    public static final BigDecimal INTERNATIONAL_STOCK_MINIMAL_PROVISION = new BigDecimal("19");
    public static final BigDecimal TEST_MINIMAL_PROVISION = new BigDecimal(69);

    //TODO Fix provision rounding and initiation of numbers such as 3 (instaed of 3.00) also in Stock calculator test
    @Test
    void testMBankGpwProvisionRateBuilder() {
        ProvisionRate gpwProvisionRate = new ProvisionRate.ProvisionRateBuilder().mBankGpwProvisionRate().build();
        assertEquals(GPW_RATE, gpwProvisionRate.getRate());
        assertEquals(GPW_MINIMAL_PROVISION, gpwProvisionRate.getMinimalProvision());
    }

    @Test
    void testMBankInternationalStockProvisionRateBuilder() {
        ProvisionRate internalionalStockProvisionRate = new ProvisionRate.ProvisionRateBuilder().mBankInternationalStockProvisionRate().build();
        assertEquals(INTERNATIONAL_STOCK_RATE, internalionalStockProvisionRate.getRate());
        assertEquals(INTERNATIONAL_STOCK_MINIMAL_PROVISION, internalionalStockProvisionRate.getMinimalProvision());
    }

    @Test
    void testProvisionRateBuilder() {
        ProvisionRate testProvisionRate = new ProvisionRate.ProvisionRateBuilder().rate(TEST_RATE).minimalProvision(TEST_MINIMAL_PROVISION).build();
        assertEquals(TEST_RATE, testProvisionRate.getRate());
        assertEquals(TEST_MINIMAL_PROVISION, testProvisionRate.getMinimalProvision());
    }

}