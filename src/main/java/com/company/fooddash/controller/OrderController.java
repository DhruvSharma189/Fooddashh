package com.company.fooddash.controller;

import com.company.fooddash.dto.OrderRequest;
import com.company.fooddash.dto.OrderResponse;
import com.company.fooddash.dto.StatusRequest;
import com.company.fooddash.model.OrderStatus;
import com.company.fooddash.service.OrderService;
import com.company.fooddash.service.AuditService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService service;
    private final AuditService audit;

    public OrderController(OrderService service, AuditService audit) {
        this.service = service;
        this.audit = audit;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse place(@Valid @RequestBody OrderRequest request, Authentication auth) {
        OrderResponse placed = service.place(request, auth.getName());
        audit.record(auth.getName(), "ORDER_PLACED", placed.id());
        return placed;
    }

    /** Every order the signed-in customer has placed, newest first. */
    @GetMapping("/mine")
    public List<OrderResponse> mine(Authentication auth) {
        return service.listForUser(auth.getName());
    }

    @GetMapping
    public List<OrderResponse> list(@RequestParam(required = false) OrderStatus status) {
        return service.list(status);
    }

    @GetMapping("/{id}")
    public OrderResponse get(@PathVariable String id, Authentication auth) {
        boolean admin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        return service.getFor(id, auth.getName(), admin);
    }

    @PatchMapping("/{id}/status")
    public OrderResponse updateStatus(@PathVariable String id, @Valid @RequestBody StatusRequest request) {
        OrderResponse updated = service.updateStatus(id, request.status());
        audit.record(SecurityContextHolder.getContext().getAuthentication().getName(), "ORDER_STATUS_CHANGED",
                id + " -> " + request.status());
        return updated;
    }
}
