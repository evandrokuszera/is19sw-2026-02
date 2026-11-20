package utfpr.api_pedido.dto;

public record RequestPagamentoDTO(Long pedidoId, String creditCardNumber, Double amount) {}
