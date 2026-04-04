package com.pismo.trasactions.service.account;

import com.pismo.trasactions.api.account.AccountResponse;
import com.pismo.trasactions.common.exception.BusinessException;
import com.pismo.trasactions.common.exception.ResourceNotFoundException;
import com.pismo.trasactions.domain.account.Account;
import com.pismo.trasactions.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    private Account savedAccount;

    @BeforeEach
    void setUp() {
        savedAccount = new Account("12345678900");
        // Simula o ID gerado pelo banco
        try {
            var field = Account.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(savedAccount, 1L);
        } catch (Exception ignored) {}
    }

    // ----------------------------------------------------------------
    // create()
    // ----------------------------------------------------------------

    @Test
    void shouldCreateAccount_whenDocumentNumberIsUnique() {
        when(accountRepository.findByDocumentNumber("12345678900")).thenReturn(Optional.empty());
        when(accountRepository.save(any(Account.class))).thenReturn(savedAccount);

        AccountResponse response = accountService.create("12345678900");

        assertThat(response).isNotNull();
        assertThat(response.getAccountId()).isEqualTo(1L);
        assertThat(response.getDocumentNumber()).isEqualTo("12345678900");
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void shouldThrowBusinessException_whenDocumentNumberAlreadyExists() {
        when(accountRepository.findByDocumentNumber("12345678900"))
                .thenReturn(Optional.of(savedAccount));

        assertThatThrownBy(() -> accountService.create("12345678900"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("document_number already exists");

        verify(accountRepository, never()).save(any());
    }

    @Test
    void shouldCreateTwoAccounts_withDifferentDocumentNumbers() {
        Account second = new Account("99988877766");
        try {
            var field = Account.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(second, 2L);
        } catch (Exception ignored) {}

        when(accountRepository.findByDocumentNumber(anyString())).thenReturn(Optional.empty());
        when(accountRepository.save(any(Account.class)))
                .thenReturn(savedAccount)
                .thenReturn(second);

        AccountResponse first = accountService.create("12345678900");
        AccountResponse secondResponse = accountService.create("99988877766");

        assertThat(first.getDocumentNumber()).isEqualTo("12345678900");
        assertThat(secondResponse.getDocumentNumber()).isEqualTo("99988877766");
        assertThat(first.getAccountId()).isNotEqualTo(secondResponse.getAccountId());
    }

    // ----------------------------------------------------------------
    // findById()
    // ----------------------------------------------------------------

    @Test
    void shouldReturnAccount_whenIdExists() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(savedAccount));

        AccountResponse response = accountService.findById(1L);

        assertThat(response.getAccountId()).isEqualTo(1L);
        assertThat(response.getDocumentNumber()).isEqualTo("12345678900");
    }

    @Test
    void shouldThrowResourceNotFoundException_whenIdDoesNotExist() {
        when(accountRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.findById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("account not found");
    }

    // ----------------------------------------------------------------
    // getEntityById()
    // ----------------------------------------------------------------

    @Test
    void shouldReturnAccountEntity_whenIdExists() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(savedAccount));

        Account entity = accountService.getEntityById(1L);

        assertThat(entity).isEqualTo(savedAccount);
    }

    @Test
    void shouldThrowResourceNotFoundException_whenEntityIdDoesNotExist() {
        when(accountRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.getEntityById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("account not found");
    }
}

