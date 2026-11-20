package utfpr.api_pagamento.dtos;

public record RequestPagamentoDTO(Long pedidoId, String creditCardNumber, Double amount) {}
