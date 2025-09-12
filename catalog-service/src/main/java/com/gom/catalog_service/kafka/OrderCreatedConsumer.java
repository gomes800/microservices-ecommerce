package com.gom.catalog_service.kafka;

import com.gom.catalog_service.dto.OrderCreatedEvent;
import com.gom.catalog_service.repositories.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class OrderCreatedConsumer {

    private final ProductRepository productRepository;

    public OrderCreatedConsumer(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @KafkaListener(topics = "order-created", groupId = "catalog-service")
    @Transactional
    public void consume(OrderCreatedEvent event) {
        System.out.println("event: " + event);

        productRepository.findById(event.getProductId())
                .ifPresent(product -> {
                    int newStock = product.getStock() - event.getQuantity();
                    product.setStock(newStock >= 0 ? newStock : 0);
                    productRepository.save(product);

                    System.out.println("Stock updated for product " + product.getId() + ": " + product.getStock());
                });
    }
}
