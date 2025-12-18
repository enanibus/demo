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

  public boolean isDateIncluded(LocalDateTime date) {
    return isAfterOrEqualStartDate(date)
        && isBeforeOrEqualEndDate(date);
  }

  private boolean isAfterOrEqualStartDate(LocalDateTime date) {
    return startDate.isBefore(date)
        || startDate.isEqual(date);
  }

  private boolean isBeforeOrEqualEndDate(LocalDateTime date) {
    return endDate.isAfter(date)
        || endDate.isEqual(date);
  }

}
