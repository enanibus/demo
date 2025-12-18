package com.example.demo.domain.repository;

import com.example.demo.domain.model.Price;

import java.time.LocalDateTime;
import java.util.List;

public interface PriceRepository {
  List<Price> findPricesByBrandProductDate(Integer brandId, Integer productId, LocalDateTime applicationDate);
}
