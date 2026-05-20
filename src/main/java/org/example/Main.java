package org.example;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static final int WINDOW_WIDTH = 900;
    public static final int WINDOW_HEIGHT = 700;

    private static final int MIN_DINERS = 2;
    private static final int MAX_DINERS = 20;

    public static void main(String[] args) {
        JFrame window = new JFrame("בעיית הפילוסופים הסועדים");

        DiningPanel diningPanel = new DiningPanel(5);

        JTextField numberField = new JTextField("5", 4);
        numberField.setHorizontalAlignment(JTextField.CENTER);

        JButton updateButton = new JButton("עדכן");

        JLabel titleLabel = new JLabel("מספר צלחות:");
        JLabel messageLabel = new JLabel("טווח מותר: " + MIN_DINERS + " עד " + MAX_DINERS);

        messageLabel.setForeground(new Color(120, 0, 0));

        Font font = new Font("Arial", Font.BOLD, 15);

        titleLabel.setFont(font);
        numberField.setFont(font);
        updateButton.setFont(font);
        messageLabel.setFont(font);

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

            if (count > MAX_DINERS) {
                messageLabel.setText("הקלדת מעל הטווח, הטווח המותר הוא " + MIN_DINERS + " עד " + MAX_DINERS);
                return;
            }

            if (count < MIN_DINERS) {
                messageLabel.setText("הקלדת מתחת לטווח, הטווח המותר הוא " + MIN_DINERS + " עד " + MAX_DINERS);
                return;
            }

            diningPanel.setDinersCount(count);
            messageLabel.setText("טווח מותר: " + MIN_DINERS + " עד " + MAX_DINERS);
        });

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        topPanel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        topPanel.add(titleLabel);
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