package com.example.demo.adapter.outbound.persistence;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = {
    "spring.sql.init.mode=never",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class PricesEntityTest {

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Should persist PricesEntity with all relationships")
    void shouldPersistPricesEntityWithRelationships() {
        // Given
        BrandsEntity brand = BrandsEntity.builder()
                .chainName("ZARA")
                .build();
        entityManager.persist(brand);

        ProductsEntity product = ProductsEntity.builder()
                .productName("Test Product")
                .build();
        entityManager.persist(product);

        PricesEntity price = PricesEntity.builder()
                .brand(brand)
                .productsEntity(product)
                .startDate(LocalDateTime.of(2020, 6, 14, 0, 0, 0))
                .endDate(LocalDateTime.of(2020, 12, 31, 23, 59, 59))
                .priority(0)
                .price(new BigDecimal("35.50"))
                .currency("EUR")
                .build();

        // When
        PricesEntity savedPrice = entityManager.persistAndFlush(price);

        // Then
        assertThat(savedPrice.getPriceList()).isNotNull();
        assertThat(savedPrice.getBrand()).isEqualTo(brand);
        assertThat(savedPrice.getProductsEntity()).isEqualTo(product);
        assertThat(savedPrice.getPrice()).isEqualByComparingTo(new BigDecimal("35.50"));
        assertThat(savedPrice.getCurrency()).isEqualTo("EUR");
    }

    @Test
    @DisplayName("Should retrieve PricesEntity with brand and product")
    void shouldRetrievePricesEntityWithBrandAndProduct() {
        // Given
        BrandsEntity brand = BrandsEntity.builder()
                .chainName("ZARA")
                .build();
        entityManager.persist(brand);

        ProductsEntity product = ProductsEntity.builder()
                .productName("Test Product")
                .build();
        entityManager.persist(product);

        PricesEntity price = PricesEntity.builder()
                .brand(brand)
                .productsEntity(product)
                .startDate(LocalDateTime.of(2020, 6, 14, 0, 0, 0))
                .endDate(LocalDateTime.of(2020, 12, 31, 23, 59, 59))
                .priority(1)
                .price(new BigDecimal("25.45"))
                .currency("EUR")
                .build();
        entityManager.persistAndFlush(price);
        entityManager.clear();

        // When
        PricesEntity foundPrice = entityManager.find(PricesEntity.class, price.getPriceList());

        // Then
        assertThat(foundPrice).isNotNull();
        assertThat(foundPrice.getBrand().getChainName()).isEqualTo("ZARA");
        assertThat(foundPrice.getProductsEntity().getProductName()).isEqualTo("Test Product");
    }

    @Test
    @DisplayName("Should correctly set date range")
    void shouldCorrectlySetDateRange() {
        // Given
        BrandsEntity brand = entityManager.persist(BrandsEntity.builder().chainName("ZARA").build());
        ProductsEntity product = entityManager.persist(ProductsEntity.builder().productName("Product").build());

        LocalDateTime startDate = LocalDateTime.of(2020, 6, 14, 15, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2020, 6, 14, 18, 30, 0);

        PricesEntity price = PricesEntity.builder()
                .brand(brand)
                .productsEntity(product)
                .startDate(startDate)
                .endDate(endDate)
                .priority(1)
                .price(new BigDecimal("25.45"))
                .currency("EUR")
                .build();

        // When
        PricesEntity savedPrice = entityManager.persistAndFlush(price);

        // Then
        assertThat(savedPrice.getStartDate()).isEqualTo(startDate);
        assertThat(savedPrice.getEndDate()).isEqualTo(endDate);
    }
}

