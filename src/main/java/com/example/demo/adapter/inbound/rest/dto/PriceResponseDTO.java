package com.example.demo.adapter.inbound.rest.dto;

import java.time.LocalDateTime;

public record PriceResponseDTO(
    Integer productId,
    Integer brandId,
    Integer priceList,
    LocalDateTime startDate,
    LocalDateTime endDate,
    String finalPrice
) {
}
