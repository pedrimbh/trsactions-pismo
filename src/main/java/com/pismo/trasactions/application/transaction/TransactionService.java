package com.pismo.trasactions.application.transaction;

import com.pismo.trasactions.api.transaction.TransactionResponse;
import com.pismo.trasactions.application.account.AccountService;
import com.pismo.trasactions.domain.account.Account;
import com.pismo.trasactions.domain.transaction.OperationType;
import com.pismo.trasactions.domain.transaction.Transaction;
import com.pismo.trasactions.repository.TransactionRepository;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountService accountService;

    public TransactionService(TransactionRepository transactionRepository, AccountService accountService) {
        this.transactionRepository = transactionRepository;
        this.accountService = accountService;
    }

    @Transactional
    public TransactionResponse create(Long accountId, Integer operationTypeId, BigDecimal amount) {
        Account account = accountService.getEntityById(accountId);
        OperationType operationType = OperationType.fromId(operationTypeId);
        BigDecimal normalizedAmount = normalizeAmount(amount, operationType);

        Transaction transaction = transactionRepository.save(new Transaction(account, operationTypeId, normalizedAmount));
        return new TransactionResponse(
                transaction.getId(),
                transaction.getAccount().getId(),
                transaction.getOperationTypeId(),
                transaction.getAmount(),
                transaction.getEventDate());
    }

    private BigDecimal normalizeAmount(BigDecimal amount, OperationType operationType) {
        BigDecimal absolute = amount.abs();
        return operationType.isNegativeAmount() ? absolute.negate() : absolute;
    }
}

