package utfpr.api_pedido.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import utfpr.api_pedido.models.Pedido;

public interface PedidoRepository extends JpaRepository<Pedido, Long> { }
