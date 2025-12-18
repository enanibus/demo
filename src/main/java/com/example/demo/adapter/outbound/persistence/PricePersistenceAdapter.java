package com.example.demo.adapter.outbound.persistence;

import com.example.demo.domain.model.Price;
import com.example.demo.domain.repository.PriceRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class PricePersistenceAdapter implements PriceRepository {

  JpaPriceRepository jpaRepo;

  @Override
  public List<Price> findPricesByBrandProductDate(Integer brandId, Integer productId, LocalDateTime applicationDate) {
    return jpaRepo.findPricesByBrandAndProductAndApplicationDate(brandId, productId, applicationDate)
            .stream()
            .map(PriceMapper::toDomain)
            .toList();
  }
}
