package com.company.fooddash.repository;

import com.company.fooddash.model.Order;
import com.company.fooddash.model.OrderStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface OrderRepository extends MongoRepository<Order, String> {
    List<Order> findAllByOrderByCreatedAtDesc();

    List<Order> findByStatusOrderByCreatedAtDesc(OrderStatus status);

    List<Order> findByUserEmailIgnoreCaseOrderByCreatedAtDesc(String userEmail);
}
