package com.example.demo.adapter.outbound.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface JpaPriceRepository extends JpaRepository<PricesEntity, Integer> {

  @Query("""
      SELECT p FROM PricesEntity p
      WHERE p.brand.id = :brandId
      AND p.productsEntity.id = :productId
      AND :applicationDate BETWEEN p.startDate AND p.endDate
      """)
  List<PricesEntity> findPricesByBrandAndProductAndApplicationDate(
      @Param("brandId") Integer brandId,
      @Param("productId") Integer productId,
      @Param("applicationDate") LocalDateTime applicationDate
  );
}
