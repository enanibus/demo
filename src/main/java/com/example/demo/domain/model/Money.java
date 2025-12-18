package com.example.demo.domain.model;

import java.math.BigDecimal;
import java.util.Objects;

public record Money(
    BigDecimal amount,
    Currency currency
) {
  public Money {
    Objects.requireNonNull(amount, "amount cannot be null");
    Objects.requireNonNull(currency, "currency cannot be null");
    if (amount.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException("amount must be >= 0");
    }
  }

  @Override
  public String toString() {
    return amount + " " + currency.name();
  }
}
