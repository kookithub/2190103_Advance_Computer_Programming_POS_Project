import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class MainApp {
    private JFrame launcherFrame;

    public MainApp() {
        launcherFrame = new JFrame("Main Menu");
        launcherFrame.setSize(820, 480);
        launcherFrame.setLocationRelativeTo(null);
        launcherFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        launcherFrame.getContentPane().setBackground(Theme.BG);

        JPanel root = new JPanel();
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setBackground(Theme.BG);
        root.setBorder(BorderFactory.createEmptyBorder(40, 48, 36, 48));

        JLabel title = Theme.label("Restaurant POS", Font.BOLD, 34f, Theme.TEXT);
        JLabel sub = Theme.label("Take orders at the counter and manage the waiting list.", Font.PLAIN, 15f, Theme.MUTED);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);
        root.add(title);
        root.add(Box.createVerticalStrut(6));
        root.add(sub);
        root.add(Box.createVerticalStrut(30));

        JPanel cards = new JPanel(new GridLayout(1, 2, 24, 0));
        cards.setOpaque(false);
        cards.setAlignmentX(Component.LEFT_ALIGNMENT);
        cards.add(moduleCard("Cashier", "Tap products to build an order, apply a discount and print the receipt.",
                "Open cashier", this::billing));
        cards.add(moduleCard("Queue & Tables", "Add walk-in parties to the waiting list and seat them at free tables.",
                "Open queue", this::openQueueSystem));
        root.add(cards);

        launcherFrame.add(root);
        launcherFrame.setVisible(true);
    }

    private JPanel moduleCard(String name, String text, String button, java.awt.event.ActionListener action) {
        Theme.Card card = new Theme.Card(new BorderLayout(0, 14), 20, 24);
        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        JLabel badge = new JLabel(name.substring(0, 1), SwingConstants.CENTER) {
            @Override protected void paintComponent(Graphics g0) {
                Graphics2D g = (Graphics2D) g0.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setColor(Theme.PRIMARY_SOFT);
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g.dispose();
                super.paintComponent(g0);
            }
        };
        badge.setFont(Theme.font(Font.BOLD, 20f));
        badge.setForeground(Theme.PRIMARY);
        badge.setPreferredSize(new Dimension(48, 48));
        badge.setMaximumSize(new Dimension(48, 48));
        badge.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel n = Theme.label(name, Font.BOLD, 20f, Theme.TEXT);
        n.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel t = new JLabel("<html><body style='width:230px'>" + text + "</body></html>");
        t.setFont(Theme.font(Font.PLAIN, 13f));
        t.setForeground(Theme.MUTED);
        t.setAlignmentX(Component.LEFT_ALIGNMENT);
        top.add(badge);
        top.add(Box.createVerticalStrut(16));
        top.add(n);
        top.add(Box.createVerticalStrut(6));
        top.add(t);
        Theme.Btn open = new Theme.Btn(button, Theme.Kind.PRIMARY);
        open.addActionListener(action);
        JPanel south = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        south.setOpaque(false);
        south.add(open);
        card.add(top, BorderLayout.CENTER);
        card.add(south, BorderLayout.SOUTH);
        return card;
    }

    private void openQueueSystem(ActionEvent e) {
        launcherFrame.dispose();
        new QueueGUI();
    }

    private void billing(ActionEvent e) {
        launcherFrame.dispose();
        new RestaurantGUI();
    }

    public static void main(String[] args) {
        Theme.install();
        SwingUtilities.invokeLater(MainApp::new);
    }
}
