package com.company.fooddash.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "orders")
public record Order(@Id String id, String customerName, String phone, String address, List<OrderItem> items,
                    double total, OrderStatus status, LocalDateTime createdAt, String userEmail) {
    public Order withStatus(OrderStatus s) {
        return new Order(id, customerName, phone, address, items, total, s, createdAt, userEmail);
    }
}
