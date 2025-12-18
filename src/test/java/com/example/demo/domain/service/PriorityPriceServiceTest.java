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
import java.util.Collections;
import java.util.List;

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
    private Price lowPriorityPrice;
    private Price highPriorityPrice;

    @BeforeEach
    void setUp() {
        request = PriceRequestDTO.builder()
                .brandId(BRAND_ID)
                .productId(PRODUCT_ID)
                .applicationDate(APPLICATION_DATE)
                .build();

        Money lowPriorityMoney = new Money(new BigDecimal("35.50"), Currency.EUR);
        Rate lowPriorityRate = new Rate(
                1,
                LocalDateTime.of(2020, 6, 14, 0, 0, 0),
                LocalDateTime.of(2020, 12, 31, 23, 59, 59),
                0,
                lowPriorityMoney
        );
        lowPriorityPrice = new Price(BRAND_ID, PRODUCT_ID, lowPriorityRate);

        Money highPriorityMoney = new Money(new BigDecimal("25.45"), Currency.EUR);
        Rate highPriorityRate = new Rate(
                2,
                LocalDateTime.of(2020, 6, 14, 15, 0, 0),
                LocalDateTime.of(2020, 6, 14, 18, 30, 0),
                1,
                highPriorityMoney
        );
        highPriorityPrice = new Price(BRAND_ID, PRODUCT_ID, highPriorityRate);
    }

    @Test
    @DisplayName("getPriorityPrice - Should return price when single price found")
    void getPriorityPrice_ShouldReturnPrice_WhenSinglePriceExists() {
        // Given
        when(priceRepository.findPricesByBrandProductDate(BRAND_ID, PRODUCT_ID, APPLICATION_DATE))
                .thenReturn(List.of(lowPriorityPrice));

        // When
        Price result = priorityPriceService.getPriorityPrice(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.brandId()).isEqualTo(BRAND_ID);
        assertThat(result.productId()).isEqualTo(PRODUCT_ID);
        assertThat(result.rate().priceList()).isEqualTo(1);
        assertThat(result.rate().price().amount()).isEqualByComparingTo(new BigDecimal("35.50"));
        assertThat(result.rate().price().currency()).isEqualTo(Currency.EUR);

        verify(priceRepository).findPricesByBrandProductDate(BRAND_ID, PRODUCT_ID, APPLICATION_DATE);
    }

    @Test
    @DisplayName("getPriorityPrice - Should throw ResourceNotFoundException when no prices found")
    void getPriorityPrice_ShouldThrowException_WhenNoPricesFound() {
        // Given
        when(priceRepository.findPricesByBrandProductDate(BRAND_ID, PRODUCT_ID, APPLICATION_DATE))
                .thenReturn(Collections.emptyList());

        // When & Then
        assertThatThrownBy(() -> priorityPriceService.getPriorityPrice(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Price not found")
                .hasMessageContaining(BRAND_ID.toString())
                .hasMessageContaining(PRODUCT_ID.toString());

        verify(priceRepository).findPricesByBrandProductDate(BRAND_ID, PRODUCT_ID, APPLICATION_DATE);
    }

    @Test
    @DisplayName("getPriorityPrice - Should call repository with correct parameters")
    void getPriorityPrice_ShouldCallRepositoryWithCorrectParameters() {
        // Given
        when(priceRepository.findPricesByBrandProductDate(BRAND_ID, PRODUCT_ID, APPLICATION_DATE))
                .thenReturn(List.of(lowPriorityPrice));

        // When
        priorityPriceService.getPriorityPrice(request);

        // Then
        verify(priceRepository).findPricesByBrandProductDate(BRAND_ID, PRODUCT_ID, APPLICATION_DATE);
    }

    @Test
    @DisplayName("getPriorityPrice - Should select highest priority when multiple prices exist")
    void getPriorityPrice_ShouldSelectHighestPriority_WhenMultiplePricesExist() {
        // Given
        when(priceRepository.findPricesByBrandProductDate(BRAND_ID, PRODUCT_ID, APPLICATION_DATE))
                .thenReturn(List.of(lowPriorityPrice, highPriorityPrice));

        // When
        Price result = priorityPriceService.getPriorityPrice(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.rate().priceList()).isEqualTo(2);
        assertThat(result.rate().priority()).isEqualTo(1);
        assertThat(result.rate().price().amount()).isEqualByComparingTo(new BigDecimal("25.45"));

        verify(priceRepository).findPricesByBrandProductDate(BRAND_ID, PRODUCT_ID, APPLICATION_DATE);
    }

    @Test
    @DisplayName("getPriorityPrice - Should select highest priority regardless of list order")
    void getPriorityPrice_ShouldSelectHighestPriority_RegardlessOfListOrder() {
        // Given - high priority price comes first in the list
        when(priceRepository.findPricesByBrandProductDate(BRAND_ID, PRODUCT_ID, APPLICATION_DATE))
                .thenReturn(List.of(highPriorityPrice, lowPriorityPrice));

        // When
        Price result = priorityPriceService.getPriorityPrice(request);

        // Then
        assertThat(result.rate().priceList()).isEqualTo(2);
        assertThat(result.rate().priority()).isEqualTo(1);
    }

    @Test
    @DisplayName("getPriorityPrice - Should return price with correct date range")
    void getPriorityPrice_ShouldReturnPriceWithCorrectDateRange() {
        // Given
        when(priceRepository.findPricesByBrandProductDate(BRAND_ID, PRODUCT_ID, APPLICATION_DATE))
                .thenReturn(List.of(lowPriorityPrice));

        // When
        Price result = priorityPriceService.getPriorityPrice(request);

        // Then
        assertThat(result.rate().startDate()).isEqualTo(LocalDateTime.of(2020, 6, 14, 0, 0, 0));
        assertThat(result.rate().endDate()).isEqualTo(LocalDateTime.of(2020, 12, 31, 23, 59, 59));
    }
}

