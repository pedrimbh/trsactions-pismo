package com.pismo.trasactions.service.transaction;

import com.pismo.trasactions.api.transaction.TransactionResponse;
import com.pismo.trasactions.common.exception.ResourceNotFoundException;
import com.pismo.trasactions.domain.account.Account;
import com.pismo.trasactions.domain.transaction.Transaction;
import com.pismo.trasactions.repository.TransactionRepository;
import com.pismo.trasactions.service.account.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private TransactionService transactionService;

    private Account account;

    @BeforeEach
    void setUp() {
        account = new Account("12345678900");
        try {
            var field = Account.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(account, 1L);
        } catch (Exception ignored) {}
    }

    private Transaction buildTransaction(Account account, Integer operationTypeId, BigDecimal amount) {
        Transaction t = new Transaction(account, operationTypeId, amount);
        try {
            var field = Transaction.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(t, 10L);

            var dateField = Transaction.class.getDeclaredField("eventDate");
            dateField.setAccessible(true);
            dateField.set(t, LocalDateTime.now());
        } catch (Exception ignored) {}
        return t;
    }

    // ----------------------------------------------------------------
    // Tipos negativos (1, 2, 3)
    // ----------------------------------------------------------------

    @Test
    void shouldCreateTransaction_withNegativeAmount_forCashPurchase() {
        BigDecimal expectedAmount = new BigDecimal("-100.00");
        Transaction saved = buildTransaction(account, 1, expectedAmount);

        when(accountService.getEntityById(1L)).thenReturn(account);
        when(transactionRepository.save(any())).thenReturn(saved);

        TransactionResponse response = transactionService.create(1L, 1, new BigDecimal("100.00"));

        assertThat(response.getAmount()).isEqualByComparingTo(new BigDecimal("-100.00"));
        assertThat(response.getOperationTypeId()).isEqualTo(1);
    }

    @Test
    void shouldCreateTransaction_withNegativeAmount_forInstallmentPurchase() {
        BigDecimal expectedAmount = new BigDecimal("-200.00");
        Transaction saved = buildTransaction(account, 2, expectedAmount);

        when(accountService.getEntityById(1L)).thenReturn(account);
        when(transactionRepository.save(any())).thenReturn(saved);

        TransactionResponse response = transactionService.create(1L, 2, new BigDecimal("200.00"));

        assertThat(response.getAmount()).isEqualByComparingTo(new BigDecimal("-200.00"));
        assertThat(response.getOperationTypeId()).isEqualTo(2);
    }

    @Test
    void shouldCreateTransaction_withNegativeAmount_forWithdrawal() {
        BigDecimal expectedAmount = new BigDecimal("-50.00");
        Transaction saved = buildTransaction(account, 3, expectedAmount);

        when(accountService.getEntityById(1L)).thenReturn(account);
        when(transactionRepository.save(any())).thenReturn(saved);

        TransactionResponse response = transactionService.create(1L, 3, new BigDecimal("50.00"));

        assertThat(response.getAmount()).isEqualByComparingTo(new BigDecimal("-50.00"));
        assertThat(response.getOperationTypeId()).isEqualTo(3);
    }

    // ----------------------------------------------------------------
    // Tipo positivo (4)
    // ----------------------------------------------------------------

    @Test
    void shouldCreateTransaction_withPositiveAmount_forPayment() {
        BigDecimal expectedAmount = new BigDecimal("300.00");
        Transaction saved = buildTransaction(account, 4, expectedAmount);

        when(accountService.getEntityById(1L)).thenReturn(account);
        when(transactionRepository.save(any())).thenReturn(saved);

        TransactionResponse response = transactionService.create(1L, 4, new BigDecimal("300.00"));

        assertThat(response.getAmount()).isEqualByComparingTo(new BigDecimal("300.00"));
        assertThat(response.getOperationTypeId()).isEqualTo(4);
    }

    // ----------------------------------------------------------------
    // Normalização de sinal — cliente envia valor com sinal errado
    // ----------------------------------------------------------------

    @Test
    void shouldNormalize_negativeSentByClient_toNegative_forPurchase() {
        // Cliente envia -100 para um tipo que já é negativo → resultado deve ser -100
        BigDecimal expectedAmount = new BigDecimal("-100.00");
        Transaction saved = buildTransaction(account, 1, expectedAmount);

        when(accountService.getEntityById(1L)).thenReturn(account);
        when(transactionRepository.save(any())).thenReturn(saved);

        TransactionResponse response = transactionService.create(1L, 1, new BigDecimal("-100.00"));

        assertThat(response.getAmount()).isEqualByComparingTo(new BigDecimal("-100.00"));
    }

    @Test
    void shouldNormalize_negativeSentByClient_toPositive_forPayment() {
        // Cliente envia -300 para um tipo positivo → resultado deve ser +300
        BigDecimal expectedAmount = new BigDecimal("300.00");
        Transaction saved = buildTransaction(account, 4, expectedAmount);

        when(accountService.getEntityById(1L)).thenReturn(account);
        when(transactionRepository.save(any())).thenReturn(saved);

        TransactionResponse response = transactionService.create(1L, 4, new BigDecimal("-300.00"));

        assertThat(response.getAmount()).isEqualByComparingTo(new BigDecimal("300.00"));
    }

    // ----------------------------------------------------------------
    // Campos da resposta
    // ----------------------------------------------------------------

    @Test
    void shouldReturnAllFieldsInResponse() {
        BigDecimal expectedAmount = new BigDecimal("-75.50");
        Transaction saved = buildTransaction(account, 1, expectedAmount);

        when(accountService.getEntityById(1L)).thenReturn(account);
        when(transactionRepository.save(any())).thenReturn(saved);

        TransactionResponse response = transactionService.create(1L, 1, new BigDecimal("75.50"));

        assertThat(response.getTransactionId()).isEqualTo(10L);
        assertThat(response.getAccountId()).isEqualTo(1L);
        assertThat(response.getOperationTypeId()).isEqualTo(1);
        assertThat(response.getAmount()).isEqualByComparingTo(new BigDecimal("-75.50"));
        assertThat(response.getEventDate()).isNotNull();
    }

    // ----------------------------------------------------------------
    // Erros
    // ----------------------------------------------------------------

    @Test
    void shouldThrowResourceNotFoundException_whenAccountDoesNotExist() {
        when(accountService.getEntityById(anyLong()))
                .thenThrow(new ResourceNotFoundException("account not found"));

        assertThatThrownBy(() -> transactionService.create(999L, 1, new BigDecimal("10.00")))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("account not found");

        verify(transactionRepository, never()).save(any());
    }

    @Test
    void shouldThrowIllegalArgumentException_whenOperationTypeIsInvalid() {
        when(accountService.getEntityById(1L)).thenReturn(account);

        assertThatThrownBy(() -> transactionService.create(1L, 99, new BigDecimal("10.00")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unsupported operation type: 99");

        verify(transactionRepository, never()).save(any());
    }
}

