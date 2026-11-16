import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Cashier screen: product tiles, order lines, discount selection, checkout and receipt.
 * All money/stock logic is delegated to Cashier / Inventory / discountStrategy.
 */
public class RestaurantGUI {
    private static final double VAT_RATE = 0.07;
    private static final String[][] DISCOUNTS = {
            {"None", "None"}, {"Percent", "Percent"}, {"Coupon", "Coupon"}, {"VAT-free", "VATFree"}};

    private JFrame frame;
    private Inventory inventory;
    private Cashier cashier;

    private JPanel productGrid, linesPanel;
    private JLabel subtotalVal, discountVal, totalVal, countLabel, hintLabel, statusLabel;
    private JTextField discountValue;
    private Theme.Btn checkoutBtn;
    private final Map<String, Theme.Btn> pills = new LinkedHashMap<>();
    private String discountType = "None";

    private final Map<String, Integer> cart = new LinkedHashMap<>();

    public RestaurantGUI() {
        inventory = new Inventory();
        cashier = new Cashier(inventory);

        inventory.addItem("Macaron1", 3.0, 10);
        inventory.addItem("Macaron2", 3.5, 8);
        inventory.addItem("Macaron3", 4.0, 5);
        inventory.addItem("Macaron4", 4.5, 12);
        inventory.addItem("Macaron5", 5.0, 3);
        inventory.addItem("Macaron6", 5.5, 0);

        frame = new JFrame("Restaurant Cashier");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1120, 700);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setBackground(Theme.BG);

