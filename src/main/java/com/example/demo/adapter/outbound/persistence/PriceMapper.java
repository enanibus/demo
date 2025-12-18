package com.example.demo.adapter.outbound.persistence;

import com.example.demo.adapter.inbound.rest.dto.PriceResponseDTO;
import com.example.demo.domain.model.Currency;
import com.example.demo.domain.model.Money;
import com.example.demo.domain.model.Price;
import com.example.demo.domain.model.Rate;

public class PriceMapper {

    private PriceMapper() {
    }

    public static Price toDomain(PricesEntity entity) {
        Money money = new Money(
                entity.getPrice(),
                Currency.valueOf(entity.getCurrency())
        );

        Rate rate = new Rate(
                entity.getPriceList(),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getPriority(),
                money
        );

        return new Price(
                entity.getBrand().getId(),
                entity.getProductsEntity().getId(),
                rate
        );
    }

    public static PriceResponseDTO toResponseDTO(Price price) {
        return new PriceResponseDTO(
                price.productId(),
                price.brandId(),
                price.rate().priceList(),
                price.rate().startDate(),
                price.rate().endDate(),
                price.rate().price().toString()
        );
    }
}
