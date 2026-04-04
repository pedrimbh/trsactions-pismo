package com.pismo.trasactions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.pismo.trasactions.api.account.AccountResponse;
import com.pismo.trasactions.api.transaction.TransactionResponse;
import com.pismo.trasactions.service.account.AccountService;
import com.pismo.trasactions.service.transaction.TransactionService;
import com.pismo.trasactions.domain.account.Account;
import com.pismo.trasactions.repository.AccountRepository;
import com.pismo.trasactions.repository.TransactionRepository;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;

@SpringBootTest
class TrasactionsApiIntegrationTests {

    @Autowired
    private AccountService accountService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @BeforeEach
    void setup() {
        transactionRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @Test
    void shouldCreateAndFetchAccount() {
        AccountResponse created = accountService.create("12345678900");
        AccountResponse loaded = accountService.findById(created.getAccountId());

        assertNotNull(created.getAccountId());
        assertEquals("12345678900", loaded.getDocumentNumber());
        assertEquals(created.getAccountId(), loaded.getAccountId());
    }

    @Test
    void shouldCreateTransactionWithNegativeAmountForPurchase() {
        Account account = accountRepository.save(new Account("98765432100"));

        TransactionResponse transaction = transactionService.create(account.getId(), 1, new BigDecimal("50.00"));

        assertEquals(account.getId(), transaction.getAccountId());
        assertEquals(1, transaction.getOperationTypeId());
        assertEquals(new BigDecimal("-50.00"), transaction.getAmount());
        assertNotNull(transaction.getEventDate());
    }

    @Test
    void shouldCreateTransactionWithPositiveAmountForPayment() {
        Account account = accountRepository.save(new Account("11122233344"));

        TransactionResponse transaction = transactionService.create(account.getId(), 4, new BigDecimal("60.00"));

        assertEquals(new BigDecimal("60.00"), transaction.getAmount());
    }
}



