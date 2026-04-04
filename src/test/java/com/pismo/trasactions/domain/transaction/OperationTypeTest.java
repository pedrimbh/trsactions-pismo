package com.pismo.trasactions.domain.transaction;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OperationTypeTest {

    // ----------------------------------------------------------------
    // fromId() — IDs válidos
    // ----------------------------------------------------------------

    @Test
    void shouldReturnCashPurchase_forId1() {
        assertThat(OperationType.fromId(1)).isEqualTo(OperationType.CASH_PURCHASE);
    }

    @Test
    void shouldReturnInstallmentPurchase_forId2() {
        assertThat(OperationType.fromId(2)).isEqualTo(OperationType.INSTALLMENT_PURCHASE);
    }

    @Test
    void shouldReturnWithdrawal_forId3() {
        assertThat(OperationType.fromId(3)).isEqualTo(OperationType.WITHDRAWAL);
    }

    @Test
    void shouldReturnPayment_forId4() {
        assertThat(OperationType.fromId(4)).isEqualTo(OperationType.PAYMENT);
    }

    // ----------------------------------------------------------------
    // isNegativeAmount()
    // ----------------------------------------------------------------

    @ParameterizedTest(name = "operação tipo {0} deve ter valor negativo")
    @ValueSource(ints = {1, 2, 3})
    void shouldBeNegativeAmount_forTypes1to3(int id) {
        assertThat(OperationType.fromId(id).isNegativeAmount()).isTrue();
    }

    @Test
    void shouldBePositiveAmount_forType4() {
        assertThat(OperationType.fromId(4).isNegativeAmount()).isFalse();
    }

    // ----------------------------------------------------------------
    // getId()
    // ----------------------------------------------------------------

    @Test
    void shouldReturnCorrectId_forEachType() {
        assertThat(OperationType.CASH_PURCHASE.getId()).isEqualTo(1);
        assertThat(OperationType.INSTALLMENT_PURCHASE.getId()).isEqualTo(2);
        assertThat(OperationType.WITHDRAWAL.getId()).isEqualTo(3);
        assertThat(OperationType.PAYMENT.getId()).isEqualTo(4);
    }

    // ----------------------------------------------------------------
    // fromId() — IDs inválidos
    // ----------------------------------------------------------------

    @Test
    void shouldThrowIllegalArgumentException_forInvalidId() {
        assertThatThrownBy(() -> OperationType.fromId(99))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unsupported operation type: 99");
    }

    @Test
    void shouldThrowIllegalArgumentException_forZeroId() {
        assertThatThrownBy(() -> OperationType.fromId(0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unsupported operation type: 0");
    }

    @Test
    void shouldThrowIllegalArgumentException_forNegativeId() {
        assertThatThrownBy(() -> OperationType.fromId(-1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unsupported operation type: -1");
    }
}

