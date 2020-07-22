package com.wojto.model;

import com.sun.xml.internal.ws.api.pipe.ServerTubeAssemblerContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

class TransactionTest {

    static private LocalDateTime TODAY_NOW = LocalDateTime.of(2020, Month.JANUARY, 1, 12, 00);
    static private LocalDateTime LATER_TODAY = TODAY_NOW.plusMinutes(1);
    static private LocalDateTime NEXT_DAY = LocalDateTime.of(2020, Month.JANUARY, 2, 13, 00);

    static private Transaction BUY_NOW = new Transaction(TODAY_NOW, "S1", "GPW", TransactionType.BUY, 1, BigDecimal.ONE, BigDecimal.ONE);
    static private Transaction BUY_LATER = new Transaction(LATER_TODAY, "S1", "GPW", TransactionType.BUY, 1, BigDecimal.ONE, BigDecimal.ONE);
    static private Transaction BUY_NEXT_DAY = new Transaction(NEXT_DAY, "S1", "GPW", TransactionType.BUY, 1, BigDecimal.ONE, BigDecimal.ONE);
    static private Transaction SELL_NOW = new Transaction(TODAY_NOW, "S1", "GPW", TransactionType.SELL, 1, BigDecimal.ONE, BigDecimal.ONE);
    static private Transaction SELL_NEXT_DAY = new Transaction(NEXT_DAY, "S1", "GPW", TransactionType.SELL, 1, BigDecimal.ONE, BigDecimal.ONE);


    @BeforeEach
    void setUp() {
    }

    @Test
    void isSameDayAndTypeAsSameDayTransaction() {
        assertEquals(true, BUY_NOW.isSameDayAndTypeAs(BUY_NOW));
        assertEquals(true, BUY_NOW.isSameDayAndTypeAs(BUY_LATER));
    }

    @Test
    void isSameDayAndTypeOnDifferentDayTransaction() {
        assertEquals(false, BUY_NOW.isSameDayAndTypeAs(BUY_NEXT_DAY));
    }

    @Test
    void isSameDayAndTypeOnDifferentTypeTransaction() {
        assertEquals(false, BUY_NOW.isSameDayAndTypeAs(SELL_NOW));
    }

    @Test
    void isSameDayAndTypeOnDifferentDayAndTypeTransaction() {
        assertEquals(false, BUY_NOW.isSameDayAndTypeAs(SELL_NEXT_DAY));
    }
}