package com.pismo.trasactions.api.transaction;

import com.pismo.trasactions.common.exception.ApiExceptionHandler;
import com.pismo.trasactions.common.exception.ResourceNotFoundException;
import com.pismo.trasactions.service.transaction.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {

    @Mock
    private TransactionService transactionService;

    private MockMvc mockMvc;

    private static final LocalDateTime NOW = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new TransactionController(transactionService))
                .setControllerAdvice(new ApiExceptionHandler())
                .build();
    }

    // ----------------------------------------------------------------
    // POST /transactions — sucesso
    // ----------------------------------------------------------------

    @Test
    void shouldCreateTransaction_withNegativeAmount_forPurchase() throws Exception {
        when(transactionService.create(eq(1L), eq(1), any(BigDecimal.class)))
                .thenReturn(new TransactionResponse(10L, 1L, 1, new BigDecimal("-100.00"), NOW));

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"account_id\":1,\"operation_type_id\":1,\"amount\":100.00}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.transaction_id").value(10))
                .andExpect(jsonPath("$.account_id").value(1))
                .andExpect(jsonPath("$.operation_type_id").value(1))
                .andExpect(jsonPath("$.amount").value(-100.00));
    }

    @Test
    void shouldCreateTransaction_withPositiveAmount_forPayment() throws Exception {
        when(transactionService.create(eq(1L), eq(4), any(BigDecimal.class)))
                .thenReturn(new TransactionResponse(11L, 1L, 4, new BigDecimal("60.00"), NOW));

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"account_id\":1,\"operation_type_id\":4,\"amount\":60.00}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.amount").value(60.00))
                .andExpect(jsonPath("$.operation_type_id").value(4));
    }

    @Test
    void shouldReturnEventDate_inResponse() throws Exception {
        when(transactionService.create(anyLong(), anyInt(), any(BigDecimal.class)))
                .thenReturn(new TransactionResponse(12L, 1L, 1, new BigDecimal("-50.00"), NOW));

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"account_id\":1,\"operation_type_id\":1,\"amount\":50.00}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.event_date").isNotEmpty());
    }

    // ----------------------------------------------------------------
    // POST /transactions — erros de negócio
    // ----------------------------------------------------------------

    @Test
    void shouldReturn404_whenAccountDoesNotExist() throws Exception {
        when(transactionService.create(anyLong(), anyInt(), any(BigDecimal.class)))
                .thenThrow(new ResourceNotFoundException("account not found"));

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"account_id\":999,\"operation_type_id\":1,\"amount\":100.00}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("account not found"));
    }

    @Test
    void shouldReturn400_whenOperationTypeIsInvalid() throws Exception {
        when(transactionService.create(anyLong(), anyInt(), any(BigDecimal.class)))
                .thenThrow(new IllegalArgumentException("Unsupported operation type: 99"));

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"account_id\":1,\"operation_type_id\":99,\"amount\":100.00}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Unsupported operation type: 99"));
    }

    // ----------------------------------------------------------------
    // POST /transactions — validação de campos (@Valid)
    // ----------------------------------------------------------------

    @Test
    void shouldReturn400_whenAccountIdIsNull() throws Exception {
        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"operation_type_id\":1,\"amount\":100.00}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void shouldReturn400_whenOperationTypeIdIsNull() throws Exception {
        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"account_id\":1,\"amount\":100.00}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void shouldReturn400_whenAmountIsNull() throws Exception {
        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"account_id\":1,\"operation_type_id\":1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void shouldReturn400_whenAmountIsZero() throws Exception {
        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"account_id\":1,\"operation_type_id\":1,\"amount\":0}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }
}
