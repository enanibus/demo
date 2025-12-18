package com.example.demo.adapter.outbound.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "PRICES")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PricesEntity {

  @ManyToOne
  @JoinColumn(name = "brand_id")
  @NotNull
  private BrandsEntity brand;

  @NotNull
  private LocalDateTime startDate;

  @NotNull
  private LocalDateTime endDate;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer priceList;

  @ManyToOne
  @JoinColumn(name = "product_id")
  @NotNull
  private ProductsEntity productsEntity;
  @NotNull
  private Integer priority;
  @NotNull
  @Positive
  private BigDecimal price;
  @NotNull
  @Size(min = 3, max = 3)
  private String currency;
}
