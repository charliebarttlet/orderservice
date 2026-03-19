package com.barttletlabs.orderservice.listener;

import com.barttletlabs.orderservice.config.RabbitMQConfig;
import com.barttletlabs.orderservice.dto.PagoEvent;
import com.barttletlabs.orderservice.entity.Pedido;
import com.barttletlabs.orderservice.repository.PedidoRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
public class PedidoListener {

    private final PedidoRepository pedidoRepository;
    private PagoEvent pago;

    public PedidoListener(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    // Listener Aprobados
    @RabbitListener(queues = RabbitMQConfig.QUEUE_APROBADOS)
    public void handlePagoAprobado(PagoEvent pago) {
        this.pago = pago;
        Optional<Pedido> pedidoOpt = pedidoRepository.findById(pago.getPedidoId());

        if (pedidoOpt.isPresent()) {
            Pedido pedido = pedidoOpt.get();
            pedido.setEstado("EN_PREPARACION");
            pedidoRepository.save(pedido);
            System.out.println("✅ [Saga Completada] Pedido ID " + pedido.getId() + " pagado. Estado: EN_PREPARACION");
        }
    }

    /**
     * Escucha el evento de pago rechazado.
     * Ejecuta la TRANSACCIÓN DE COMPENSACIÓN de la Saga: revierte el estado
     * del pedido a CANCELADO ya que el microservicio de pagos reportó un fallo.
     */
    @RabbitListener(queues = RabbitMQConfig.QUEUE_RECHAZADOS)
    public void handlePagoRechazado(PagoEvent pago) {
        Optional<Pedido> pedidoOpt = pedidoRepository.findById(pago.getPedidoId());

        if (pedidoOpt.isPresent()) {
            Pedido pedido = pedidoOpt.get();
            pedido.setEstado("CANCELADO");
            pedidoRepository.save(pedido);
            System.out.println("❌ [Compensación Saga] Pedido ID " + pedido.getId() + " sin fondos. Estado: CANCELADO");
        }
    }
}
