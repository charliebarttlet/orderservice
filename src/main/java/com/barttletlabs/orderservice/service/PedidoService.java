package com.barttletlabs.orderservice.service;


import com.barttletlabs.orderservice.entity.Pedido;
import com.barttletlabs.orderservice.repository.PedidoRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final RabbitTemplate rabbitTemplate;

    public static final String EXCHANGE_NAME = "pizzeria.exchange";
    public static final String ROUTING_KEY_CREADO = "pedido.creado";

    public PedidoService(PedidoRepository pedidoRepository, RabbitTemplate rabbitTemplate) {
        this.pedidoRepository = pedidoRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Servicio responsable de iniciar la Saga de compras.
     * Aplica el patrón Coreografía: guarda el estado localmente y emite un evento
     * asíncrono a RabbitMQ para que otros microservicios actúen en consecuencia.
     */
    public Pedido crearPedido(Pedido pedido) {
        pedido.setEstado("PENDIENTE");
        Pedido pedidoGuardado = pedidoRepository.save(pedido);

        rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY_CREADO, pedidoGuardado);

        System.out.println("🍕 [PedidoService] Pedido guardado y evento 'PedidoCreado' publicado. ID: " + pedidoGuardado.getId());

        return pedidoGuardado;
    }
}
