public class Cashier {
    private final Inventory inventory;

    public Cashier(Inventory inventory) {
        this.inventory = inventory;
    }

    public double processOrder(String itemName, int quantity) {
        itemName = itemName.toLowerCase();
        Item item = inventory.getItem(itemName);

        if (item == null) {
            System.out.println("Item not found: " + itemName);
            return 0;
        }

        if (!inventory.isInStock(itemName, quantity)) {
            System.out.println("Not enough stock for: " + itemName);
            return 0;
        }

        double total = item.getPrice() * quantity;
        inventory.reduceStock(itemName, quantity);

        System.out.printf("%d x %s = $%.2f\n", quantity, item.getName(), total);
        return total;
    }
}
