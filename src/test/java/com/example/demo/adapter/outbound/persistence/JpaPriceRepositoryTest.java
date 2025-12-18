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
import java.util.Comparator;
import java.util.List;
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
            // Test 1: 10:00 on day 14 - Should return 1 price (priceList 1)
            Arguments.of(
                LocalDateTime.of(2020, 6, 14, 10, 0, 0),
                1,
                1,
                new BigDecimal("35.50")
            ),
            // Test 2: 16:00 on day 14 - Should return 2 prices, highest priority is priceList 2
            Arguments.of(
                LocalDateTime.of(2020, 6, 14, 16, 0, 0),
                2,
                2,
                new BigDecimal("25.45")
            ),
            // Test 3: 21:00 on day 14 - Should return 1 price (priceList 1)
            Arguments.of(
                LocalDateTime.of(2020, 6, 14, 21, 0, 0),
                1,
                1,
                new BigDecimal("35.50")
            ),
            // Test 4: 10:00 on day 15 - Should return 2 prices, highest priority is priceList 3
            Arguments.of(
                LocalDateTime.of(2020, 6, 15, 10, 0, 0),
                2,
                3,
                new BigDecimal("30.50")
            ),
            // Test 5: 21:00 on day 16 - Should return 2 prices, highest priority is priceList 4
            Arguments.of(
                LocalDateTime.of(2020, 6, 16, 21, 0, 0),
                2,
                4,
                new BigDecimal("38.95")
            )
        );
    }

    @ParameterizedTest(name = "Query at {0} should return {1} prices, highest priority priceList {2} with price {3}")
    @MethodSource("priceQueryTestCases")
    @DisplayName("Should return all matching prices for date range")
    void shouldReturnAllMatchingPricesForDateRange(
            LocalDateTime applicationDate,
            int expectedCount,
            Integer expectedHighestPriorityPriceList,
            BigDecimal expectedHighestPriorityPrice) {
        // When
        List<PricesEntity> results = jpaPriceRepository.findPricesByBrandAndProductAndApplicationDate(
                BRAND_ID, PRODUCT_ID, applicationDate);

        // Then
        assertThat(results).hasSize(expectedCount);

        // Verify highest priority price
        PricesEntity highestPriority = results.stream()
                .max(Comparator.comparingInt(PricesEntity::getPriority))
                .orElseThrow();
        assertThat(highestPriority.getPriceList()).isEqualTo(expectedHighestPriorityPriceList);
        assertThat(highestPriority.getPrice()).isEqualByComparingTo(expectedHighestPriorityPrice);
    }

    @Test
    @DisplayName("Should return empty list when no price found for date")
    void shouldReturnEmptyListWhenNoPriceFoundForDate() {
        // Given
        LocalDateTime dateWithNoPrice = LocalDateTime.of(2019, 1, 1, 10, 0, 0);

        // When
        List<PricesEntity> results = jpaPriceRepository.findPricesByBrandAndProductAndApplicationDate(
                BRAND_ID, PRODUCT_ID, dateWithNoPrice);

        // Then
        assertThat(results).isEmpty();
    }

    @Test
    @DisplayName("Should return empty list when brand not found")
    void shouldReturnEmptyListWhenBrandNotFound() {
        // Given
        Integer nonExistentBrandId = 999;
        LocalDateTime applicationDate = LocalDateTime.of(2020, 6, 14, 10, 0, 0);

        // When
        List<PricesEntity> results = jpaPriceRepository.findPricesByBrandAndProductAndApplicationDate(
                nonExistentBrandId, PRODUCT_ID, applicationDate);

        // Then
        assertThat(results).isEmpty();
    }

    @Test
    @DisplayName("Should return empty list when product not found")
    void shouldReturnEmptyListWhenProductNotFound() {
        // Given
        Integer nonExistentProductId = 99999;
        LocalDateTime applicationDate = LocalDateTime.of(2020, 6, 14, 10, 0, 0);

        // When
        List<PricesEntity> results = jpaPriceRepository.findPricesByBrandAndProductAndApplicationDate(
                BRAND_ID, nonExistentProductId, applicationDate);

        // Then
        assertThat(results).isEmpty();
    }

    @Test
    @DisplayName("Should return prices with correct brand and product relationships")
    void shouldReturnPricesWithCorrectRelationships() {
        // Given
        LocalDateTime applicationDate = LocalDateTime.of(2020, 6, 14, 10, 0, 0);

        // When
        List<PricesEntity> results = jpaPriceRepository.findPricesByBrandAndProductAndApplicationDate(
                BRAND_ID, PRODUCT_ID, applicationDate);

        // Then
        assertThat(results).isNotEmpty();
        results.forEach(result -> {
            assertThat(result.getBrand()).isNotNull();
            assertThat(result.getBrand().getId()).isEqualTo(BRAND_ID);
            assertThat(result.getProductsEntity()).isNotNull();
            assertThat(result.getProductsEntity().getId()).isEqualTo(PRODUCT_ID);
        });
    }
}

