package com.company.fooddash.model;

public record OrderItem(String menuItemId, String name, double price, int quantity) {
}
