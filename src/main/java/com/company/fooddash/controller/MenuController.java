package com.company.fooddash.controller;

import com.company.fooddash.dto.MenuItemRequest;
import com.company.fooddash.dto.MenuItemResponse;
import com.company.fooddash.service.MenuService;
import com.company.fooddash.service.AuditService;
import org.springframework.security.core.context.SecurityContextHolder;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/menu")
public class MenuController {
    private final MenuService service;
    private final AuditService audit;

    public MenuController(MenuService service, AuditService audit) {
        this.service = service;
        this.audit = audit;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MenuItemResponse create(@Valid @RequestBody MenuItemRequest request) {
        MenuItemResponse created = service.create(request);
        audit.record(actor(), "MENU_CREATED", created.name());
        return created;
    }

    private String actor() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @GetMapping
    public List<MenuItemResponse> list(@RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Boolean veg,
            @RequestParam(required = false) String sort) {
        return service.list(search, category, veg, sort);
    }

    @GetMapping("/categories")
    public List<String> categories() {
        return service.categories();
    }

    @GetMapping("/{id}")
    public MenuItemResponse get(@PathVariable String id) {
        return service.get(id);
    }

    @PutMapping("/{id}")
    public MenuItemResponse update(@PathVariable String id, @Valid @RequestBody MenuItemRequest request) {
        MenuItemResponse updated = service.update(id, request);
        audit.record(actor(), "MENU_UPDATED", updated.name());
        return updated;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        service.delete(id);
        audit.record(actor(), "MENU_DELETED", id);
    }
}
