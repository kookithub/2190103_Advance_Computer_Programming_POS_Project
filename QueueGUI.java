import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Queue screen: add parties to the waiting list, seat the next one, free tables.
 * Tables are cards (green chip = available, red chip = occupied) with their own "Free table" button.
 */
public class QueueGUI {
    private static final int TABLES = 5;

    private JFrame frame;
    private JTextField nameField;
    private Theme.Stepper sizeStepper;
    private JPanel tablesPanel, waitingPanel;
    private JLabel statusLabel, waitingCount, freeCount;
    private QueueManager manager;

    public QueueGUI() {
        manager = QueueManager.getInstance(TABLES);
        frame = new JFrame("Restaurant Queue System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1120, 700);
        frame.setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(0, 18));
        root.setBackground(Theme.BG);
        root.setBorder(BorderFactory.createEmptyBorder(20, 24, 14, 24));
        root.add(buildHeader(), BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(18, 0));
        center.setOpaque(false);
        center.add(buildForm(), BorderLayout.WEST);
        center.add(buildTables(), BorderLayout.CENTER);
        center.add(buildWaiting(), BorderLayout.EAST);
        root.add(center, BorderLayout.CENTER);

        statusLabel = Theme.label(" ", Font.PLAIN, 13f, Theme.MUTED);
        root.add(statusLabel, BorderLayout.SOUTH);
        frame.setContentPane(root);

        refresh();
        frame.setVisible(true);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        JLabel title = Theme.label("Queue & Tables", Font.BOLD, 28f, Theme.TEXT);
        JLabel sub = Theme.label("Seat walk-in parties in arrival order", Font.PLAIN, 14f, Theme.MUTED);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);
        left.add(title);
        left.add(sub);
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);
        Theme.Btn back = new Theme.Btn("← Menu", Theme.Kind.GHOST);
        back.addActionListener(e -> {
            frame.dispose();
            new MainApp();
        });
        right.add(back);
        header.add(left, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);
        return header;
    }

    private JPanel buildForm() {
        Theme.Card card = new Theme.Card(new BorderLayout(), 20, 20);
        card.setPreferredSize(new Dimension(270, 0));
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        p.add(left(Theme.label("New party", Font.BOLD, 18f, Theme.TEXT)));
        p.add(Box.createVerticalStrut(16));
        p.add(left(Theme.label("Customer name", Font.PLAIN, 12f, Theme.MUTED)));
        p.add(Box.createVerticalStrut(4));
        nameField = Theme.field(12);
        nameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        nameField.addActionListener(e -> addCustomer());
        p.add(left(nameField));
        p.add(Box.createVerticalStrut(14));
        p.add(left(Theme.label("Party size", Font.PLAIN, 12f, Theme.MUTED)));
        p.add(Box.createVerticalStrut(4));
        sizeStepper = new Theme.Stepper(2, 1, 20);
        JPanel stepRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        stepRow.setOpaque(false);
        stepRow.add(sizeStepper);
        p.add(left(stepRow));
        p.add(Box.createVerticalStrut(18));
        Theme.Btn add = new Theme.Btn("Add to queue", Theme.Kind.PRIMARY);
        add.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        add.addActionListener(e -> addCustomer());
        p.add(left(add));
        p.add(Box.createVerticalStrut(22));
        p.add(left(Theme.divider()));
        p.add(Box.createVerticalStrut(22));
        Theme.Btn seat = new Theme.Btn("Seat next customer", Theme.Kind.SECONDARY);
        seat.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        seat.addActionListener(e -> {
            int before = manager.getWaiting().size();
            manager.seatNextCustomer();
            refresh();
            status(manager.getWaiting().size() < before ? "Seated the next party"
                    : before == 0 ? "Nobody is waiting" : "No free table right now");
        });
        p.add(left(seat));
        card.add(p, BorderLayout.NORTH);
        return card;
    }

    private JComponent left(JComponent c) {
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        return c;
    }

    private JPanel buildTables() {
        JPanel wrap = new JPanel(new BorderLayout(0, 10));
        wrap.setOpaque(false);
        JPanel head = new JPanel(new BorderLayout());
        head.setOpaque(false);
        head.add(Theme.label("Tables", Font.BOLD, 18f, Theme.TEXT), BorderLayout.WEST);
        freeCount = Theme.label("", Font.PLAIN, 13f, Theme.MUTED);
        head.add(freeCount, BorderLayout.EAST);
        tablesPanel = new JPanel(new GridLayout(0, 2, 16, 16));
        tablesPanel.setOpaque(false);
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        top.add(tablesPanel, BorderLayout.NORTH);
        wrap.add(head, BorderLayout.NORTH);
        wrap.add(top, BorderLayout.CENTER);
        return wrap;
    }

    private JPanel buildWaiting() {
        Theme.Card card = new Theme.Card(new BorderLayout(0, 12), 20, 20);
        card.setPreferredSize(new Dimension(290, 0));
        JPanel head = new JPanel(new BorderLayout());
        head.setOpaque(false);
        head.add(Theme.label("Waiting list", Font.BOLD, 18f, Theme.TEXT), BorderLayout.WEST);
        waitingCount = Theme.label("", Font.PLAIN, 13f, Theme.MUTED);
        head.add(waitingCount, BorderLayout.EAST);
        waitingPanel = new JPanel();
        waitingPanel.setOpaque(false);
        waitingPanel.setLayout(new BoxLayout(waitingPanel, BoxLayout.Y_AXIS));
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.add(waitingPanel, BorderLayout.NORTH);
        JScrollPane sp = new JScrollPane(wrap);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        card.add(head, BorderLayout.NORTH);
        card.add(sp, BorderLayout.CENTER);
        return card;
    }

    private void addCustomer() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            status("Enter a customer name");
            return;
        }
        manager.addCustomer(new Customer(name, sizeStepper.getValue()));
        nameField.setText("");
        refresh();
        status("\"" + name + "\" added to the queue");
        nameField.requestFocusInWindow();
    }

    private JPanel tableCard(Table t) {
        boolean free = t.isAvailable();
        Theme.Card card = new Theme.Card(new BorderLayout(0, 10), 18, 16);
        card.setPreferredSize(new Dimension(180, 150));
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(Theme.label("Table " + t.getTableNumber(), Font.BOLD, 17f, Theme.TEXT), BorderLayout.WEST);
        JPanel chipWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        chipWrap.setOpaque(false);
        chipWrap.add(free ? new Theme.Chip("Available", Theme.OK_SOFT, Theme.OK)
                : new Theme.Chip("Occupied", Theme.BAD_SOFT, Theme.BAD));
        top.add(chipWrap, BorderLayout.EAST);
        card.add(top, BorderLayout.NORTH);

        JPanel mid = new JPanel(new GridLayout(2, 1));
        mid.setOpaque(false);
        if (free) {
            mid.add(Theme.label("Ready for guests", Font.PLAIN, 14f, Theme.MUTED));
            mid.add(Theme.label(" ", Font.PLAIN, 12f, Theme.MUTED));
        } else {
            mid.add(Theme.label(t.getCustomer().getName(), Font.BOLD, 16f, Theme.TEXT));
            mid.add(Theme.label("Party of " + t.getCustomer().getPartySize(), Font.PLAIN, 13f, Theme.MUTED));
        }
        card.add(mid, BorderLayout.CENTER);

        if (!free) {
            Theme.Btn free1 = new Theme.Btn("Free table", Theme.Kind.SECONDARY);
            free1.setName("free-" + t.getTableNumber());
            free1.addActionListener(e -> {
                manager.freeTable(t.getTableNumber());
                refresh();
                status("Table " + t.getTableNumber() + " is now available");
            });
            card.add(free1, BorderLayout.SOUTH);
        }
        return card;
    }

    private void refresh() {
        tablesPanel.removeAll();
        int freeTables = 0;
        for (Table t : manager.getTables()) {
            if (t.isAvailable()) freeTables++;
            tablesPanel.add(tableCard(t));
        }
        freeCount.setText(freeTables + " of " + TABLES + " free");
        tablesPanel.revalidate();
        tablesPanel.repaint();

        waitingPanel.removeAll();
        List<Customer> waiting = manager.getWaiting();
        if (waiting.isEmpty()) {
            JLabel l = Theme.label("Nobody is waiting", Font.PLAIN, 14f, Theme.MUTED);
            l.setBorder(BorderFactory.createEmptyBorder(24, 0, 0, 0));
            l.setAlignmentX(Component.LEFT_ALIGNMENT);
            waitingPanel.add(l);
        }
        int i = 1;
        for (Customer c : waiting) {
            JPanel row = new JPanel(new BorderLayout(10, 0));
            row.setOpaque(false);
            row.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
            row.setAlignmentX(Component.LEFT_ALIGNMENT);
            row.add(new Theme.Chip("#" + i++, Theme.PRIMARY_SOFT, Theme.PRIMARY) , BorderLayout.WEST);
            row.add(Theme.label(c.getName(), Font.BOLD, 14f, Theme.TEXT), BorderLayout.CENTER);
            row.add(new Theme.Chip(c.getPartySize() + " ppl", Theme.BORDER, Theme.TEXT), BorderLayout.EAST);
            waitingPanel.add(row);
            waitingPanel.add(Theme.divider());
        }
        waitingCount.setText(waiting.size() + " waiting");
        waitingPanel.revalidate();
        waitingPanel.repaint();
    }

    private void status(String msg) {
        statusLabel.setText(msg);
    }
}
