package com.barttletlabs.orderservice.config;

import com.barttletlabs.orderservice.service.PedidoService;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Declaración de queue
    public static final String QUEUE_PAGOS = "pagos.queue";

    // 1. Declaración de Exchange
    @Bean
    public TopicExchange pizzeriaExchange() {
        return new TopicExchange(PedidoService.EXCHANGE_NAME);
    }

    // 2. Declaración de Queue
    @Bean
    public Queue pagosQueue() {
        return new Queue(QUEUE_PAGOS, true); // true para persistir si se reinicia AWS
    }

    // 3. Bind de Queue con Exchange por medio de Routing Key
    @Bean
    public Binding bindingPagos(Queue pagosQueue, TopicExchange pizzeriaExchange) {
        return BindingBuilder.bind(pagosQueue).to(pizzeriaExchange).with(PedidoService.ROUTING_KEY_CREADO);
    }

    // 4. Configurar JSON converter
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // 5. Inyectar RabbitTemplate para envíar mensajes
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}

