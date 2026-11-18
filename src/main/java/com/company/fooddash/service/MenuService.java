package com.company.fooddash.service;

import com.company.fooddash.dto.MenuItemRequest;
import com.company.fooddash.dto.MenuItemResponse;
import java.util.List;

public interface MenuService {
    MenuItemResponse create(MenuItemRequest request);

    List<MenuItemResponse> list(String search, String category, Boolean veg, String sort);

    MenuItemResponse get(String id);

    MenuItemResponse update(String id, MenuItemRequest request);

    void delete(String id);

    List<String> categories();
}
