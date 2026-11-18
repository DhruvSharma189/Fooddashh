package com.company.fooddash.service;

import com.company.fooddash.dto.MenuItemRequest;
import com.company.fooddash.dto.MenuItemResponse;
import com.company.fooddash.exception.ResourceNotFoundException;
import com.company.fooddash.model.MenuItem;
import com.company.fooddash.repository.MenuItemRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class MenuServiceImpl implements MenuService {
    private final MenuItemRepository repository;
    private final MongoTemplate mongoTemplate;

    public MenuServiceImpl(MenuItemRepository repository, MongoTemplate mongoTemplate) {
        this.repository = repository;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public MenuItemResponse create(MenuItemRequest r) {
        return toResponse(repository.save(build(null, r, LocalDateTime.now())));
    }

    @Override
    public List<MenuItemResponse> list(String search, String category, Boolean veg, String sort) {
        List<Criteria> filters = new ArrayList<>();
        if (search != null && !search.isBlank())
            filters.add(Criteria.where("name").regex(Pattern.quote(search.trim()), "i"));
        if (category != null && !category.isBlank())
            filters.add(Criteria.where("category").regex("^" + Pattern.quote(category.trim()) + "$", "i"));
        if (veg != null)
            filters.add(Criteria.where("veg").is(veg));
        Query query = new Query();
        if (!filters.isEmpty())
            query.addCriteria(new Criteria().andOperator(filters.toArray(new Criteria[0])));
        query.with(sortFor(sort));
        return mongoTemplate.find(query, MenuItem.class).stream().map(this::toResponse).toList();
    }

    @Override
    public MenuItemResponse get(String id) {
        return toResponse(find(id));
    }

    @Override
    public MenuItemResponse update(String id, MenuItemRequest r) {
        MenuItem existing = find(id);
        return toResponse(repository.save(build(id, r, existing.createdAt())));
    }

    @Override
    public void delete(String id) {
        repository.delete(find(id));
    }

    @Override
    public List<String> categories() {
        return mongoTemplate.findDistinct(new Query(), "category", MenuItem.class, String.class).stream().sorted()
                .toList();
    }

    private Sort sortFor(String sort) {
        if (sort == null)
            return Sort.by(Sort.Direction.DESC, "rating");
        return switch (sort) {
            case "price_asc" -> Sort.by(Sort.Direction.ASC, "price");
            case "price_desc" -> Sort.by(Sort.Direction.DESC, "price");
            case "newest" -> Sort.by(Sort.Direction.DESC, "createdAt");
            default -> Sort.by(Sort.Direction.DESC, "rating");
        };
    }

    private MenuItem find(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with id: " + id));
    }

    private MenuItem build(String id, MenuItemRequest r, LocalDateTime createdAt) {
        return new MenuItem(id, r.name().trim(), r.description().trim(), r.category().trim(), r.price(), r.veg(),
                r.imageUrl().trim(), r.rating(), r.available() == null || r.available(), createdAt,
                r.stockQuantity() == null ? 50 : r.stockQuantity());
    }

    private MenuItemResponse toResponse(MenuItem m) {
        return new MenuItemResponse(m.id(), m.name(), m.description(), m.category(), m.price(), m.veg(),
                m.imageUrl(), m.rating(), m.available(), m.createdAt(),
                m.stockQuantity() == null ? 50 : m.stockQuantity());
    }
}
