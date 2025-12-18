package com.example.demo.adapter.inbound.rest.dto;

public record ErrorResponse(
    int status,
    String message,
    long timestamp
) {
}
