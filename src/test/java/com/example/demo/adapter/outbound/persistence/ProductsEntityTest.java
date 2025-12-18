package com.example.demo.adapter.outbound.persistence;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = {
    "spring.sql.init.mode=never",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class ProductsEntityTest {

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Should persist ProductsEntity")
    void shouldPersistProductsEntity() {
        // Given
        ProductsEntity product = ProductsEntity.builder()
                .productName("Summer T-Shirt")
                .build();

        // When
        ProductsEntity savedProduct = entityManager.persistAndFlush(product);

        // Then
        assertThat(savedProduct.getId()).isNotNull();
        assertThat(savedProduct.getProductName()).isEqualTo("Summer T-Shirt");
    }

    @Test
    @DisplayName("Should retrieve ProductsEntity by id")
    void shouldRetrieveProductsEntityById() {
        // Given
        ProductsEntity product = ProductsEntity.builder()
                .productName("Winter Jacket")
                .build();
        entityManager.persistAndFlush(product);
        entityManager.clear();

        // When
        ProductsEntity foundProduct = entityManager.find(ProductsEntity.class, product.getId());

        // Then
        assertThat(foundProduct).isNotNull();
        assertThat(foundProduct.getProductName()).isEqualTo("Winter Jacket");
    }

    @Test
    @DisplayName("Should update ProductsEntity productName")
    void shouldUpdateProductsEntityProductName() {
        // Given
        ProductsEntity product = ProductsEntity.builder()
                .productName("Old Product Name")
                .build();
        entityManager.persistAndFlush(product);

        // When
        product.setProductName("New Product Name");
        entityManager.persistAndFlush(product);
        entityManager.clear();

        // Then
        ProductsEntity updatedProduct = entityManager.find(ProductsEntity.class, product.getId());
        assertThat(updatedProduct.getProductName()).isEqualTo("New Product Name");
    }
}

