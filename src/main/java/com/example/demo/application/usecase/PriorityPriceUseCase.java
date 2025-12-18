package com.example.demo.application.usecase;

import com.example.demo.adapter.inbound.rest.dto.PriceRequestDTO;
import com.example.demo.domain.model.Price;

public interface PriorityPriceUseCase {
  Price getPriorityPrice(PriceRequestDTO request);
}
