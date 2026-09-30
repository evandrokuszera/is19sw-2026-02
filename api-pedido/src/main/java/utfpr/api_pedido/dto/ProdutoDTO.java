package utfpr.api_pedido.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ProdutoDTO(
        Long id,

        @NotBlank(message = "Descrição obrigatória")
        @Size(min = 3, max = 50, message = "Descrição deve ter entre 3 e 50 caracteres")
        String description,

        @NotNull(message = "Quantidade obrigatória")
        @Positive(message = "Quantidade deve ser maior que zero")
        Integer quantity,

        @NotNull(message = "Preço obrigatório")
        @Positive (message = "Preço deve ser maior que zero")
        Double price,

        String category
) {}