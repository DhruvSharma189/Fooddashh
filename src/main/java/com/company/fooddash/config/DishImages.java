package com.company.fooddash.config;

import java.util.List;
import java.util.Map;

/** Real food photos per category (replaces the random picsum.photos placeholders). */
public final class DishImages {
    private DishImages() {
    }

    private static final Map<String, List<String>> BY_CATEGORY = Map.of(
            "pizza", List.of("photo-1513104890138-7c749659a591", "photo-1579751626657-72bc17010498",
                    "photo-1565299624946-b28f40a0ae38", "photo-1574071318508-1cdbab80d002"),
            "burgers", List.of("photo-1568901346375-23c9450c58cd", "photo-1550547660-d9450f859349",
                    "photo-1571091718767-18b5b1457add"),
            "biryani", List.of("photo-1563379091339-03246963d96c", "photo-1589302168068-964664d93dc0",
                    "photo-1631515243349-e0cb75fb8d3a"),
            "desserts", List.of("photo-1488477181946-6428a0291777", "photo-1606313564200-e75d5e30476c",
                    "photo-1571115177098-24ec42ed204d"),
            "drinks", List.of("photo-1461023058943-07fcbe16d735", "photo-1495474472287-4d71bcdd2085",
                    "photo-1544787219-7f47ccb76574"));

    private static final String DEFAULT = "photo-1495474472287-4d71bcdd2085";

    public static String forDish(String category, String name) {
        List<String> ids = BY_CATEGORY.getOrDefault(category == null ? "" : category.toLowerCase(), List.of(DEFAULT));
        String id = ids.get(Math.abs((name == null ? "" : name).hashCode()) % ids.size());
        return "https://images.unsplash.com/" + id + "?auto=format&fit=crop&w=800&q=80";
    }

    public static boolean needsImage(String url) {
        return url == null || url.isBlank() || url.contains("picsum.photos");
    }
}
