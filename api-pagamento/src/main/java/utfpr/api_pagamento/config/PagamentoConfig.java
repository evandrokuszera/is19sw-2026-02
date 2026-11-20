package utfpr.api_pagamento.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PagamentoConfig {

    public static final String PAGAMENTO_EXCHANGE = "pagamento.exchange";
    public static final String PAGAMENTO_REQUEST_QUEUE = "pagamento.request.queue";
    public static final String PAGAMENTO_RESPONSE_QUEUE = "pagamento.response.queue";

    @Bean
    public DirectExchange pagamentoExchange(){
        return new DirectExchange(PAGAMENTO_EXCHANGE);
    }

    @Bean
    public Queue pagamentoRequestQueue(){
        return new Queue(PAGAMENTO_REQUEST_QUEUE);
    }

    @Bean
    public Queue pagamentoResponseQueue(){
        return new Queue(PAGAMENTO_RESPONSE_QUEUE);
    }

    @Bean
    public Binding requestBinding(){
        return BindingBuilder.bind(pagamentoRequestQueue()).to(pagamentoExchange()).with("request");
    }

    @Bean
    public Binding responseBinding(){
        return BindingBuilder.bind(pagamentoResponseQueue()).to(pagamentoExchange()).with("response");
    }

}
