public class Table {
    private int tableNumber;
    private boolean isAvailable;
    private Customer currentCustomer;

    public Table(int tableNumber) {
        this.tableNumber = tableNumber;
        this.isAvailable = true;
    }

    public int getTableNumber() {
        return tableNumber;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void seatCustomer(Customer customer) {
        this.isAvailable = false;
        this.currentCustomer = customer;
    }

    public Customer getCustomer() {
        return currentCustomer;
    }

    public void free() {
        isAvailable = true;
        currentCustomer = null;
    }

    @Override
    public String toString() {
        return "Table " + tableNumber + ": " + (isAvailable ? "Available" : "Occupied by " + currentCustomer.getName());
    }
}
