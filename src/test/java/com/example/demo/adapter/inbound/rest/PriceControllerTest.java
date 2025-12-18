package com.example.demo.adapter.inbound.rest;

import com.example.demo.adapter.inbound.rest.dto.PriceRequestDTO;
import com.example.demo.application.usecase.PriorityPriceUseCase;
import com.example.demo.domain.exception.ResourceNotFoundException;
import com.example.demo.domain.model.Currency;
import com.example.demo.domain.model.Money;
import com.example.demo.domain.model.Price;
import com.example.demo.domain.model.Rate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PriceController.class)
class PriceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PriorityPriceUseCase priorityPriceUseCase;

    private static final String BASE_URL = "/brand/{brandId}/product/{productId}/prices";

    @Test
    @DisplayName("GET /brand/{brandId}/product/{productId}/prices - 200 OK - Returns price successfully")
    void getPriorityPrice_ReturnsOk_WhenPriceExists() throws Exception {
        // Given
        Integer brandId = 1;
        Integer productId = 35455;
        LocalDateTime applicationDate = LocalDateTime.of(2020, 6, 14, 10, 0, 0);

        Money money = new Money(new BigDecimal("35.50"), Currency.EUR);
        Rate rate = new Rate(1,
                LocalDateTime.of(2020, 6, 14, 0, 0, 0),
                LocalDateTime.of(2020, 12, 31, 23, 59, 59),
                0,
                money);
        Price price = new Price(brandId, productId, rate);

        when(priorityPriceUseCase.getPriorityPrice(any(PriceRequestDTO.class))).thenReturn(price);

        // When & Then
        mockMvc.perform(get(BASE_URL, brandId, productId)
                        .param("applicationDate", applicationDate.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(productId))
                .andExpect(jsonPath("$.brandId").value(brandId))
                .andExpect(jsonPath("$.priceList").value(1))
                .andExpect(jsonPath("$.finalPrice").value("35.50 EUR"));
    }

    @Test
    @DisplayName("GET /brand/{brandId}/product/{productId}/prices - 200 OK - Returns price with higher priority")
    void getPriorityPrice_ReturnsOk_WhenMultiplePricesExist() throws Exception {
        // Given
        Integer brandId = 1;
        Integer productId = 35455;
        LocalDateTime applicationDate = LocalDateTime.of(2020, 6, 14, 16, 0, 0);

        Money money = new Money(new BigDecimal("25.45"), Currency.EUR);
        Rate rate = new Rate(2,
                LocalDateTime.of(2020, 6, 14, 15, 0, 0),
                LocalDateTime.of(2020, 6, 14, 18, 30, 0),
                1,
                money);
        Price price = new Price(brandId, productId, rate);

        when(priorityPriceUseCase.getPriorityPrice(any(PriceRequestDTO.class))).thenReturn(price);

        // When & Then
        mockMvc.perform(get(BASE_URL, brandId, productId)
                        .param("applicationDate", applicationDate.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(productId))
                .andExpect(jsonPath("$.brandId").value(brandId))
                .andExpect(jsonPath("$.priceList").value(2))
                .andExpect(jsonPath("$.finalPrice").value("25.45 EUR"));
    }

    @Test
    @DisplayName("GET /brand/{brandId}/product/{productId}/prices - 400 Bad Request - Missing applicationDate parameter")
    void getPriorityPrice_ReturnsBadRequest_WhenApplicationDateIsMissing() throws Exception {
        // Given
        Integer brandId = 1;
        Integer productId = 35455;

        // When & Then
        mockMvc.perform(get(BASE_URL, brandId, productId))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /brand/{brandId}/product/{productId}/prices - 400 Bad Request - Invalid applicationDate format")
    void getPriorityPrice_ReturnsBadRequest_WhenApplicationDateFormatIsInvalid() throws Exception {
        // Given
        Integer brandId = 1;
        Integer productId = 35455;

        // When & Then
        mockMvc.perform(get(BASE_URL, brandId, productId)
                        .param("applicationDate", "invalid-date-format"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /brand/{brandId}/product/{productId}/prices - 400 Bad Request - Invalid brandId type")
    void getPriorityPrice_ReturnsBadRequest_WhenBrandIdIsInvalid() throws Exception {
        // When & Then
        mockMvc.perform(get("/brand/{brandId}/product/{productId}/prices", "invalid", 35455)
                        .param("applicationDate", "2020-06-14T10:00:00"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /brand/{brandId}/product/{productId}/prices - 400 Bad Request - Invalid productId type")
    void getPriorityPrice_ReturnsBadRequest_WhenProductIdIsInvalid() throws Exception {
        // When & Then
        mockMvc.perform(get("/brand/{brandId}/product/{productId}/prices", 1, "invalid")
                        .param("applicationDate", "2020-06-14T10:00:00"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /brand/{brandId}/product/{productId}/prices - 404 Not Found - Price not found")
    void getPriorityPrice_ReturnsNotFound_WhenPriceDoesNotExist() throws Exception {
        // Given
        Integer brandId = 1;
        Integer productId = 99999;
        LocalDateTime applicationDate = LocalDateTime.of(2020, 6, 14, 10, 0, 0);

        when(priorityPriceUseCase.getPriorityPrice(any(PriceRequestDTO.class)))
                .thenThrow(new ResourceNotFoundException(
                        String.format("Price not found for brandId=%d, productId=%d, applicationDate=%s",
                                brandId, productId, applicationDate)));

        // When & Then
        mockMvc.perform(get(BASE_URL, brandId, productId)
                        .param("applicationDate", applicationDate.toString()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("GET /brand/{brandId}/product/{productId}/prices - 404 Not Found - Brand not found")
    void getPriorityPrice_ReturnsNotFound_WhenBrandDoesNotExist() throws Exception {
        // Given
        Integer brandId = 999;
        Integer productId = 35455;
        LocalDateTime applicationDate = LocalDateTime.of(2020, 6, 14, 10, 0, 0);

        when(priorityPriceUseCase.getPriorityPrice(any(PriceRequestDTO.class)))
                .thenThrow(new ResourceNotFoundException(
                        String.format("Price not found for brandId=%d, productId=%d, applicationDate=%s",
                                brandId, productId, applicationDate)));

        // When & Then
        mockMvc.perform(get(BASE_URL, brandId, productId)
                        .param("applicationDate", applicationDate.toString()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value(String.format(
                        "Price not found for brandId=%d, productId=%d, applicationDate=%s",
                        brandId, productId, applicationDate)));
    }

    @Test
    @DisplayName("GET /brand/{brandId}/product/{productId}/prices - 404 Not Found - No price for given date")
    void getPriorityPrice_ReturnsNotFound_WhenNoPriceForDate() throws Exception {
        // Given
        Integer brandId = 1;
        Integer productId = 35455;
        LocalDateTime applicationDate = LocalDateTime.of(2019, 1, 1, 10, 0, 0);

        when(priorityPriceUseCase.getPriorityPrice(any(PriceRequestDTO.class)))
                .thenThrow(new ResourceNotFoundException(
                        String.format("Price not found for brandId=%d, productId=%d, applicationDate=%s",
                                brandId, productId, applicationDate)));

        // When & Then
        mockMvc.perform(get(BASE_URL, brandId, productId)
                        .param("applicationDate", applicationDate.toString()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("GET /brand/{brandId}/product/{productId}/prices - 500 Internal Server Error - Unexpected exception")
    void getPriorityPrice_ReturnsInternalServerError_WhenUnexpectedExceptionOccurs() throws Exception {
        // Given
        Integer brandId = 1;
        Integer productId = 35455;
        LocalDateTime applicationDate = LocalDateTime.of(2020, 6, 14, 10, 0, 0);

        when(priorityPriceUseCase.getPriorityPrice(any(PriceRequestDTO.class)))
                .thenThrow(new RuntimeException("Database connection failed"));

        // When & Then
        mockMvc.perform(get(BASE_URL, brandId, productId)
                        .param("applicationDate", applicationDate.toString()))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("An unexpected error occurred"));
    }

    @Test
    @DisplayName("GET /brand/{brandId}/product/{productId}/prices - 500 Internal Server Error - NullPointerException")
    void getPriorityPrice_ReturnsInternalServerError_WhenNullPointerExceptionOccurs() throws Exception {
        // Given
        Integer brandId = 1;
        Integer productId = 35455;
        LocalDateTime applicationDate = LocalDateTime.of(2020, 6, 14, 10, 0, 0);

        when(priorityPriceUseCase.getPriorityPrice(any(PriceRequestDTO.class)))
                .thenThrow(new NullPointerException("Unexpected null value"));

        // When & Then
        mockMvc.perform(get(BASE_URL, brandId, productId)
                        .param("applicationDate", applicationDate.toString()))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("An unexpected error occurred"))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}

