package com.example.demo.adapter.outbound.persistence;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class JpaPriceRepositoryTest {

    @Autowired
    private JpaPriceRepository jpaPriceRepository;

    private static final Integer BRAND_ID = 1;
    private static final Integer PRODUCT_ID = 35455;

    static Stream<Arguments> priceQueryTestCases() {
        return Stream.of(
            // Test 1: 10:00 on day 14 - Should return priceList 1
            Arguments.of(
                LocalDateTime.of(2020, 6, 14, 10, 0, 0),
                1,
                new BigDecimal("35.50")
            ),
            // Test 2: 16:00 on day 14 - Should return priceList 2 (higher priority)
            Arguments.of(
                LocalDateTime.of(2020, 6, 14, 16, 0, 0),
                2,
                new BigDecimal("25.45")
            ),
            // Test 3: 21:00 on day 14 - Should return priceList 1
            Arguments.of(
                LocalDateTime.of(2020, 6, 14, 21, 0, 0),
                1,
                new BigDecimal("35.50")
            ),
            // Test 4: 10:00 on day 15 - Should return priceList 3 (higher priority)
            Arguments.of(
                LocalDateTime.of(2020, 6, 15, 10, 0, 0),
                3,
                new BigDecimal("30.50")
            ),
            // Test 5: 21:00 on day 16 - Should return priceList 4 (higher priority)
            Arguments.of(
                LocalDateTime.of(2020, 6, 16, 21, 0, 0),
                4,
                new BigDecimal("38.95")
            )
        );
    }

    @ParameterizedTest(name = "Query at {0} should return priceList {1} with price {2}")
    @MethodSource("priceQueryTestCases")
    @DisplayName("Should return correct price based on date and priority")
    void shouldReturnCorrectPriceBasedOnDateAndPriority(
            LocalDateTime applicationDate,
            Integer expectedPriceList,
            BigDecimal expectedPrice) {
        // When
        Optional<PricesEntity> result = jpaPriceRepository.findPricesByBrandAndProductAndApplicationDate(
                BRAND_ID, PRODUCT_ID, applicationDate);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getPriceList()).isEqualTo(expectedPriceList);
        assertThat(result.get().getPrice()).isEqualByComparingTo(expectedPrice);
    }

    @Test
    @DisplayName("Should return empty when no price found for date")
    void shouldReturnEmptyWhenNoPriceFoundForDate() {
        // Given
        LocalDateTime dateWithNoPrice = LocalDateTime.of(2019, 1, 1, 10, 0, 0);

        // When
        Optional<PricesEntity> result = jpaPriceRepository.findPricesByBrandAndProductAndApplicationDate(
                BRAND_ID, PRODUCT_ID, dateWithNoPrice);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should return empty when brand not found")
    void shouldReturnEmptyWhenBrandNotFound() {
        // Given
        Integer nonExistentBrandId = 999;
        LocalDateTime applicationDate = LocalDateTime.of(2020, 6, 14, 10, 0, 0);

        // When
        Optional<PricesEntity> result = jpaPriceRepository.findPricesByBrandAndProductAndApplicationDate(
                nonExistentBrandId, PRODUCT_ID, applicationDate);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should return empty when product not found")
    void shouldReturnEmptyWhenProductNotFound() {
        // Given
        Integer nonExistentProductId = 99999;
        LocalDateTime applicationDate = LocalDateTime.of(2020, 6, 14, 10, 0, 0);

        // When
        Optional<PricesEntity> result = jpaPriceRepository.findPricesByBrandAndProductAndApplicationDate(
                BRAND_ID, nonExistentProductId, applicationDate);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should return price with correct brand and product relationships")
    void shouldReturnPriceWithCorrectRelationships() {
        // Given
        LocalDateTime applicationDate = LocalDateTime.of(2020, 6, 14, 10, 0, 0);

        // When
        Optional<PricesEntity> result = jpaPriceRepository.findPricesByBrandAndProductAndApplicationDate(
                BRAND_ID, PRODUCT_ID, applicationDate);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getBrand()).isNotNull();
        assertThat(result.get().getBrand().getId()).isEqualTo(BRAND_ID);
        assertThat(result.get().getProductsEntity()).isNotNull();
        assertThat(result.get().getProductsEntity().getId()).isEqualTo(PRODUCT_ID);
    }
}

