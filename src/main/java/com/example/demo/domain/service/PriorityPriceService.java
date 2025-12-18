package com.example.demo.domain.service;

import com.example.demo.adapter.inbound.rest.dto.PriceRequestDTO;
import com.example.demo.application.usecase.PriorityPriceUseCase;
import com.example.demo.domain.model.Price;
import com.example.demo.domain.repository.PriceRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PriorityPriceService implements PriorityPriceUseCase {

  PriceRepository repository;

  @Override
  public Price getPriorityPrice(PriceRequestDTO request) {
    return repository.findPriceByBrandProductDate(request.brandId(), request.productId(), request.applicationDate());
  }
}
