package utfpr.api_pagamento.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import utfpr.api_pagamento.config.PagamentoConfig;
import utfpr.api_pagamento.dtos.RequestPagamentoDTO;
import utfpr.api_pagamento.dtos.ResponsePagamentoDTO;

import java.util.random.RandomGenerator;

@Service
public class PagamentoService {

    private RabbitTemplate rabbitTemplate;

    public PagamentoService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    // RECEBE REQUISIÇÃO DE PAGAMENTO, PROCESSA, E RESPONSE COM SUCCESS OU FAIL

    @RabbitListener(queues = PagamentoConfig.PAGAMENTO_REQUEST_QUEUE)
    public void processarPagamento(RequestPagamentoDTO requestDTO) throws InterruptedException {

        String status = "FAIL: pagamento não autorizado";

        System.out.println("Solicitação Recebida (pedido:" + requestDTO.pedidoId() + ")");

        System.out.println("Processando... (pedido:" + requestDTO.pedidoId() + ")");
        Thread.sleep(10000);

        if ( RandomGenerator.getDefault().nextBoolean() ) {
            // Simula processamento do pagamento...
            // Poderia avaliar o valor, número do cartão de crédito, etc.
            status = "SUCCESS: pagamento autorizado";
        }

        System.out.println(status + " (pedido:" + requestDTO.pedidoId() + ")");

        ResponsePagamentoDTO responseDTO = new ResponsePagamentoDTO(
                requestDTO.pedidoId(),
                status
        );

        // Aqui... estou enviando a resposta do pagamento para a fila de respostas.
        this.rabbitTemplate.convertAndSend(
                PagamentoConfig.PAGAMENTO_EXCHANGE,
                "response",
                responseDTO
        );
    }
}
