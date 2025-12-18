package com.example.demo.domain.service;

import com.example.demo.adapter.inbound.rest.dto.PriceRequestDTO;
import com.example.demo.application.usecase.PriorityPriceUseCase;
import com.example.demo.domain.exception.ResourceNotFoundException;
import com.example.demo.domain.model.Price;
import com.example.demo.domain.repository.PriceRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PriorityPriceService implements PriorityPriceUseCase {

  PriceRepository repository;

  @Override
  public Price getPriorityPrice(PriceRequestDTO request) {
    List<Price> prices = repository.findPricesByBrandProductDate(
        request.brandId(),
        request.productId(),
        request.applicationDate()
    );

    return selectHighestPriorityPrice(prices, request);
  }

  private Price selectHighestPriorityPrice(List<Price> prices, PriceRequestDTO request) {
    if (prices.isEmpty()) {
      throw new ResourceNotFoundException(
          String.format("Price not found for brandId=%d, productId=%d, applicationDate=%s",
              request.brandId(), request.productId(), request.applicationDate()));
    }

    return prices.stream()
        .max(Comparator.comparingInt(price -> price.rate().priority()))
        .orElseThrow(() -> new ResourceNotFoundException(
            String.format("Price not found for brandId=%d, productId=%d, applicationDate=%s",
                request.brandId(), request.productId(), request.applicationDate())));
  }
}
