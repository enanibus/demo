package com.example.demo.domain.repository;

import com.example.demo.domain.model.Price;

import java.time.LocalDateTime;

public interface PriceRepository {
  Price findPriceByBrandProductDate(Integer brandId, Integer productId, LocalDateTime applicationDate);
}
