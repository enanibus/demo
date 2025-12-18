package com.example.demo.adapter.outbound.persistence;

import com.example.demo.domain.exception.ResourceNotFoundException;
import com.example.demo.domain.model.Price;
import com.example.demo.domain.repository.PriceRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PRIVATE;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class PricePersistenceAdapter implements PriceRepository {

  JpaPriceRepository jpaRepo;

  @Override
  public Price findPriceByBrandProductDate(Integer brandId, Integer productId, LocalDateTime applicationDate) {
    return jpaRepo.findPricesByBrandAndProductAndApplicationDate(brandId, productId, applicationDate)
            .map(PriceMapper::toDomain)
            .orElseThrow(() -> new ResourceNotFoundException(
                String.format("Price not found for brandId=%d, productId=%d, applicationDate=%s", brandId, productId, applicationDate)));
  }
}
