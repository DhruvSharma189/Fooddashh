package com.company.fooddash.service;

import com.company.fooddash.dto.OrderRequest;
import com.company.fooddash.dto.OrderResponse;
import com.company.fooddash.exception.ResourceNotFoundException;
import com.company.fooddash.model.MenuItem;
import com.company.fooddash.model.Order;
import com.company.fooddash.model.OrderItem;
import com.company.fooddash.model.OrderStatus;
import com.company.fooddash.repository.MenuItemRepository;
import com.company.fooddash.repository.OrderRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orders;
    private final MenuItemRepository menu;

    public OrderServiceImpl(OrderRepository orders, MenuItemRepository menu) {
        this.orders = orders;
        this.menu = menu;
    }

    @Override
    public OrderResponse place(OrderRequest r, String userEmail) {
        List<OrderItem> items = new ArrayList<>();
        List<MenuItem> selected = new ArrayList<>();
        double total = 0;
        for (OrderRequest.Line line : r.items()) {
            MenuItem m = menu.findById(line.menuItemId())
                    .orElseThrow(
                            () -> new ResourceNotFoundException("Menu item not found with id: " + line.menuItemId()));
            int stock = m.stockQuantity() == null ? 50 : m.stockQuantity();
            if (!m.available())
                throw new IllegalArgumentException(m.name() + " is currently unavailable");
            if (stock < line.quantity())
                throw new IllegalArgumentException(m.name() + " has insufficient stock");
            selected.add(m);
            items.add(new OrderItem(m.id(), m.name(), m.price(), line.quantity()));
            total += m.price() * line.quantity(); // price always comes from the database, never the client
        }
        // Apply inventory deductions only after every cart line has passed validation.
        for (int i = 0; i < selected.size(); i++) {
            MenuItem m = selected.get(i);
            int quantity = r.items().get(i).quantity();
            menu.save(new MenuItem(m.id(), m.name(), m.description(), m.category(), m.price(), m.veg(),
                    m.imageUrl(), m.rating(), m.available(), m.createdAt(),
                    (m.stockQuantity() == null ? 50 : m.stockQuantity()) - quantity));
        }
        Order o = new Order(null, r.customerName().trim(), r.phone(), r.address().trim(), items,
                Math.round(total * 100) / 100.0, OrderStatus.PLACED, LocalDateTime.now(), userEmail);
        return toResponse(orders.save(o));
    }

    @Override
    public List<OrderResponse> list(OrderStatus status) {
        List<Order> list = status == null ? orders.findAllByOrderByCreatedAtDesc()
                : orders.findByStatusOrderByCreatedAtDesc(status);
        return list.stream().map(this::toResponse).toList();
    }

    @Override
    public List<OrderResponse> listForUser(String userEmail) {
        return orders.findByUserEmailIgnoreCaseOrderByCreatedAtDesc(userEmail).stream().map(this::toResponse).toList();
    }

    @Override
    public OrderResponse get(String id) {
        return toResponse(find(id));
    }

    @Override
    public OrderResponse getFor(String id, String userEmail, boolean admin) {
        Order o = find(id);
        if (!admin && (o.userEmail() == null || !o.userEmail().equalsIgnoreCase(userEmail)))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This order belongs to another account");
        return toResponse(o);
    }

    @Override
    public OrderResponse updateStatus(String id, OrderStatus next) {
        Order o = find(id);
        if (o.status() == OrderStatus.DELIVERED || o.status() == OrderStatus.CANCELLED)
            throw new IllegalArgumentException("A " + o.status() + " order cannot be changed");
        if (next.ordinal() <= o.status().ordinal())
            throw new IllegalArgumentException("Cannot move order from " + o.status() + " back to " + next);
        return toResponse(orders.save(o.withStatus(next)));
    }

    private Order find(String id) {
        return orders.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
    }

    private OrderResponse toResponse(Order o) {
        return new OrderResponse(o.id(), o.customerName(), o.phone(), o.address(), o.items(), o.total(), o.status(),
                o.createdAt());
    }
}
