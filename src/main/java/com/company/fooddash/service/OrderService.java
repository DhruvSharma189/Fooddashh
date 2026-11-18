package com.company.fooddash.service;

import com.company.fooddash.dto.OrderRequest;
import com.company.fooddash.dto.OrderResponse;
import com.company.fooddash.model.OrderStatus;
import java.util.List;

public interface OrderService {
    OrderResponse place(OrderRequest request, String userEmail);

    List<OrderResponse> listForUser(String userEmail);

    /** Customers can read only their own orders; admins can read any. */
    OrderResponse getFor(String id, String userEmail, boolean admin);

    List<OrderResponse> list(OrderStatus status);

    OrderResponse get(String id);

    OrderResponse updateStatus(String id, OrderStatus status);
}
