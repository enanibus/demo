package com.example.demo.domain.model;

public enum Currency {

  EUR("€");

  public final String symbol;

  Currency(String symbol) {
    this.symbol = symbol;
  }
}
