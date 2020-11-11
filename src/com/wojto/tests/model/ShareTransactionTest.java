package com.wojto.tests.model;

import com.wojto.model.ShareTransaction;
import com.wojto.model.Transaction;
import com.wojto.model.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ShareTransactionTest {

    // Creation of Transactions for testing should be changed if more than one is needed
    private static Transaction testTransaction = new Transaction(LocalDateTime.of(2020,3,1,12,0,0),
            "TESTSTOCK",
            "WWA-GPW",
            TransactionType.BUY,
            7,
            BigDecimal.valueOf(140),
            BigDecimal.valueOf(900),
            BigDecimal.valueOf(3.82));

    @BeforeEach
    void setUp() {
    }

    @Test
    void createSharesFromTransaction() {
        List<ShareTransaction> shareTransactions = ShareTransaction.createSharesFromTransaction(testTransaction);
        ShareTransaction singleShare = shareTransactions.get(0);

        assertEquals(7, shareTransactions.size());
        assertEquals(BigDecimal.valueOf(140), singleShare.getPrice());
        assertEquals(BigDecimal.valueOf(0.545714285714), singleShare.getProvision());
        assertEquals(LocalDateTime.of(2020,3,1,12,0,0), singleShare.getDate());
        assertEquals(TransactionType.BUY, singleShare.getType());
    }
}