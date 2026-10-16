import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class MainApp {
    private JFrame launcherFrame;

    public MainApp() {
        launcherFrame = new JFrame("Main Menu");
        launcherFrame.setSize(400, 300);
        launcherFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        launcherFrame.setLayout(new BorderLayout());

        JButton billing = new JButton("Restaurant Management");
        billing.addActionListener(this::billing);

        JButton openQueueSystem = new JButton("Queue System");
        openQueueSystem.addActionListener(this::openQueueSystem);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS)); // Arrange buttons vertically
        buttonPanel.add(billing);
        buttonPanel.add(openQueueSystem);

        JLabel title = new JLabel("Welcome to the Restaurant App", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));

        launcherFrame.add(title, BorderLayout.NORTH);
        launcherFrame.add(buttonPanel, BorderLayout.CENTER); // Add the panel with buttons to center
        launcherFrame.setVisible(true);
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
        SwingUtilities.invokeLater(MainApp::new);
    }
}
