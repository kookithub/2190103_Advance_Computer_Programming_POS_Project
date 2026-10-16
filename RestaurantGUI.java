import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class RestaurantGUI {
    private JFrame frame;
    private Inventory inventory;
    private Cashier cashier;
    private JTextArea orderDisplay;
    private double orderTotal;

    public RestaurantGUI() {
        inventory = new Inventory();
        cashier = new Cashier(inventory);
        orderTotal = 0;

        inventory.addItem("Macaron1", 3.0, 10);
        inventory.addItem("Macaron2", 3.5, 8);
        inventory.addItem("Macaron3", 4.0, 5);

        frame = new JFrame("Restaurant Cashier");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 500);
        frame.setLayout(new BorderLayout());

        orderDisplay = new JTextArea();
        orderDisplay.setEditable(false);
        frame.add(new JScrollPane(orderDisplay), BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new GridLayout(0, 1));

        JButton orderBtn = new JButton("Order Item");
        orderBtn.addActionListener(this::handleOrder);

        JButton discountBtn = new JButton("Apply Discount");
        discountBtn.addActionListener(this::handleDiscount);

        JButton inventoryBtn = new JButton("Check Inventory");
        inventoryBtn.addActionListener(e -> {
            orderDisplay.append("\n--- Inventory ---\n" + inventory.getInventoryDetails());
        });

        JButton addItemBtn = new JButton("Add Item");
        addItemBtn.addActionListener(this::handleAddItem);

        JButton backBtn = new JButton("Back");
        backBtn.addActionListener(e -> {
            frame.dispose();
            new MainApp();
        });

        controlPanel.add(orderBtn);
        controlPanel.add(discountBtn);
        controlPanel.add(inventoryBtn);
        controlPanel.add(addItemBtn);
        controlPanel.add(backBtn);

        frame.add(controlPanel, BorderLayout.EAST);
        frame.setVisible(true);
    }

    private void handleOrder(ActionEvent e) {
        String item = JOptionPane.showInputDialog(frame, "Enter item name:");
        String quantityStr = JOptionPane.showInputDialog(frame, "Enter quantity:");
        try {
            int qty = Integer.parseInt(quantityStr);
            double total = cashier.processOrder(item, qty);
            orderTotal += total;
            orderDisplay.append(String.format("Added %d x %s = $%.2f\n", qty, item, total));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Invalid input or item not available.");
        }
    }

    private void handleDiscount(ActionEvent e) {
        String[] options = {"None", "Coupon", "VATFree", "Percent"};
        String type = (String) JOptionPane.showInputDialog(frame, "Choose discount type:", "Discount", JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        if (type == null || type.equals("None")) {
            orderDisplay.append(String.format("No discount applied. Final price: $%.2f\n", orderTotal));
        } else if (type.equals("Coupon")) {
            double amount = Double.parseDouble(JOptionPane.showInputDialog("Enter coupon amount:"));
            double minSpend = 4 * amount;
            if (orderTotal < minSpend) {
                orderDisplay.append(String.format("Coupon not applied. Spend at least $%.2f\n", minSpend));
            } else {
                discountStrategy coupon = new CouponDiscountStrategy(amount, minSpend);
                double finalPrice = coupon.applyDiscount(orderTotal);
                orderDisplay.append(String.format("After coupon discount: $%.2f\n", finalPrice));
            }
        } else if (type.equals("VATFree")) {
            discountStrategy vatFree = new VATFreeDiscountStrategy(0.07);
            double finalPrice = vatFree.applyDiscount(orderTotal);
            orderDisplay.append(String.format("After VAT removal: $%.2f\n", finalPrice));
        } else if (type.equals("Percent")) {
            double percent = Double.parseDouble(JOptionPane.showInputDialog("Enter discount percentage:"));
            discountStrategy percentDiscount = new PercentDiscountStrategy(percent);
            double finalPrice = percentDiscount.applyDiscount(orderTotal);
            orderDisplay.append(String.format("After %.0f%% discount: $%.2f\n", percent, finalPrice));
        }
        orderTotal = 0;
    }

    private void handleAddItem(ActionEvent e) {
        String name = JOptionPane.showInputDialog("Enter item name:");
        double price = Double.parseDouble(JOptionPane.showInputDialog("Enter price:"));
        int stock = Integer.parseInt(JOptionPane.showInputDialog("Enter stock:"));
        inventory.addItem(name, price, stock);
        orderDisplay.append("Item added to inventory: " + name + "\n");
    }

}

