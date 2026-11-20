package utfpr.api_pedido.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import utfpr.api_pedido.dto.RequestPagamentoDTO;
import utfpr.api_pedido.dto.ResponsePagamentoDTO;
import utfpr.api_pedido.models.Pedido;
import utfpr.api_pedido.repositories.PedidoRepository;

@Service
public class PagamentoService {

    private PedidoRepository pedidoRepository;
    private RabbitTemplate rabbitTemplate;

    public PagamentoService(PedidoRepository pedidoRepository, RabbitTemplate rabbitTemplate) {
        this.pedidoRepository = pedidoRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    // ENVIANDO REQUISIÇÃO DE PAGAMENTO PARA API-PAGAMENTO VIA RABBITMQ
    public void sendPagamentoRequest(Pedido pedido){
        // Requisição de pagamento
        RequestPagamentoDTO requestPagamentoDTO = new RequestPagamentoDTO(
                pedido.getId(),
                "123456789",
                pedido.getAmount()
        );

        // Enviando requisição de pagagamento para Exchange
        this.rabbitTemplate.convertAndSend("pagamento.exchange", "request", requestPagamentoDTO);
        System.out.println("Solicitação de pagamento enviada para operadora.");
    }



    // RECEBENDO/ESCUTANDO RETORNO DA API-PAGAMENTO VIA RABBITMQ
    @RabbitListener(queues = "pagamento.response.queue")
    public void receivePagamentoResponse(ResponsePagamentoDTO responsePagamentoDTO){
        // Procurar pedido na base de dados
        Long idPedido = responsePagamentoDTO.pedidoId();
        Pedido pedido = this.pedidoRepository.findById(idPedido).orElse(null);

        // Se pedido encontrado, atualizar seu STATUS conforme retorno da API-PAGAMENTOS
        if (pedido != null){

            if (responsePagamentoDTO.status().contains("FAIL")){
                pedido.setStatus("CANCELLED");
                System.out.println(idPedido + ": PEDIDO CANCELADO");
            } else if (responsePagamentoDTO.status().contains("SUCCESS")){
                pedido.setStatus("PAID");
                System.out.println(idPedido + ": PEDIDO PAGO");
            }

            this.pedidoRepository.save(pedido);

        }

    }

}
