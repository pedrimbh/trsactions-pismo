package com.pismo.trasactions.domain.transaction;

public enum OperationType {
    CASH_PURCHASE(1, true),
    INSTALLMENT_PURCHASE(2, true),
    WITHDRAWAL(3, true),
    PAYMENT(4, false);

    private final int id;
    private final boolean negativeAmount;

    OperationType(int id, boolean negativeAmount) {
        this.id = id;
        this.negativeAmount = negativeAmount;
    }

    public int getId() {
        return id;
    }

    public boolean isNegativeAmount() {
        return negativeAmount;
    }

    public static OperationType fromId(int id) {
        for (OperationType operationType : values()) {
            if (operationType.id == id) {
                return operationType;
            }
        }
        throw new IllegalArgumentException("Unsupported operation type: " + id);
    }
}

