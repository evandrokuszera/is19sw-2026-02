package utfpr.api_pedido.controllers;

import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utfpr.api_pedido.dto.AddItemPedidoDTO;
import utfpr.api_pedido.models.ItemPedido;
import utfpr.api_pedido.models.Pedido;
import utfpr.api_pedido.models.Produto;
import utfpr.api_pedido.repositories.ItemPedidoRepository;
import utfpr.api_pedido.repositories.PedidoRepository;
import utfpr.api_pedido.repositories.ProdutoRepository;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private PedidoRepository pedidoRepository;
    private ProdutoRepository produtoRepository;
    private ItemPedidoRepository itemPedidoRepository;

    public PedidoController(PedidoRepository pedidoRepository,
                            ProdutoRepository produtoRepository,
                            ItemPedidoRepository itemPedidoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.produtoRepository = produtoRepository;
        this.itemPedidoRepository = itemPedidoRepository;
    }

    @PostMapping
    public ResponseEntity<Pedido> createPedido(){
        Pedido pedido = new Pedido();
        pedido.setAmount(0.0);
        pedido.setStatus("CREATED");
        pedido.setDate(LocalDateTime.now());

        this.pedidoRepository.save(pedido);
        return ResponseEntity.ok(pedido);
    }

    @PostMapping("/{id}/items")
    @Transactional
    public ResponseEntity<ItemPedido> addItem(@PathVariable Long id, @RequestBody AddItemPedidoDTO addItemPedidoDTO){

        Pedido pedido = this.pedidoRepository.findById(id).orElse(null);
        if (pedido == null) return ResponseEntity.notFound().build();

        Produto produto = this.produtoRepository.findById( addItemPedidoDTO.produtoId() ).orElse(null);
        if (produto == null) return ResponseEntity.notFound().build();

        ItemPedido item = new ItemPedido();
        item.setPedido(pedido);
        item.setProduto(produto);
        item.setUnitPrice( produto.getPrice() );
        item.setQuantity( addItemPedidoDTO.quantity() );
        this.itemPedidoRepository.save(item);

        // atualizar estoque
        produto.setQuantity( produto.getQuantity() - item.getQuantity() );
        this.produtoRepository.save(produto);

        double total = pedido.getAmount() + (item.getQuantity() * item.getUnitPrice());
        pedido.setAmount(total);
        this.pedidoRepository.save(pedido);

        return ResponseEntity.ok(item);
    }

    @GetMapping("/{id}")
    ResponseEntity<Pedido> getPedido(@PathVariable Long id){

        Pedido pedido = this.pedidoRepository.findById(id).orElse(null);
        if (pedido == null) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(pedido);
    }

}
