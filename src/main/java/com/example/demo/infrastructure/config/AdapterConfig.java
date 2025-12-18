package com.example.demo.infrastructure.config;

import com.example.demo.adapter.outbound.persistence.JpaPriceRepository;
import com.example.demo.adapter.outbound.persistence.PricePersistenceAdapter;
import com.example.demo.domain.repository.PriceRepository;
import com.example.demo.domain.service.PriorityPriceService;
import com.example.demo.application.usecase.PriorityPriceUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdapterConfig {

    @Bean
    public PriceRepository priceRepository(JpaPriceRepository jpaPriceRepository) {
        return new PricePersistenceAdapter(jpaPriceRepository);
    }

    @Bean
    public PriorityPriceUseCase priorityPriceUseCase(PriceRepository priceRepository) {
        return new PriorityPriceService(priceRepository);
    }
}

