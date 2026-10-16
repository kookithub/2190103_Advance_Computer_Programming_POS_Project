import java.util.*;

public class QueueManager {
    private static QueueManager instance;
    private Queue<Customer> customerQueue;
    private List<Table> tables;

    private QueueManager(int numberOfTables) {
        customerQueue = new LinkedList<>();
        tables = new ArrayList<>();
        for (int i = 1; i <= numberOfTables; i++) {
            tables.add(new Table(i));
        }
    }

    public static QueueManager getInstance(int numberOfTables) {
        if (instance == null) {
            instance = new QueueManager(numberOfTables);
        }
        return instance;
    }

    public void addCustomer(Customer customer) {
        customerQueue.add(customer);
        System.out.println(customer + " added to the queue.");
    }

    public void seatNextCustomer() {
        if (customerQueue.isEmpty()) {
            System.out.println("No customers in queue.");
            return;
        }

        for (Table table : tables) {
            if (table.isAvailable()) {
                Customer customer = customerQueue.poll();
                table.seatCustomer(customer);
                System.out.println(customer + " seated at Table " + table.getTableNumber());
                return;
            }
        }

        System.out.println("No available tables right now.");
    }

    public void freeTable(int tableNumber) {
        for (Table table : tables) {
            if (table.getTableNumber() == tableNumber && !table.isAvailable()) {
                table.free();
                System.out.println("Table " + tableNumber + " is now available.");
                return;
            }
        }
        System.out.println("Invalid table number or already available.");
    }

    public void displayQueue() {
        System.out.println("\nCurrent Queue:");
        if (customerQueue.isEmpty()) {
            System.out.println("No customers in queue.");
        } else {
            for (Customer c : customerQueue) {
                System.out.println(" - " + c);
            }
        }
    }

    public void displayTables() {
        System.out.println("\nTable Status:");
        for (Table t : tables) {
            System.out.println(" - " + t);
        }
    }

    public String getTablesInfo() {
        StringBuilder sb = new StringBuilder("=== Tables ===\n");
        for (Table table : tables) {
            sb.append("Table ").append(table.getTableNumber()).append(": ");
            if (table.isAvailable()) {
                sb.append("Available\n");
            } else {
                sb.append("Occupied by ").append(table.getCustomer().getName()).append("\n");
            }
        }
        return sb.toString();
    }

    public String getQueueInfo() {
        StringBuilder sb = new StringBuilder("=== Queue ===\n");
        if (customerQueue.isEmpty()) {
            sb.append("No customers in queue.\n");
        } else {
            for (Customer c : customerQueue) {
                sb.append(c.getName()).append(" (").append(c.getPartySize()).append(" people)\n");
            }
        }
        return sb.toString();
    }

}
