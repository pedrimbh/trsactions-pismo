package com.pismo.trasactions.application.account;

import com.pismo.trasactions.api.account.AccountResponse;
import com.pismo.trasactions.common.exception.BusinessException;
import com.pismo.trasactions.common.exception.ResourceNotFoundException;
import com.pismo.trasactions.domain.account.Account;
import com.pismo.trasactions.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional
    public AccountResponse create(String documentNumber) {
        accountRepository.findByDocumentNumber(documentNumber).ifPresent(account -> {
            throw new BusinessException("document_number already exists");
        });

        Account account = accountRepository.save(new Account(documentNumber));
        return toResponse(account);
    }

    @Transactional(readOnly = true)
    public AccountResponse findById(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("account not found"));
        return toResponse(account);
    }

    @Transactional(readOnly = true)
    public Account getEntityById(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("account not found"));
    }

    private AccountResponse toResponse(Account account) {
        return new AccountResponse(account.getId(), account.getDocumentNumber());
    }
}

