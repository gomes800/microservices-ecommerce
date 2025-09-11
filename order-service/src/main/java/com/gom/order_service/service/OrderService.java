package com.gom.order_service.service;

import com.gom.order_service.dto.OrderCreatedEvent;
import com.gom.order_service.kafka.OrderProducer;
import com.gom.order_service.model.Order;
import com.gom.order_service.repositories.OrderRepository;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderProducer orderProducer;

    public OrderService(OrderRepository orderRepository, OrderProducer orderProducer) {
        this.orderRepository = orderRepository;
        this.orderProducer = orderProducer;
    }

    public Order createOrder(Order order) {
        Order savedOrder = orderRepository.save(order);

        OrderCreatedEvent event = new OrderCreatedEvent(
                savedOrder.getId(),
                savedOrder.getProductId(),
                savedOrder.getQuantity(),
                savedOrder.getStatus()
        );
        orderProducer.sendOrderCreatedEvent(event);

        return savedOrder;
    }
}
