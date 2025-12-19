package com.example.demo.adapter.outbound.persistence;

import com.example.demo.domain.model.Price;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PricePersistenceAdapterTest {

    @Mock
    private JpaPriceRepository jpaPriceRepository;

    private PricePersistenceAdapter pricePersistenceAdapter;

    private static final Integer BRAND_ID = 1;
    private static final Integer PRODUCT_ID = 35455;
    private static final LocalDateTime APPLICATION_DATE = LocalDateTime.of(2020, 6, 14, 10, 0, 0);

    @BeforeEach
    void setUp() {
        pricePersistenceAdapter = new PricePersistenceAdapter(jpaPriceRepository);
    }

    @Test
    @DisplayName("findPricesByBrandProductDate - Should return list of Prices when found")
    void findPricesByBrandProductDate_ShouldReturnPrices_WhenFound() {
        // Given
        PricesEntity entity = createPricesEntity(1, 0, new BigDecimal("35.50"));
        when(jpaPriceRepository.findPricesByBrandAndProductAndApplicationDate(BRAND_ID, PRODUCT_ID, APPLICATION_DATE))
                .thenReturn(List.of(entity));

        // When
        List<Price> result = pricePersistenceAdapter.findPricesByBrandProductDate(BRAND_ID, PRODUCT_ID, APPLICATION_DATE);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().brandId()).isEqualTo(BRAND_ID);
        assertThat(result.getFirst().productId()).isEqualTo(PRODUCT_ID);
        assertThat(result.getFirst().rate().priceList()).isEqualTo(1);
        assertThat(result.getFirst().rate().price().amount()).isEqualByComparingTo(new BigDecimal("35.50"));

        verify(jpaPriceRepository).findPricesByBrandAndProductAndApplicationDate(BRAND_ID, PRODUCT_ID, APPLICATION_DATE);
    }

    @Test
    @DisplayName("findPricesByBrandProductDate - Should return empty list when not found")
    void findPricesByBrandProductDate_ShouldReturnEmptyList_WhenNotFound() {
        // Given
        when(jpaPriceRepository.findPricesByBrandAndProductAndApplicationDate(BRAND_ID, PRODUCT_ID, APPLICATION_DATE))
                .thenReturn(Collections.emptyList());

        // When
        List<Price> result = pricePersistenceAdapter.findPricesByBrandProductDate(BRAND_ID, PRODUCT_ID, APPLICATION_DATE);

        // Then
        assertThat(result).isEmpty();

        verify(jpaPriceRepository).findPricesByBrandAndProductAndApplicationDate(BRAND_ID, PRODUCT_ID, APPLICATION_DATE);
    }

    @Test
    @DisplayName("findPricesByBrandProductDate - Should return multiple prices when overlapping date ranges")
    void findPricesByBrandProductDate_ShouldReturnMultiplePrices_WhenOverlappingDateRanges() {
        // Given
        PricesEntity entity1 = createPricesEntity(1, 0, new BigDecimal("35.50"));
        PricesEntity entity2 = createPricesEntity(2, 1, new BigDecimal("25.45"));
        when(jpaPriceRepository.findPricesByBrandAndProductAndApplicationDate(BRAND_ID, PRODUCT_ID, APPLICATION_DATE))
                .thenReturn(List.of(entity1, entity2));

        // When
        List<Price> result = pricePersistenceAdapter.findPricesByBrandProductDate(BRAND_ID, PRODUCT_ID, APPLICATION_DATE);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(price -> price.rate().priceList()).containsExactlyInAnyOrder(1, 2);
        assertThat(result).extracting(price -> price.rate().priority()).containsExactlyInAnyOrder(0, 1);
    }

    @Test
    @DisplayName("findPricesByBrandProductDate - Should map all entities to domain correctly")
    void findPricesByBrandProductDate_ShouldMapEntitiesToDomainCorrectly() {
        // Given
        PricesEntity entity = createPricesEntity(1, 0, new BigDecimal("35.50"));
        when(jpaPriceRepository.findPricesByBrandAndProductAndApplicationDate(BRAND_ID, PRODUCT_ID, APPLICATION_DATE))
                .thenReturn(List.of(entity));

        // When
        List<Price> result = pricePersistenceAdapter.findPricesByBrandProductDate(BRAND_ID, PRODUCT_ID, APPLICATION_DATE);

        // Then
        assertThat(result).hasSize(1);
        Price price = result.get(0);
        assertThat(price.rate().startDate()).isEqualTo(LocalDateTime.of(2020, 6, 14, 0, 0, 0));
        assertThat(price.rate().endDate()).isEqualTo(LocalDateTime.of(2020, 12, 31, 23, 59, 59));
        assertThat(price.rate().priority()).isEqualTo(0);
        assertThat(price.rate().price().currency().name()).isEqualTo("EUR");
    }

    private PricesEntity createPricesEntity(Integer priceList, Integer priority, BigDecimal price) {
        BrandsEntity brand = BrandsEntity.builder()
                .id(BRAND_ID)
                .chainName("ZARA")
                .build();

        ProductsEntity product = ProductsEntity.builder()
                .id(PRODUCT_ID)
                .productName("Test Product")
                .build();

        return PricesEntity.builder()
                .priceList(priceList)
                .brand(brand)
                .productsEntity(product)
                .startDate(LocalDateTime.of(2020, 6, 14, 0, 0, 0))
                .endDate(LocalDateTime.of(2020, 12, 31, 23, 59, 59))
                .priority(priority)
                .price(price)
                .currency("EUR")
                .build();
    }
}

