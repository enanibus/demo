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
class BrandsEntityTest {

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Should persist BrandsEntity")
    void shouldPersistBrandsEntity() {
        // Given
        BrandsEntity brand = BrandsEntity.builder()
                .chainName("ZARA")
                .build();

        // When
        BrandsEntity savedBrand = entityManager.persistAndFlush(brand);

        // Then
        assertThat(savedBrand.getId()).isNotNull();
        assertThat(savedBrand.getChainName()).isEqualTo("ZARA");
    }

    @Test
    @DisplayName("Should retrieve BrandsEntity by id")
    void shouldRetrieveBrandsEntityById() {
        // Given
        BrandsEntity brand = BrandsEntity.builder()
                .chainName("PULL&BEAR")
                .build();
        entityManager.persistAndFlush(brand);
        entityManager.clear();

        // When
        BrandsEntity foundBrand = entityManager.find(BrandsEntity.class, brand.getId());

        // Then
        assertThat(foundBrand).isNotNull();
        assertThat(foundBrand.getChainName()).isEqualTo("PULL&BEAR");
    }

    @Test
    @DisplayName("Should update BrandsEntity chainName")
    void shouldUpdateBrandsEntityChainName() {
        // Given
        BrandsEntity brand = BrandsEntity.builder()
                .chainName("OLD_NAME")
                .build();
        entityManager.persistAndFlush(brand);

        // When
        brand.setChainName("NEW_NAME");
        entityManager.persistAndFlush(brand);
        entityManager.clear();

        // Then
        BrandsEntity updatedBrand = entityManager.find(BrandsEntity.class, brand.getId());
        assertThat(updatedBrand.getChainName()).isEqualTo("NEW_NAME");
    }
}

