import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class QueueGUI {
    private JFrame frame;
    private JPanel cardPanel;
    private CardLayout cardLayout;
    private JTextField nameField, sizeField, freeTableField;
    private JTextArea outputArea, tablesTextArea, queueTextArea;
    private QueueManager manager;

    public QueueGUI() {
        manager = QueueManager.getInstance(5); 
        frame = new JFrame("Restaurant Queue System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 600);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        cardPanel.add(createMainMenuPanel(), "MainMenu");
        cardPanel.add(createTablesPanel(), "Tables");
        cardPanel.add(createQueuePanel(), "Queue");

        frame.getContentPane().add(cardPanel);
        frame.setVisible(true);
    }

    private JPanel createMainMenuPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 1));

        nameField = new JTextField();
        sizeField = new JTextField();
        freeTableField = new JTextField();

        JButton addButton = new JButton("Add Customer");
        addButton.addActionListener(this::addCustomer);

        JButton seatButton = new JButton("Seat Next Customer");
        seatButton.addActionListener(this::seatNextCustomer);

        JButton freeButton = new JButton("Free Table");
        freeButton.addActionListener(this::freeTable);

        JButton showTables = new JButton("Show Tables");
        showTables.addActionListener(e -> {
            tablesTextArea.setText(manager.getTablesInfo());
            cardLayout.show(cardPanel, "Tables");
        });

        JButton showQueue = new JButton("Show Queue");
        showQueue.addActionListener(e -> {
            queueTextArea.setText(manager.getQueueInfo());
            cardLayout.show(cardPanel, "Queue");
        });

        JButton backBtn = new JButton("Back to Main Menu");
        backBtn.addActionListener(e -> {
            frame.dispose();
            new MainApp();
        });

        outputArea = new JTextArea();
        outputArea.setEditable(false);

        panel.add(new JLabel("Customer Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Party Size:"));
        panel.add(sizeField);
        panel.add(addButton);
        panel.add(seatButton);
        panel.add(new JLabel("Table to Free:"));
        panel.add(freeTableField);
        panel.add(freeButton);
        panel.add(showTables);
        panel.add(showQueue);
        panel.add(backBtn);
        panel.add(new JScrollPane(outputArea));

        return panel;
    }

    private JPanel createTablesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        tablesTextArea = new JTextArea();
        tablesTextArea.setEditable(false);
        panel.add(new JScrollPane(tablesTextArea), BorderLayout.CENTER);

        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> cardLayout.show(cardPanel, "MainMenu"));
        panel.add(backButton, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createQueuePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        queueTextArea = new JTextArea();
        queueTextArea.setEditable(false);
        panel.add(new JScrollPane(queueTextArea), BorderLayout.CENTER);

        return panel;
    }

    private void addCustomer(ActionEvent e) {
        String name = nameField.getText();
        int size;
        try {
            size = Integer.parseInt(sizeField.getText());
        } catch (NumberFormatException ex) {
            outputArea.setText("Invalid party size.");
            return;
        }
        manager.addCustomer(new Customer(name, size));
        updateOutput();
        JOptionPane.showMessageDialog(frame, "Customer \"" + name + "\" added to the queue.");
    }

    private void seatNextCustomer(ActionEvent e) {
        manager.seatNextCustomer();
        updateOutput();
        tablesTextArea.setText(manager.getTablesInfo());
        JOptionPane.showMessageDialog(frame, "Next customer has been seated.");
    }

    private void freeTable(ActionEvent e) {
        int tableNum;
        try {
            tableNum = Integer.parseInt(freeTableField.getText());
        } catch (NumberFormatException ex) {
            outputArea.setText("Invalid table number.");
            return;
        }
        manager.freeTable(tableNum);
        updateOutput();
        JOptionPane.showMessageDialog(frame, "Table " + tableNum + " is now free.");
    }

    private void updateOutput() {
        outputArea.setText(manager.getTablesInfo() + "\n" + manager.getQueueInfo());
    }

}
