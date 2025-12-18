package com.example.demo.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

public record Rate(
    Integer priceList,
    LocalDateTime startDate,
    LocalDateTime endDate,
    Integer priority,
    Money price
) {
  public Rate {
    Objects.requireNonNull(startDate);
    Objects.requireNonNull(endDate);
    if (startDate.isAfter(endDate)) {
      throw new IllegalArgumentException("Start date must not be after end date");
    }
  }

}
