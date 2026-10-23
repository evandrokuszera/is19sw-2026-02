package utfpr.api_pedido.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import utfpr.api_pedido.models.ItemPedido;

public interface ItemPedidoRepository extends JpaRepository<ItemPedido, Long> { }
