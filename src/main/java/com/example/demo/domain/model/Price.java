package com.example.demo.domain.model;

import java.util.Objects;

public record Price(
    Integer brandId,
    Integer productId,
    Rate rate
) {
  public Price {
    Objects.requireNonNull(brandId);
    Objects.requireNonNull(productId);
    Objects.requireNonNull(rate);
  }

}
