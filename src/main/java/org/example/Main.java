package org.example;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static final int WINDOW_WIDTH = 900;
    public static final int WINDOW_HEIGHT = 700;

    public static void main(String[] args) {
        JFrame window = new JFrame("Dining Philosophers Problem");

        DiningPanel diningPanel = new DiningPanel(5);

        JTextField numberField = new JTextField("5", 4);
        JButton updateButton = new JButton("Update");

        JLabel messageLabel = new JLabel("טווח מותר: 2 עד 30");
        messageLabel.setForeground(new Color(120, 0, 0));

        updateButton.addActionListener(e -> {
            String input = numberField.getText();

            if (input.isEmpty()) {
                messageLabel.setText("יש להקליד מספר");
                return;
            }

            if (!input.matches("\\d+")) {
                messageLabel.setText("יש להקליד מספרים בלבד");
                return;
            }

            int count;

            try {
                count = Integer.parseInt(input);
            } catch (NumberFormatException ex) {
                messageLabel.setText("המספר גדול מדי");
                return;
            }

            if (count > 30) {
                messageLabel.setText("הקלדת מעל הטווח, הטווח המותר הוא 2 עד 30");
                return;
            }

            if (count < 2) {
                messageLabel.setText("הקלדת מתחת לטווח, הטווח המותר הוא 2 עד 30");
                return;
            }

            diningPanel.setDinersCount(count);
            messageLabel.setText("טווח מותר: 2 עד 30");
        });

        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Number of plates:"));
        topPanel.add(numberField);
        topPanel.add(updateButton);
        topPanel.add(messageLabel);

        window.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        window.setResizable(false);
        window.setLocationRelativeTo(null);
        window.setLayout(new BorderLayout());
        window.add(topPanel, BorderLayout.NORTH);
        window.add(diningPanel, BorderLayout.CENTER);
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.setVisible(true);
    }
}