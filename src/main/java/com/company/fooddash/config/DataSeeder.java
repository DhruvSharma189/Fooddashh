package com.company.fooddash.config;

import com.company.fooddash.model.MenuItem;
import com.company.fooddash.repository.MenuItemRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {
    private final MenuItemRepository repository;

    public DataSeeder(MenuItemRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        backfillMissingImages();
        if (repository.count() > 0) return;
        List<MenuItem> l = new ArrayList<>();
        l.add(m("Margherita Pizza", "Classic tomato sauce, fresh mozzarella and basil on a thin crust.", "Pizza", 249, true, 4.4, true));
        l.add(m("Farmhouse Pizza", "Loaded with capsicum, onion, mushroom and sweet corn.", "Pizza", 329, true, 4.3, true));
        l.add(m("Pepperoni Feast", "Double pepperoni, mozzarella and oregano.", "Pizza", 399, false, 4.6, true));
        l.add(m("BBQ Chicken Pizza", "Smoky BBQ sauce, grilled chicken and red onion.", "Pizza", 379, false, 4.5, false));
        l.add(m("Classic Veg Burger", "Crispy aloo patty, lettuce, tomato and house sauce.", "Burgers", 119, true, 4.0, true));
        l.add(m("Paneer Tikka Burger", "Tandoori paneer patty with mint mayo.", "Burgers", 159, true, 4.3, true));
        l.add(m("Crispy Chicken Burger", "Buttermilk fried chicken, pickles and spicy mayo.", "Burgers", 189, false, 4.5, true));
        l.add(m("Double Cheese Beef Burger", "Two patties, cheddar and caramelised onions.", "Burgers", 249, false, 4.2, true));
        l.add(m("Hyderabadi Chicken Biryani", "Dum-cooked basmati rice with tender chicken and saffron.", "Biryani", 289, false, 4.7, true));
        l.add(m("Veg Dum Biryani", "Fragrant rice layered with vegetables, mint and fried onions.", "Biryani", 219, true, 4.2, true));
        l.add(m("Mutton Biryani", "Slow-cooked mutton with aromatic spices and raita.", "Biryani", 349, false, 4.6, true));
        l.add(m("Chocolate Lava Cake", "Warm cake with a molten chocolate centre.", "Desserts", 99, true, 4.6, true));
        l.add(m("Gulab Jamun (2 pc)", "Soft milk dumplings in cardamom sugar syrup.", "Desserts", 69, true, 4.5, true));
        l.add(m("Mango Ice Cream Tub", "Creamy Alphonso mango ice cream, 500ml.", "Desserts", 179, true, 4.4, true));
        l.add(m("Fresh Lime Soda", "Sweet or salted, served chilled.", "Drinks", 59, true, 4.1, true));
        l.add(m("Cold Coffee", "Thick blended coffee with ice cream.", "Drinks", 99, true, 4.3, true));
        l.add(m("Masala Chai", "Strong ginger and cardamom tea.", "Drinks", 39, true, 4.5, true));
        repository.saveAll(l);
    }

    /** Existing databases already have items (some with no image / picsum placeholders, e.g. biryani). Fix them once. */
    private void backfillMissingImages() {
        for (MenuItem m : repository.findAll()) {
            if (!DishImages.needsImage(m.imageUrl())) continue;
            repository.save(new MenuItem(m.id(), m.name(), m.description(), m.category(), m.price(), m.veg(),
                    DishImages.forDish(m.category(), m.name()), m.rating(), m.available(), m.createdAt(),
                    m.stockQuantity()));
        }
    }

    private MenuItem m(String name, String desc, String cat, double price, boolean veg, double rating, boolean available) {
        return new MenuItem(null, name, desc, cat, price, veg,
                DishImages.forDish(cat, name),
                rating, available, LocalDateTime.now().minusMinutes((long) (price % 500)), 50);
    }
}
