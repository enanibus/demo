package com.example.demo.adapter.outbound.persistence;

import com.example.demo.adapter.inbound.rest.dto.PriceResponseDTO;
import com.example.demo.domain.model.Currency;
import com.example.demo.domain.model.Money;
import com.example.demo.domain.model.Price;
import com.example.demo.domain.model.Rate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class PriceMapperTest {

    @Test
    @DisplayName("toDomain - Should map PricesEntity to Price domain model")
    void toDomain_ShouldMapPricesEntityToPriceDomainModel() {
        // Given
        BrandsEntity brand = BrandsEntity.builder()
                .id(1)
                .chainName("ZARA")
                .build();

        ProductsEntity product = ProductsEntity.builder()
                .id(35455)
                .productName("Test Product")
                .build();

        PricesEntity entity = PricesEntity.builder()
                .priceList(1)
                .brand(brand)
                .productsEntity(product)
                .startDate(LocalDateTime.of(2020, 6, 14, 0, 0, 0))
                .endDate(LocalDateTime.of(2020, 12, 31, 23, 59, 59))
                .priority(0)
                .price(new BigDecimal("35.50"))
                .currency("EUR")
                .build();

        // When
        Price price = PriceMapper.toDomain(entity);

        // Then
        assertThat(price).isNotNull();
        assertThat(price.brandId()).isEqualTo(1);
        assertThat(price.productId()).isEqualTo(35455);
        assertThat(price.rate().priceList()).isEqualTo(1);
        assertThat(price.rate().startDate()).isEqualTo(LocalDateTime.of(2020, 6, 14, 0, 0, 0));
        assertThat(price.rate().endDate()).isEqualTo(LocalDateTime.of(2020, 12, 31, 23, 59, 59));
        assertThat(price.rate().priority()).isEqualTo(0);
        assertThat(price.rate().price().amount()).isEqualByComparingTo(new BigDecimal("35.50"));
        assertThat(price.rate().price().currency()).isEqualTo(Currency.EUR);
    }

    @Test
    @DisplayName("toDomain - Should map EUR currency correctly")
    void toDomain_ShouldMapEurCurrencyCorrectly() {
        // Given
        BrandsEntity brand = BrandsEntity.builder().id(1).chainName("ZARA").build();
        ProductsEntity product = ProductsEntity.builder().id(35455).productName("Product").build();

        PricesEntity entityEUR = createPricesEntity(brand, product);

        // When & Then
        assertThat(PriceMapper.toDomain(entityEUR).rate().price().currency()).isEqualTo(Currency.EUR);
    }

    @Test
    @DisplayName("toResponseDTO - Should map Price to PriceResponseDTO")
    void toResponseDTO_ShouldMapPriceToPriceResponseDTO() {
        // Given
        Money money = new Money(new BigDecimal("35.50"), Currency.EUR);
        Rate rate = new Rate(
                1,
                LocalDateTime.of(2020, 6, 14, 0, 0, 0),
                LocalDateTime.of(2020, 12, 31, 23, 59, 59),
                0,
                money
        );
        Price price = new Price(1, 35455, rate);

        // When
        PriceResponseDTO dto = PriceMapper.toResponseDTO(price);

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.productId()).isEqualTo(35455);
        assertThat(dto.brandId()).isEqualTo(1);
        assertThat(dto.priceList()).isEqualTo(1);
        assertThat(dto.startDate()).isEqualTo(LocalDateTime.of(2020, 6, 14, 0, 0, 0));
        assertThat(dto.endDate()).isEqualTo(LocalDateTime.of(2020, 12, 31, 23, 59, 59));
        assertThat(dto.finalPrice()).isEqualTo("35.50 EUR");
    }

    @Test
    @DisplayName("toResponseDTO - Should format finalPrice correctly")
    void toResponseDTO_ShouldFormatFinalPriceCorrectly() {
        // Given
        Money money = new Money(new BigDecimal("25.45"), Currency.EUR);
        Rate rate = new Rate(
                2,
                LocalDateTime.of(2020, 6, 14, 15, 0, 0),
                LocalDateTime.of(2020, 6, 14, 18, 30, 0),
                1,
                money
        );
        Price price = new Price(1, 35455, rate);

        // When
        PriceResponseDTO dto = PriceMapper.toResponseDTO(price);

        // Then
        assertThat(dto.finalPrice()).isEqualTo("25.45 EUR");
    }

    private PricesEntity createPricesEntity(BrandsEntity brand, ProductsEntity product) {
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

