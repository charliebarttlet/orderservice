# 🍕 Arquitectura de Microservicios: Patrón Saga con RabbitMQ y AWS

Este proyecto demuestra la comunicación asíncrona entre microservicios utilizando **RabbitMQ**, **Amazon MQ (AWS)** y el patron **Saga**.

# 🚧  Escenario planteado

Un microservicio para ordenar pizzas.

# 🤖 ¿Qué construí en este proyecto?

Implementé una arquitectura de microservicios para resolver un problema de **transacciones distribuidas**.
Desarrollé dos microservicios independientes en Java Spring Boot **ordeservice** y **paymenservice** que se comunican de forma asíncrona a través de **RabbitMQ**, alojado en la nube mediante **Amazon MQ (AWS)**. El flujo garantiza que, si el servicio de pagos rechaza una transacción por falta de fondos, el sistema emite automáticamente un evento de compensación para cancelar el pedido, manteniendo la consistencia de los datos sin depender de una base de datos central.

#  🐇 ¿Qué es RabbitMQ y para qué sirve?
Es un **Message Broker** (Bróker de mensajería) tradicional basado en colas. En este proyecto lo utilicé para enrutar tareas específicas de forma fiable.

## Propósito

* Garantizar que los comandos y eventos lleguen de un punto A a un punto B de forma segura.

## Filosofía

*  Funciona bajo el modelo de "Bróker inteligente, consumidor tonto". **RabbitMQ** se encarga de aplicar reglas complejas para decidir a qué cola va cada mensaje (usando Exchanges y Routing Keys).

## Ciclo de vida

* Una vez que el microservicio lee y confirma el mensaje, este se borra de la cola para siempre.

## Casos de uso ideales

* Tareas en segundo plano
* Envío de correos
* Notificaciones y procesos transaccionales (como el cobro de nuestra pizzería).