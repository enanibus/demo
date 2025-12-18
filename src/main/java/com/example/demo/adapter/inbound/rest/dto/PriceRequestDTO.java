package com.example.demo.adapter.inbound.rest.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PriceRequestDTO(
    @NotNull(message = "applicationDate is required")
    LocalDateTime applicationDate,
    @NotNull(message = "productId is required")
    Integer productId,
    @NotNull(message = "brandId is required")
    Integer brandId
) {
}
