import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Inventory {
    private Map<String, Item> items;

    public Inventory() {
        items = new HashMap<>();
    }

    public void addItem(String name, double price, int quantity) {
        name = name.toLowerCase(); // Normalize casing
        if (items.containsKey(name)) {
            Item existing = items.get(name);
            existing.addStock(quantity);
        } else {
            items.put(name, ItemFactory.createItem(name, price, quantity));

        }
    }

    public boolean isInStock(String name, int quantity) {
        Item item = items.get(name.toLowerCase());
        return item != null && item.getStock() >= quantity;
    }

    public boolean reduceStock(String name, int quantity) {
        Item item = items.get(name.toLowerCase());
        if (item != null) {
            return item.reduceStock(quantity);
        }
        return false;
    }

    public Item getItem(String name) {
        return items.get(name.toLowerCase());
    }

    /** Read-only snapshot of all items, used by the GUI product table. */
    public List<Item> getItems() {
        return new ArrayList<>(items.values());
    }

    public void printInventory() {
        System.out.println("=== INVENTORY ===");
        for (Item item : items.values()) {
            System.out.println(item);
        }
    }

    public String getInventoryDetails() {
        StringBuilder sb = new StringBuilder();
        for (String itemName : items.keySet()) {
            Item item = items.get(itemName);
            sb.append(String.format("%s - $%.2f - Stock: %d\n", itemName, item.getPrice(), item.getStock()));
        }
        return sb.toString();
    }

}
