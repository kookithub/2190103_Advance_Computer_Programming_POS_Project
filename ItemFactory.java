public class ItemFactory {
    public static Item createItem(String name, double price, int stock) {
        return new Item(name, price, stock);
    }
}
