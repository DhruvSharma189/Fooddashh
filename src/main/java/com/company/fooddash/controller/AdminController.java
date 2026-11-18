package com.company.fooddash.controller;

import com.company.fooddash.model.*;
import com.company.fooddash.repository.*;
import com.company.fooddash.service.AuditService;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final OrderRepository orders;
    private final MenuItemRepository menu;
    private final AppUserRepository users;
    private final AuditEventRepository audit;
    private final AuditService auditService;
    private final PasswordEncoder encoder;

    public AdminController(OrderRepository o, MenuItemRepository m, AppUserRepository u, AuditEventRepository a,
            AuditService as, PasswordEncoder pe) {
        orders = o;
        menu = m;
        users = u;
        audit = a;
        auditService = as;
        encoder = pe;
    }

    @GetMapping("/analytics")
    public Map<String, Object> analytics() {
        var all = orders.findAll();
        var paid = all.stream().filter(o -> o.status() != OrderStatus.CANCELLED).toList();
        double revenue = paid.stream().mapToDouble(Order::total).sum();
        Map<String, Long> status = all.stream()
                .collect(Collectors.groupingBy(o -> o.status().name(), Collectors.counting()));
        Map<String, Long> categories = new TreeMap<>();
        Map<String, Long> dishes = new HashMap<>();
        for (Order o : paid)
            for (OrderItem i : o.items()) {
                dishes.merge(i.name(), (long) i.quantity(), Long::sum);
                menu.findById(i.menuItemId())
                        .ifPresent(m -> categories.merge(m.category(), (long) i.quantity(), Long::sum));
            }
        return Map.of("revenue", revenue, "orders", all.size(), "activeOrders",
                all.stream()
                        .filter(o -> o.status() == OrderStatus.PLACED || o.status() == OrderStatus.PREPARING
                                || o.status() == OrderStatus.OUT_FOR_DELIVERY)
                        .count(),
                "averageOrderValue", paid.isEmpty() ? 0 : revenue / paid.size(), "statuses", status, "categories",
                categories, "topDishes", dishes.entrySet().stream()
                        .sorted(Map.Entry.<String, Long>comparingByValue().reversed()).limit(8).toList());
    }

    @GetMapping("/audit")
    public List<AuditEvent> audit() {
        return audit.findTop100ByOrderByCreatedAtDesc();
    }

    @GetMapping("/users")
    public List<Map<String, Object>> users() {
        return users.findAll().stream().map(u -> Map.<String, Object>of("id", u.id(), "name", u.name(), "email",
                u.email(), "roles", u.roles(), "enabled", u.enabled(), "createdAt", u.createdAt())).toList();
    }

    @PostMapping("/users")
    public Map<String, String> createAdmin(@RequestBody Map<String, String> body) {
        String email = Objects.toString(body.get("email"), "").trim().toLowerCase();
        String password = Objects.toString(body.get("password"), "");
        String name = Objects.toString(body.get("name"), "").trim();
        if (name.length() < 2 || !email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$") || password.length() < 12)
            throw new IllegalArgumentException("Provide a name, valid email and password of at least 12 characters");
        if (users.existsByEmailIgnoreCase(email))
            throw new IllegalArgumentException("Email already exists");
        users.save(
                new AppUser(null, name, email, encoder.encode(password), Set.of("ADMIN"), true, LocalDateTime.now()));
        auditService.record("admin", "ADMIN_CREATED", email);
        return Map.of("message", "Admin account created");
    }

    @PatchMapping("/users/{id}/enabled")
    public Map<String, String> enabled(@PathVariable String id, @RequestBody Map<String, Boolean> body) {
        AppUser u = users.findById(id).orElseThrow();
        users.save(new AppUser(u.id(), u.name(), u.email(), u.passwordHash(), u.roles(),
                Boolean.TRUE.equals(body.get("enabled")), u.createdAt()));
        auditService.record("admin", "USER_STATUS_CHANGED", u.email());
        return Map.of("message", "User updated");
    }

    @GetMapping("/inventory/low-stock")
    public List<MenuItem> lowStock(@RequestParam(defaultValue = "5") int threshold) {
        return menu.findAll().stream().filter(m -> m.stockQuantity() != null && m.stockQuantity() <= threshold)
                .toList();
    }

    @GetMapping(value = "/sales.csv", produces = "text/csv")
    public ResponseEntity<String> salesCsv() {
        StringBuilder b = new StringBuilder("order_id,customer,total,status,created_at\n");
        for (Order o : orders.findAllByOrderByCreatedAtDesc())
            b.append(csv(o.id())).append(',').append(csv(o.customerName())).append(',').append(o.total()).append(',')
                    .append(o.status()).append(',').append(o.createdAt()).append('\n');
        auditService.record("admin", "SALES_EXPORT", "CSV sales report exported");
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=fooddash-sales.csv")
                .contentType(MediaType.parseMediaType("text/csv")).body(b.toString());
    }

    private String csv(String s) {
        return "\"" + Objects.toString(s, "").replace("\"", "\"\"") + "\"";
    }
}
