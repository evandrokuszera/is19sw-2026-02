package utfpr.api_frete.dtos;

public record ViaCepResponseDTO(String cep, String uf, String localidade, Boolean erro) {}