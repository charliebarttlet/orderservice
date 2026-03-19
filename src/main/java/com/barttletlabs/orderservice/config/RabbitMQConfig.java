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

    // Declaración de queues names para RabbitMQ
    public static final String QUEUE_PAGOS = "pagos.queue";
    public static final String QUEUE_APROBADOS = "pedidos.aprobados.queue";
    public static final String QUEUE_RECHAZADOS = "pedidos.rechazados.queue";
    // Declaración de nuevas keys
    public static final String ROUTING_KEY_APROBADO = "pago.aprobado";
    public static final String ROUTING_KEY_RECHAZADO = "pago.rechazado";

    @Bean
    public TopicExchange pizzeriaExchange() {
        return new TopicExchange(PedidoService.EXCHANGE_NAME);
    }

    // 2. Instancias de Queues
    @Bean
    public Queue pagosQueue() {
        return new Queue(QUEUE_PAGOS, true); // true para persistir si se reinicia AWS
    }

    @Bean
    public Queue aprobadosQueue() {
        return new Queue(QUEUE_APROBADOS, true);
    }

    @Bean
    public Queue rechazadosQueue() {
        return new Queue(QUEUE_RECHAZADOS, true);
    }

    @Bean
    public Binding bindingPagos(Queue pagosQueue, TopicExchange pizzeriaExchange) {
        return BindingBuilder.bind(pagosQueue).to(pizzeriaExchange).with(PedidoService.ROUTING_KEY_CREADO);
    }

    // Binding Queues con Keys
    @Bean
    public Binding bindingAprobados(Queue aprobadosQueue, TopicExchange pizzeriaExchange) {
        return BindingBuilder.bind(aprobadosQueue).to(pizzeriaExchange).with(ROUTING_KEY_APROBADO);
    }

    @Bean
    public Binding bindingRechazados(Queue rechazadosQueue, TopicExchange pizzeriaExchange) {
        return BindingBuilder.bind(rechazadosQueue).to(pizzeriaExchange).with(ROUTING_KEY_RECHAZADO);
    }

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

