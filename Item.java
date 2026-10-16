public class Item {
    private String name;
    private double price;
    private int stock;

    public Item(String name, double price, int stock) {
        this.name = name.toLowerCase();
        this.price = price;
        this.stock = stock;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    public void addStock(int amount) {
        this.stock += amount;
    }

    public boolean reduceStock(int amount) {
        if (stock >= amount) {
            stock -= amount;
            return true;
        }
        return false;
    }

    public String toString() {
        return name + " - $" + price + " - Stock: " + stock;
    }
}
