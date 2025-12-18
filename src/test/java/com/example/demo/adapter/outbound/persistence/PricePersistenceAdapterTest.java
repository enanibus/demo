package com.example.demo.adapter.outbound.persistence;

import com.example.demo.domain.exception.ResourceNotFoundException;
import com.example.demo.domain.model.Price;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
    @DisplayName("findPriceByBrandProductDate - Should return Price when found")
    void findPriceByBrandProductDate_ShouldReturnPrice_WhenFound() {
        // Given
        PricesEntity entity = createPricesEntity();
        when(jpaPriceRepository.findPricesByBrandAndProductAndApplicationDate(BRAND_ID, PRODUCT_ID, APPLICATION_DATE))
                .thenReturn(Optional.of(entity));

        // When
        Price result = pricePersistenceAdapter.findPriceByBrandProductDate(BRAND_ID, PRODUCT_ID, APPLICATION_DATE);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.brandId()).isEqualTo(BRAND_ID);
        assertThat(result.productId()).isEqualTo(PRODUCT_ID);
        assertThat(result.rate().priceList()).isEqualTo(1);
        assertThat(result.rate().price().amount()).isEqualByComparingTo(new BigDecimal("35.50"));

        verify(jpaPriceRepository).findPricesByBrandAndProductAndApplicationDate(BRAND_ID, PRODUCT_ID, APPLICATION_DATE);
    }

    @Test
    @DisplayName("findPriceByBrandProductDate - Should throw ResourceNotFoundException when not found")
    void findPriceByBrandProductDate_ShouldThrowException_WhenNotFound() {
        // Given
        when(jpaPriceRepository.findPricesByBrandAndProductAndApplicationDate(BRAND_ID, PRODUCT_ID, APPLICATION_DATE))
                .thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> pricePersistenceAdapter.findPriceByBrandProductDate(BRAND_ID, PRODUCT_ID, APPLICATION_DATE))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Price not found")
                .hasMessageContaining(BRAND_ID.toString())
                .hasMessageContaining(PRODUCT_ID.toString())
                .hasMessageContaining(APPLICATION_DATE.toString());

        verify(jpaPriceRepository).findPricesByBrandAndProductAndApplicationDate(BRAND_ID, PRODUCT_ID, APPLICATION_DATE);
    }

    @Test
    @DisplayName("findPriceByBrandProductDate - Should call repository with correct parameters")
    void findPriceByBrandProductDate_ShouldCallRepositoryWithCorrectParameters() {
        // Given
        PricesEntity entity = createPricesEntity();
        when(jpaPriceRepository.findPricesByBrandAndProductAndApplicationDate(BRAND_ID, PRODUCT_ID, APPLICATION_DATE))
                .thenReturn(Optional.of(entity));

        // When
        pricePersistenceAdapter.findPriceByBrandProductDate(BRAND_ID, PRODUCT_ID, APPLICATION_DATE);

        // Then
        verify(jpaPriceRepository).findPricesByBrandAndProductAndApplicationDate(BRAND_ID, PRODUCT_ID, APPLICATION_DATE);
    }

    @Test
    @DisplayName("findPriceByBrandProductDate - Should map entity to domain correctly")
    void findPriceByBrandProductDate_ShouldMapEntityToDomainCorrectly() {
        // Given
        PricesEntity entity = createPricesEntity();
        when(jpaPriceRepository.findPricesByBrandAndProductAndApplicationDate(BRAND_ID, PRODUCT_ID, APPLICATION_DATE))
                .thenReturn(Optional.of(entity));

        // When
        Price result = pricePersistenceAdapter.findPriceByBrandProductDate(BRAND_ID, PRODUCT_ID, APPLICATION_DATE);

        // Then
        assertThat(result.rate().startDate()).isEqualTo(LocalDateTime.of(2020, 6, 14, 0, 0, 0));
        assertThat(result.rate().endDate()).isEqualTo(LocalDateTime.of(2020, 12, 31, 23, 59, 59));
        assertThat(result.rate().priority()).isEqualTo(0);
        assertThat(result.rate().price().currency().name()).isEqualTo("EUR");
    }

    private PricesEntity createPricesEntity() {
        BrandsEntity brand = BrandsEntity.builder()
                .id(BRAND_ID)
                .chainName("ZARA")
                .build();

        ProductsEntity product = ProductsEntity.builder()
                .id(PRODUCT_ID)
                .productName("Test Product")
                .build();

        return PricesEntity.builder()
                .priceList(1)
                .brand(brand)
                .productsEntity(product)
                .startDate(LocalDateTime.of(2020, 6, 14, 0, 0, 0))
                .endDate(LocalDateTime.of(2020, 12, 31, 23, 59, 59))
                .priority(0)
                .price(new BigDecimal("35.50"))
                .currency("EUR")
                .build();
    }
}

