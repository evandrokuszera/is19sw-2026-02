package utfpr.api_pedido.controllers;

import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import utfpr.api_pedido.dto.AddItemPedidoDTO;
import utfpr.api_pedido.dto.FreteRequestDTO;
import utfpr.api_pedido.dto.FreteResponseDTO;
import utfpr.api_pedido.models.ItemPedido;
import utfpr.api_pedido.models.Pedido;
import utfpr.api_pedido.models.Produto;
import utfpr.api_pedido.repositories.ItemPedidoRepository;
import utfpr.api_pedido.repositories.PedidoRepository;
import utfpr.api_pedido.repositories.ProdutoRepository;
import utfpr.api_pedido.service.PagamentoService;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private PedidoRepository pedidoRepository;
    private ProdutoRepository produtoRepository;
    private ItemPedidoRepository itemPedidoRepository;
    private PagamentoService pagamentoService;

    // RestClient para permitir comunicação entre os microserviços
    private RestClient restClient = RestClient
            .create("http://localhost:8081/fretes");

    public PedidoController(PedidoRepository pedidoRepository,
                            ProdutoRepository produtoRepository,
                            ItemPedidoRepository itemPedidoRepository, PagamentoService pagamentoService) {
        this.pedidoRepository = pedidoRepository;
        this.produtoRepository = produtoRepository;
        this.itemPedidoRepository = itemPedidoRepository;
        this.pagamentoService = pagamentoService;
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



    // Novo endpoint para consultar api-fretes
    @PostMapping("/{id}/frete/{cep}")
    public ResponseEntity<Pedido> frete(@PathVariable Long id, @PathVariable String cep){
        // Consulta pedido no banco de dados
        Pedido pedido = this.pedidoRepository.findById(id).orElse(null);

        if (pedido == null) {
            return ResponseEntity.noContent().build();
        }

        // Cria requisição para api-fretes
        FreteRequestDTO freteRequestDTO = new FreteRequestDTO(cep, pedido.getAmount());

        // Envia requisição para api-fretes
        FreteResponseDTO freteResponseDTO = restClient
                .post()
                .body(freteRequestDTO)
                .retrieve()
                .body(FreteResponseDTO.class);

        // Atualiza informações do pedido (fretes, status, etc)
        pedido.setZipCode(cep);
        pedido.setShippingValue(freteResponseDTO.shippingPrice());
        pedido.setDeliveryDays(freteResponseDTO.deliveryDays());
        pedido.setStatus("WAITING_PAYMENT");
        this.pedidoRepository.save(pedido);

        // Retorna informações do pedido atualizado.
        return ResponseEntity.ok(pedido);
    }




    // -------------------------------------------------------------------
    // Integração com serviço API-PAGAMENTO
    // -------------------------------------------------------------------

    @GetMapping("/{id}/pagamento")
    public ResponseEntity<Pedido> pagamento(@PathVariable Long id){

        Pedido pedido = this.pedidoRepository.findById(id).orElse(null);
        if (pedido == null) return ResponseEntity.notFound().build();

        this.pagamentoService.sendPagamentoRequest(pedido);
        return ResponseEntity.ok(pedido);
    }




}