        JPanel root = new JPanel(new BorderLayout(0, 18));
        root.setBackground(Theme.BG);
        root.setBorder(BorderFactory.createEmptyBorder(20, 24, 14, 24));
        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildProducts(), BorderLayout.CENTER);
        root.add(buildOrderCard(), BorderLayout.EAST);
        frame.setContentPane(root);

        refreshProducts();
        refreshCart();
        frame.setVisible(true);
    }

    // ------------------------------------------------------------ layout

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        JLabel title = Theme.label("Cashier", Font.BOLD, 28f, Theme.TEXT);
        JLabel sub = Theme.label("Tap a product to add it to the order", Font.PLAIN, 14f, Theme.MUTED);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);
        left.add(title);
        left.add(sub);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);
        Theme.Btn newItem = new Theme.Btn("+ New item", Theme.Kind.SECONDARY);
        newItem.addActionListener(e -> showNewItemDialog());
        Theme.Btn back = new Theme.Btn("← Menu", Theme.Kind.GHOST);
        back.addActionListener(e -> {
            frame.dispose();
            new MainApp();
        });
        right.add(newItem);
        right.add(back);
        header.add(left, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);
        return header;
    }

    private JComponent buildProducts() {
        JPanel wrap = new JPanel(new BorderLayout(0, 8));
        wrap.setOpaque(false);
        productGrid = new JPanel(new GridLayout(0, 3, 16, 16));
        productGrid.setOpaque(false);
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 8));
        top.add(productGrid, BorderLayout.NORTH);
        JScrollPane sp = new JScrollPane(top);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        statusLabel = Theme.label(" ", Font.PLAIN, 13f, Theme.MUTED);
        wrap.add(sp, BorderLayout.CENTER);
        wrap.add(statusLabel, BorderLayout.SOUTH);
        return wrap;
    }

    private JPanel buildOrderCard() {
        Theme.Card card = new Theme.Card(new BorderLayout(0, 12), 20, 20);
        card.setPreferredSize(new Dimension(410, 0));

        JPanel head = new JPanel(new BorderLayout());
        head.setOpaque(false);
        head.add(Theme.label("Current order", Font.BOLD, 18f, Theme.TEXT), BorderLayout.WEST);
        countLabel = Theme.label("0 items", Font.PLAIN, 13f, Theme.MUTED);
        head.add(countLabel, BorderLayout.EAST);
        card.add(head, BorderLayout.NORTH);

        linesPanel = new JPanel();
        linesPanel.setOpaque(false);
        linesPanel.setLayout(new BoxLayout(linesPanel, BoxLayout.Y_AXIS));
        JPanel linesWrap = new JPanel(new BorderLayout());
        linesWrap.setOpaque(false);
        linesWrap.add(linesPanel, BorderLayout.NORTH);
        JScrollPane sp = new JScrollPane(linesWrap);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        card.add(sp, BorderLayout.CENTER);

        JPanel south = new JPanel();
        south.setOpaque(false);
        south.setLayout(new BoxLayout(south, BoxLayout.Y_AXIS));
        south.add(Theme.divider());
        south.add(Box.createVerticalStrut(12));
        JLabel dl = Theme.label("Discount", Font.BOLD, 13f, Theme.MUTED);
        dl.setAlignmentX(Component.LEFT_ALIGNMENT);
        south.add(dl);
        south.add(Box.createVerticalStrut(6));

        JPanel pillRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        pillRow.setOpaque(false);
        pillRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        for (String[] d : DISCOUNTS) {
            Theme.Btn b = new Theme.Btn(d[0], Theme.Kind.SECONDARY);
            b.setFont(Theme.font(Font.BOLD, 12f));
            b.setPreferredSize(new Dimension(b.getFontMetrics(b.getFont()).stringWidth(d[0]) + 26, 32));
            b.addActionListener(e -> selectDiscount(d[1]));
            pills.put(d[1], b);
            pillRow.add(b);
        }
        south.add(pillRow);
        south.add(Box.createVerticalStrut(8));

        JPanel valueRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        valueRow.setOpaque(false);
        valueRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        discountValue = Theme.field(5);
        discountValue.setText("10");
        discountValue.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override public void keyReleased(java.awt.event.KeyEvent e) { refreshCart(); }
        });
        hintLabel = Theme.label("", Font.PLAIN, 12f, Theme.MUTED);
        valueRow.add(discountValue);
        valueRow.add(hintLabel);
        south.add(valueRow);
        south.add(Box.createVerticalStrut(14));

        south.add(totalRow("Subtotal", subtotalVal = Theme.label("", Font.PLAIN, 14f, Theme.TEXT)));
        south.add(Box.createVerticalStrut(4));
        south.add(totalRow("Discount", discountVal = Theme.label("", Font.PLAIN, 14f, Theme.OK)));
        south.add(Box.createVerticalStrut(8));
        south.add(totalRow("Total", totalVal = Theme.label("", Font.BOLD, 26f, Theme.PRIMARY)));
        south.add(Box.createVerticalStrut(14));

        JPanel actions = new JPanel(new BorderLayout(8, 0));
        actions.setOpaque(false);
        actions.setAlignmentX(Component.LEFT_ALIGNMENT);
        actions.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        Theme.Btn clear = new Theme.Btn("Clear", Theme.Kind.DANGER);
        clear.addActionListener(e -> {
            cart.clear();
            refreshCart();
            refreshProducts();
            status("Order cleared");
        });
        checkoutBtn = new Theme.Btn("Checkout & print receipt", Theme.Kind.PRIMARY) {
            @Override public Dimension getPreferredSize() { return new Dimension(100, 46); }
        };
        checkoutBtn.setFont(Theme.font(Font.BOLD, 15f));
        checkoutBtn.addActionListener(e -> checkout());
        actions.add(clear, BorderLayout.WEST);
        actions.add(checkoutBtn, BorderLayout.CENTER);
        south.add(actions);

        card.add(south, BorderLayout.SOUTH);
        selectDiscount("None");
        return card;
    }

    private JPanel totalRow(String name, JLabel value) {
        JPanel r = new JPanel(new BorderLayout());
        r.setOpaque(false);
        r.setAlignmentX(Component.LEFT_ALIGNMENT);
        r.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        r.add(Theme.label(name, Font.PLAIN, 14f, Theme.MUTED), BorderLayout.WEST);
        r.add(value, BorderLayout.EAST);
        return r;
    }

    // ------------------------------------------------------------ product tile

    private class ProductTile extends JButton {
        private final String name;
        private final double price;
        private final int remaining;
        private boolean hover;

        ProductTile(String name, double price, int remaining) {
            this.name = name;
            this.price = price;
            this.remaining = remaining;
            setName("tile-" + name);
            setText(name);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setEnabled(remaining > 0);
            setCursor(Cursor.getPredefinedCursor(remaining > 0 ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
            setPreferredSize(new Dimension(180, 132));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                @Override public void mouseExited(MouseEvent e) { hover = false; repaint(); }
            });
            addActionListener(e -> addToCart(name));
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight() - 3;
            g.setColor(new Color(17, 24, 39, 10));
            g.fill(new RoundRectangle2D.Float(0, 2, w, h, 20, 20));
            g.setColor(isEnabled() ? Color.WHITE : new Color(0xF9FAFB));
            g.fill(new RoundRectangle2D.Float(0, 0, w, h, 20, 20));
            g.setColor(hover && isEnabled() ? Theme.PRIMARY : Theme.BORDER);
            g.setStroke(new BasicStroke(hover && isEnabled() ? 2f : 1f));
            g.draw(new RoundRectangle2D.Float(1, 1, w - 2, h - 2, 20, 20));

            // initial badge
            g.setColor(isEnabled() ? Theme.PRIMARY_SOFT : new Color(0xE5E7EB));
            g.fill(new RoundRectangle2D.Float(16, 16, 40, 40, 14, 14));
            g.setFont(Theme.font(Font.BOLD, 18f));
            g.setColor(isEnabled() ? Theme.PRIMARY : Theme.MUTED);
            String initial = name.substring(0, 1).toUpperCase();
            FontMetrics fm = g.getFontMetrics();
            g.drawString(initial, 16 + (40 - fm.stringWidth(initial)) / 2, 16 + (40 - fm.getHeight()) / 2 + fm.getAscent());

            // stock chip
            String chip;
            Color cbg, cfg;
            if (remaining <= 0) { chip = "Sold out"; cbg = Theme.BAD_SOFT; cfg = Theme.BAD; }
            else if (remaining <= 3) { chip = remaining + " left"; cbg = Theme.WARN_SOFT; cfg = Theme.WARN; }
            else { chip = remaining + " in stock"; cbg = Theme.OK_SOFT; cfg = Theme.OK; }
            g.setFont(Theme.font(Font.BOLD, 11f));
            fm = g.getFontMetrics();
            int cw = fm.stringWidth(chip) + 18;
            g.setColor(cbg);
            g.fill(new RoundRectangle2D.Float(w - cw - 14, 20, cw, 22, 22, 22));
            g.setColor(cfg);
            g.drawString(chip, w - cw - 14 + 9, 20 + (22 - fm.getHeight()) / 2 + fm.getAscent());

            // name + price
            g.setFont(Theme.font(Font.BOLD, 16f));
            g.setColor(isEnabled() ? Theme.TEXT : Theme.MUTED);
            String shown = name.substring(0, 1).toUpperCase() + name.substring(1);
            g.drawString(shown, 18, 84);
            g.setFont(Theme.font(Font.BOLD, 18f));
            g.setColor(isEnabled() ? Theme.PRIMARY : Theme.MUTED);
            g.drawString(String.format("$%.2f", price), 18, 110);
            g.dispose();
        }
    }

    // ------------------------------------------------------------ actions

    private void addToCart(String name) {
        int already = cart.getOrDefault(name, 0);
        if (!inventory.isInStock(name, already + 1)) {
            status("Not enough stock for " + name);
            return;
        }
        cart.put(name, already + 1);
        refreshCart();
        refreshProducts();
        status("Added 1 x " + name);
    }

    private void selectDiscount(String type) {
        discountType = type;
        for (Map.Entry<String, Theme.Btn> e : pills.entrySet()) {
            e.getValue().setSelectedLook(e.getKey().equals(type));
        }
        boolean needsValue = type.equals("Percent") || type.equals("Coupon");
        discountValue.setEnabled(needsValue);
        discountValue.setVisible(needsValue);
        if (type.equals("Percent")) {
            discountValue.setText("10");
            hintLabel.setText("% off the whole bill");
        } else if (type.equals("Coupon")) {
            discountValue.setText("5");
            hintLabel.setText("$ off, min. spend = 4 x coupon");
        } else if (type.equals("VATFree")) {
            hintLabel.setText("Removes 7% VAT");
        } else {
            hintLabel.setText("No discount");
        }
        refreshCart();
    }

    private double subtotal() {
        double sum = 0;
        for (Map.Entry<String, Integer> e : cart.entrySet()) {
            sum += inventory.getItem(e.getKey()).getPrice() * e.getValue();
        }
        return sum;
    }

    /** Builds the strategy for the selected discount type; null means no discount. */
    private discountStrategy currentStrategy() {
        if (discountType.equals("VATFree")) return new VATFreeDiscountStrategy(VAT_RATE);
        try {
            double v = Double.parseDouble(discountValue.getText().trim());
            switch (discountType) {
                case "Percent": return new PercentDiscountStrategy(v);
                case "Coupon":  return new CouponDiscountStrategy(v, 4 * v);
                default:        return null;
            }
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private double total(double sub) {
        discountStrategy s = currentStrategy();
        return s == null ? sub : s.applyDiscount(sub);
    }

    private void checkout() {
        if (cart.isEmpty()) {
            status("The order is empty");
            return;
        }
        double sub = subtotal();
        double total = total(sub);
        StringBuilder sb = new StringBuilder();
        sb.append("        MACARON SHOP\n");
        sb.append("   ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))).append("\n");
        sb.append("--------------------------------\n");
        for (Map.Entry<String, Integer> e : cart.entrySet()) {
            double line = cashier.processOrder(e.getKey(), e.getValue());   // deducts stock
            sb.append(String.format("%-10s %2d x %5.2f %7.2f%n", e.getKey(), e.getValue(),
                    inventory.getItem(e.getKey()).getPrice(), line));
        }
        sb.append("--------------------------------\n");
        sb.append(String.format("Subtotal %23.2f%n", sub));
        sb.append(String.format("Discount (%s) %s%n", discountType.equals("VATFree") ? "VAT-free" : discountType,
                String.format("%" + Math.max(1, 17 - discountType.length()) + ".2f", -(sub - total))));
        sb.append("--------------------------------\n");
        sb.append(String.format("TOTAL %26.2f%n", total));
        sb.append("\n    Thank you, see you again!\n");

        cart.clear();
        refreshProducts();
        refreshCart();
        showReceipt(sb.toString());
        status(String.format("Checked out: $%.2f", total));
    }

    private void showReceipt(String text) {
        JDialog d = new JDialog(frame, "Receipt", true);
        d.getContentPane().setBackground(Theme.BG);
        JPanel root = new JPanel(new BorderLayout(0, 14));
        root.setBackground(Theme.BG);
        root.setBorder(BorderFactory.createEmptyBorder(18, 18, 16, 18));
        Theme.Card paper = new Theme.Card(new BorderLayout(), 14, 18).fill(new Color(0xFFFEFA));
        JTextArea ta = new JTextArea(text);
        ta.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        ta.setForeground(Theme.TEXT);
        ta.setOpaque(false);
        ta.setEditable(false);
        paper.add(ta, BorderLayout.CENTER);
        Theme.Btn done = new Theme.Btn("Done", Theme.Kind.PRIMARY);
        done.addActionListener(e -> d.dispose());
        JPanel s = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        s.setOpaque(false);
        s.add(done);
        root.add(paper, BorderLayout.CENTER);
        root.add(s, BorderLayout.SOUTH);
        d.setContentPane(root);
        d.pack();
        d.setLocationRelativeTo(frame);
        d.setVisible(true);
    }

    private void showNewItemDialog() {
        JDialog d = new JDialog(frame, "New item", true);
        JPanel root = new JPanel();
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setBackground(Theme.BG);
        root.setBorder(BorderFactory.createEmptyBorder(20, 22, 18, 22));
        JTextField name = Theme.field(16), price = Theme.field(16), stock = Theme.field(16);
        root.add(Theme.label("Add or restock a product", Font.BOLD, 17f, Theme.TEXT));
        root.add(Box.createVerticalStrut(12));
        for (Object[] row : new Object[][]{{"Name", name}, {"Price ($)", price}, {"Stock", stock}}) {
            JLabel l = Theme.label((String) row[0], Font.PLAIN, 12f, Theme.MUTED);
            l.setAlignmentX(Component.LEFT_ALIGNMENT);
            JTextField f = (JTextField) row[1];
            f.setAlignmentX(Component.LEFT_ALIGNMENT);
            root.add(l);
            root.add(Box.createVerticalStrut(3));
            root.add(f);
            root.add(Box.createVerticalStrut(10));
        }
        JLabel err = Theme.label(" ", Font.PLAIN, 12f, Theme.BAD);
        err.setAlignmentX(Component.LEFT_ALIGNMENT);
        root.add(err);
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btns.setOpaque(false);
        btns.setAlignmentX(Component.LEFT_ALIGNMENT);
        Theme.Btn cancel = new Theme.Btn("Cancel", Theme.Kind.GHOST);
        cancel.addActionListener(e -> d.dispose());
        Theme.Btn add = new Theme.Btn("Add to inventory", Theme.Kind.PRIMARY);
        add.addActionListener(e -> {
            try {
                String n = name.getText().trim();
                if (n.isEmpty()) throw new IllegalArgumentException();
                inventory.addItem(n, Double.parseDouble(price.getText().trim()), Integer.parseInt(stock.getText().trim()));
                refreshProducts();
                status("Inventory updated: " + n);
                d.dispose();
            } catch (RuntimeException ex) {
                err.setText("Enter a name, a numeric price and an integer stock.");
            }
        });
        btns.add(cancel);
        btns.add(add);
        root.add(btns);
        d.setContentPane(root);
        d.pack();
        d.setLocationRelativeTo(frame);
        d.setVisible(true);
    }

    // ------------------------------------------------------------ view refresh

    private void refreshProducts() {
        productGrid.removeAll();
        for (Item it : inventory.getItems()) {
            int remaining = it.getStock() - cart.getOrDefault(it.getName(), 0);
            productGrid.add(new ProductTile(it.getName(), it.getPrice(), remaining));
        }
        productGrid.revalidate();
        productGrid.repaint();
    }

    private void refreshCart() {
        linesPanel.removeAll();
        int count = 0;
        if (cart.isEmpty()) {
            JLabel empty = Theme.label("No items yet - tap a product", Font.PLAIN, 14f, Theme.MUTED);
            empty.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));
            empty.setAlignmentX(Component.LEFT_ALIGNMENT);
            linesPanel.add(empty);
        }
        for (Map.Entry<String, Integer> e : cart.entrySet()) {
            String name = e.getKey();
            Item item = inventory.getItem(name);
            count += e.getValue();
            JPanel row = new JPanel(new BorderLayout(10, 0));
            row.setOpaque(false);
            row.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));
            row.setAlignmentX(Component.LEFT_ALIGNMENT);
            JPanel names = new JPanel(new GridLayout(2, 1));
            names.setOpaque(false);
            names.add(Theme.label(name.substring(0, 1).toUpperCase() + name.substring(1), Font.BOLD, 14f, Theme.TEXT));
            names.add(Theme.label(String.format("$%.2f each", item.getPrice()), Font.PLAIN, 12f, Theme.MUTED));
            Theme.Stepper st = new Theme.Stepper(e.getValue(), 0, item.getStock());
            st.setOnChange(() -> {
                if (st.getValue() == 0) cart.remove(name); else cart.put(name, st.getValue());
                refreshCart();
                refreshProducts();
            });
            JLabel sub = Theme.label(String.format("$%.2f", item.getPrice() * e.getValue()), Font.BOLD, 14f, Theme.TEXT);
            sub.setPreferredSize(new Dimension(64, 24));
            sub.setHorizontalAlignment(SwingConstants.RIGHT);
            JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 4));
            right.setOpaque(false);
            right.add(st);
            right.add(sub);
            row.add(names, BorderLayout.CENTER);
            row.add(right, BorderLayout.EAST);
            linesPanel.add(row);
            linesPanel.add(Theme.divider());
        }
        countLabel.setText(count + (count == 1 ? " item" : " items"));
        double sub = subtotal();
        double total = total(sub);
        subtotalVal.setText(String.format("$%.2f", sub));
        discountVal.setText(sub - total > 0.004 ? String.format("-$%.2f", sub - total) : "-");
        totalVal.setText(String.format("$%.2f", total));
        checkoutBtn.setEnabled(!cart.isEmpty());
        linesPanel.revalidate();
        linesPanel.repaint();
    }

    private void status(String msg) {
        statusLabel.setText(msg);
    }
}
