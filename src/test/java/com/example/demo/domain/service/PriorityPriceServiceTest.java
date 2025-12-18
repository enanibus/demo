package com.example.demo.domain.service;

import com.example.demo.adapter.inbound.rest.dto.PriceRequestDTO;
import com.example.demo.domain.exception.ResourceNotFoundException;
import com.example.demo.domain.model.Currency;
import com.example.demo.domain.model.Money;
import com.example.demo.domain.model.Price;
import com.example.demo.domain.model.Rate;
import com.example.demo.domain.repository.PriceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PriorityPriceServiceTest {

    @Mock
    private PriceRepository priceRepository;

    @InjectMocks
    private PriorityPriceService priorityPriceService;

    private static final Integer BRAND_ID = 1;
    private static final Integer PRODUCT_ID = 35455;
    private static final LocalDateTime APPLICATION_DATE = LocalDateTime.of(2020, 6, 14, 10, 0, 0);

    private PriceRequestDTO request;
    private Price expectedPrice;

    @BeforeEach
    void setUp() {
        request = PriceRequestDTO.builder()
                .brandId(BRAND_ID)
                .productId(PRODUCT_ID)
                .applicationDate(APPLICATION_DATE)
                .build();

        Money money = new Money(new BigDecimal("35.50"), Currency.EUR);
        Rate rate = new Rate(
                1,
                LocalDateTime.of(2020, 6, 14, 0, 0, 0),
                LocalDateTime.of(2020, 12, 31, 23, 59, 59),
                0,
                money
        );
        expectedPrice = new Price(BRAND_ID, PRODUCT_ID, rate);
    }

    @Test
    @DisplayName("getPriorityPrice - Should return price when found")
    void getPriorityPrice_ShouldReturnPrice_WhenPriceExists() {
        // Given
        when(priceRepository.findPriceByBrandProductDate(BRAND_ID, PRODUCT_ID, APPLICATION_DATE))
                .thenReturn(expectedPrice);

        // When
        Price result = priorityPriceService.getPriorityPrice(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.brandId()).isEqualTo(BRAND_ID);
        assertThat(result.productId()).isEqualTo(PRODUCT_ID);
        assertThat(result.rate().priceList()).isEqualTo(1);
        assertThat(result.rate().price().amount()).isEqualByComparingTo(new BigDecimal("35.50"));
        assertThat(result.rate().price().currency()).isEqualTo(Currency.EUR);

        verify(priceRepository).findPriceByBrandProductDate(BRAND_ID, PRODUCT_ID, APPLICATION_DATE);
    }

    @Test
    @DisplayName("getPriorityPrice - Should throw ResourceNotFoundException when price not found")
    void getPriorityPrice_ShouldThrowException_WhenPriceNotFound() {
        // Given
        when(priceRepository.findPriceByBrandProductDate(BRAND_ID, PRODUCT_ID, APPLICATION_DATE))
                .thenThrow(new ResourceNotFoundException(
                        String.format("Price not found for brandId=%d, productId=%d, applicationDate=%s",
                                BRAND_ID, PRODUCT_ID, APPLICATION_DATE)));

        // When & Then
        assertThatThrownBy(() -> priorityPriceService.getPriorityPrice(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Price not found")
                .hasMessageContaining(BRAND_ID.toString())
                .hasMessageContaining(PRODUCT_ID.toString());

        verify(priceRepository).findPriceByBrandProductDate(BRAND_ID, PRODUCT_ID, APPLICATION_DATE);
    }

    @Test
    @DisplayName("getPriorityPrice - Should call repository with correct parameters")
    void getPriorityPrice_ShouldCallRepositoryWithCorrectParameters() {
        // Given
        when(priceRepository.findPriceByBrandProductDate(BRAND_ID, PRODUCT_ID, APPLICATION_DATE))
                .thenReturn(expectedPrice);

        // When
        priorityPriceService.getPriorityPrice(request);

        // Then
        verify(priceRepository).findPriceByBrandProductDate(BRAND_ID, PRODUCT_ID, APPLICATION_DATE);
    }

    @Test
    @DisplayName("getPriorityPrice - Should return price with higher priority rate")
    void getPriorityPrice_ShouldReturnPriceWithHigherPriority() {
        // Given
        LocalDateTime dateWithMultiplePrices = LocalDateTime.of(2020, 6, 14, 16, 0, 0);
        PriceRequestDTO requestWithMultiplePrices = PriceRequestDTO.builder()
                .brandId(BRAND_ID)
                .productId(PRODUCT_ID)
                .applicationDate(dateWithMultiplePrices)
                .build();

        Money money = new Money(new BigDecimal("25.45"), Currency.EUR);
        Rate highPriorityRate = new Rate(
                2,
                LocalDateTime.of(2020, 6, 14, 15, 0, 0),
                LocalDateTime.of(2020, 6, 14, 18, 30, 0),
                1,
                money
        );
        Price highPriorityPrice = new Price(BRAND_ID, PRODUCT_ID, highPriorityRate);

        when(priceRepository.findPriceByBrandProductDate(BRAND_ID, PRODUCT_ID, dateWithMultiplePrices))
                .thenReturn(highPriorityPrice);

        // When
        Price result = priorityPriceService.getPriorityPrice(requestWithMultiplePrices);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.rate().priceList()).isEqualTo(2);
        assertThat(result.rate().priority()).isEqualTo(1);
        assertThat(result.rate().price().amount()).isEqualByComparingTo(new BigDecimal("25.45"));
    }

    @Test
    @DisplayName("getPriorityPrice - Should return price with correct date range")
    void getPriorityPrice_ShouldReturnPriceWithCorrectDateRange() {
        // Given
        when(priceRepository.findPriceByBrandProductDate(BRAND_ID, PRODUCT_ID, APPLICATION_DATE))
                .thenReturn(expectedPrice);

        // When
        Price result = priorityPriceService.getPriorityPrice(request);

        // Then
        assertThat(result.rate().startDate()).isEqualTo(LocalDateTime.of(2020, 6, 14, 0, 0, 0));
        assertThat(result.rate().endDate()).isEqualTo(LocalDateTime.of(2020, 12, 31, 23, 59, 59));
    }
}

