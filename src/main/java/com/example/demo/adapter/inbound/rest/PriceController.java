package com.example.demo.adapter.inbound.rest;

import com.example.demo.adapter.inbound.rest.dto.PriceRequestDTO;
import com.example.demo.adapter.inbound.rest.dto.PriceResponseDTO;
import com.example.demo.adapter.outbound.persistence.PriceMapper;
import com.example.demo.application.usecase.PriorityPriceUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
@Tag(name = "Prices", description = "API for price queries")
public class PriceController {

  PriorityPriceUseCase priorityPriceUseCase;

  @Operation(summary = "Get priority price", description = "Gets the applicable price for a product and brand on a given date")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Price found"),
      @ApiResponse(responseCode = "404", description = "Price not found")
  })
  @GetMapping("/brand/{brandId}/product/{productId}/prices")
  public ResponseEntity<PriceResponseDTO> getPriorityPrice(
      @Parameter(description = "Brand ID") @PathVariable Integer brandId,
      @Parameter(description = "Product ID") @PathVariable Integer productId,
      @Parameter(description = "Application date (format: yyyy-MM-ddTHH:mm:ss)") @RequestParam LocalDateTime applicationDate) {

    var request = PriceRequestDTO.builder()
        .applicationDate(applicationDate)
        .productId(productId)
        .brandId(brandId)
        .build();

    var price = priorityPriceUseCase.getPriorityPrice(request);

    return ResponseEntity.ok(PriceMapper.toResponseDTO(price));
  }
}
