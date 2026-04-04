package com.pismo.trasactions.api.account;

import com.pismo.trasactions.common.exception.ApiExceptionHandler;
import com.pismo.trasactions.common.exception.BusinessException;
import com.pismo.trasactions.common.exception.ResourceNotFoundException;
import com.pismo.trasactions.service.account.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AccountControllerTest {

    @Mock
    private AccountService accountService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new AccountController(accountService))
                .setControllerAdvice(new ApiExceptionHandler())
                .build();
    }

    // ----------------------------------------------------------------
    // POST /accounts
    // ----------------------------------------------------------------

    @Test
    void shouldCreateAccount_andReturn201() throws Exception {
        when(accountService.create("12345678900"))
                .thenReturn(new AccountResponse(1L, "12345678900"));

        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"document_number\":\"12345678900\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.account_id").value(1))
                .andExpect(jsonPath("$.document_number").value("12345678900"));
    }

    @Test
    void shouldReturn422_whenDocumentNumberAlreadyExists() throws Exception {
        when(accountService.create("12345678900"))
                .thenThrow(new BusinessException("document_number already exists"));

        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"document_number\":\"12345678900\"}"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.message").value("document_number already exists"));
    }

    @Test
    void shouldReturn400_whenDocumentNumberIsBlank() throws Exception {
        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"document_number\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void shouldReturn400_whenDocumentNumberHasInvalidFormat() throws Exception {
        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"document_number\":\"abc\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void shouldReturn400_whenDocumentNumberIsMissing() throws Exception {
        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    // ----------------------------------------------------------------
    // GET /accounts/{accountId}
    // ----------------------------------------------------------------

    @Test
    void shouldReturnAccount_andReturn200() throws Exception {
        when(accountService.findById(1L))
                .thenReturn(new AccountResponse(1L, "12345678900"));

        mockMvc.perform(get("/accounts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.account_id").value(1))
                .andExpect(jsonPath("$.document_number").value("12345678900"));
    }

    @Test
    void shouldReturn404_whenAccountDoesNotExist() throws Exception {
        when(accountService.findById(999L))
                .thenThrow(new ResourceNotFoundException("account not found"));

        mockMvc.perform(get("/accounts/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("account not found"));
    }
}
