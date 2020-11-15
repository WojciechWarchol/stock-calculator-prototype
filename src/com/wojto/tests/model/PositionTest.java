package com.wojto.tests.model;

import com.wojto.model.Position;
import com.wojto.model.ShareTransaction;
import com.wojto.model.StateOfPossesion;
import com.wojto.model.TransactionType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PositionTest {

    private static Position testedPosition;
    private static List<ShareTransaction> boughtShares;
    private static List<ShareTransaction> soldShares;

    private static final LocalDateTime PURCHESE_DATE_1 = LocalDateTime.of(2020,1,1,12,0,0);
    private static final LocalDateTime SELL_DATE_1 = LocalDateTime.of(2020,1,10,12,0,0);

    @BeforeEach
    void setUp() {
        testedPosition = new Position();
        boughtShares = new ArrayList<>();
        soldShares = new ArrayList<>();
    }

    @Test
    void addShareTransaction() {
        boughtShares = createPurchesedSharesList(5);
        soldShares = createSoldSharesList(3);
        addSharesListToTestedPosition(boughtShares);
        addSharesListToTestedPosition(soldShares);

        assertEquals(boughtShares, testedPosition.getBoughtShareTransactions());
        assertEquals(soldShares, testedPosition.getSoldShareTransactions());
        assertEquals(PURCHESE_DATE_1, testedPosition.getFirstTransactionDate());
        assertEquals(SELL_DATE_1, testedPosition.getLastTransactionDate());
        assertEquals(Year.of(2020),testedPosition.getTaxYear());
        Assertions.assertEquals(StateOfPossesion.OPEN, testedPosition.getPositionState());
    }

    @Test
    void numberOfDaysPositionWasOpen() {
        boughtShares = createPurchesedSharesList(5);
        soldShares = createSoldSharesList(5);
        addSharesListToTestedPosition(boughtShares);
        addSharesListToTestedPosition(soldShares);

        assertEquals(9, testedPosition.numberOfDaysPositionWasOpen());
    }

    @Test
    void numberOfSharesNeededToClose() {
        boughtShares = createPurchesedSharesList(5);
        soldShares = createSoldSharesList(3);
        addSharesListToTestedPosition(boughtShares);
        addSharesListToTestedPosition(soldShares);

        assertEquals(2, testedPosition.numberOfSharesNeededToClose());
    }

    @Test
    void getPositionState() {
        soldShares = createSoldSharesList(3);
        addSharesListToTestedPosition(soldShares);
        assertEquals(StateOfPossesion.LACKS_PURCHESE, testedPosition.getPositionState());

        boughtShares = createPurchesedSharesList(6);
        addSharesListToTestedPosition(boughtShares);
        assertEquals(StateOfPossesion.OPEN, testedPosition.getPositionState());

        addSharesListToTestedPosition(soldShares);
        assertEquals(StateOfPossesion.CLOSED, testedPosition.getPositionState());
    }

    private static List<ShareTransaction> createPurchesedSharesList(int numberOfShares) {
        List<ShareTransaction> shareTransactionList = new ArrayList<>();
        ShareTransaction shareTransaction = createPurcheseShareTransaction();
        IntStream.range(0, numberOfShares).forEach(i -> shareTransactionList.add(shareTransaction));
        return shareTransactionList;
    }

    private static List<ShareTransaction> createSoldSharesList(int numberOfShares) {
        List<ShareTransaction> shareTransactionList = new ArrayList<>();
        ShareTransaction shareTransaction = createSellShareTransaction();
        IntStream.range(0, numberOfShares).forEach(i -> shareTransactionList.add(shareTransaction));
        return shareTransactionList;
    }

    private static ShareTransaction createPurcheseShareTransaction() {
        return new ShareTransaction(BigDecimal.valueOf(10),
                BigDecimal.valueOf(0.039),
                PURCHESE_DATE_1,
                TransactionType.BUY);
    }

    private static ShareTransaction createSellShareTransaction() {
        return new ShareTransaction(BigDecimal.valueOf(10),
                BigDecimal.valueOf(0.039),
                SELL_DATE_1,
                TransactionType.SELL);
    }

    private void addSharesListToTestedPosition(List<ShareTransaction> boughtShares) {
        boughtShares.stream().forEach(i -> testedPosition.addShareTransaction(i));
    }
}