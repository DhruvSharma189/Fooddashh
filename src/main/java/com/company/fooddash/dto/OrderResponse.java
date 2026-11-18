package com.company.fooddash.dto;

import com.company.fooddash.model.OrderItem;
import com.company.fooddash.model.OrderStatus;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(String id, String customerName, String phone, String address, List<OrderItem> items,
        double total, OrderStatus status, LocalDateTime createdAt) {
}
