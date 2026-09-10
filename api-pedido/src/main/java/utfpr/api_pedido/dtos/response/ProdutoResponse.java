package utfpr.api_pedido.dtos.response;

import java.util.List;
import java.util.ArrayList;

import utfpr.api_pedido.dtos.request.ProdutoRequest;
import utfpr.api_pedido.models.Produto;

public record ProdutoResponse(
		Long id,
		String description,
		Integer quantity,
		Double price,
		String category) {

	public static ProdutoResponse toResponse(Produto produto) {
		return new ProdutoResponse(
				produto.getId(),
				produto.getDescription(),
				produto.getQuantity(),
				produto.getPrice(),
				produto.getCategory());
	}

	public static List<ProdutoResponse> toResponse(List<Produto> produtos) {
		List<ProdutoResponse> response = new ArrayList<>();
		for (Produto produto : produtos) {
			response.add(toResponse(produto));
		}
		return response;
	}

	public static Produto toEntity(ProdutoResponse produto) {
		return new Produto(
				produto.id(),
				produto.description(),
				produto.quantity(),
				produto.price(),
				produto.category());
	}

	public static Produto toEntity(ProdutoRequest produto) {
		return new Produto(
				produto.id(),
				produto.description(),
				produto.quantity(),
				produto.price(),
				produto.category());
	}
}
