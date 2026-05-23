package org.example;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static final int WINDOW_WIDTH = 900;
    public static final int WINDOW_HEIGHT = 700;

    private static final int MIN_DINERS = 2;
    private static final int MAX_DINERS = 15;

    public static void main(String[] args) {
        JFrame window = new JFrame("בעיית הפילוסופים הסועדים");

        DiningPanel diningPanel = new DiningPanel(5);

        JTextField numberField = new JTextField("5", 4);
        numberField.setHorizontalAlignment(JTextField.CENTER);

        JButton updateButton = new JButton("עדכן");

        JTextField stopField = new JTextField("1,3", 6);
        stopField.setHorizontalAlignment(JTextField.CENTER);

        JButton stopButton = new JButton("הפסק פילוסופים");
        JButton restartButton = new JButton("התחל מהתחלה");

        JLabel titleLabel = new JLabel("מספר צלחות:");
        JLabel stopLabel = new JLabel("פילוסופים להפסקה:");
        JLabel messageLabel = new JLabel("טווח מותר: " + MIN_DINERS + " עד " + MAX_DINERS);

        messageLabel.setForeground(new Color(120, 0, 0));

        Font font = new Font("Arial", Font.BOLD, 15);

        titleLabel.setFont(font);
        numberField.setFont(font);
        updateButton.setFont(font);
        stopLabel.setFont(font);
        stopField.setFont(font);
        stopButton.setFont(font);
        restartButton.setFont(font);
        messageLabel.setFont(font);

        updateButton.addActionListener(e -> {
            int count = getValidDinersCount(numberField, messageLabel);

            if (count == -1) {
                return;
            }

            diningPanel.setDinersCount(count);
            messageLabel.setText("טווח מותר: " + MIN_DINERS + " עד " + MAX_DINERS);
        });

        restartButton.addActionListener(e -> {
            int count = getValidDinersCount(numberField, messageLabel);

            if (count == -1) {
                return;
            }

            diningPanel.setDinersCount(count);
            messageLabel.setText("הסימולציה התחילה מחדש עם " + count + " פילוסופים");
        });

        stopButton.addActionListener(e -> {
            String input = stopField.getText().replace(" ", "");

            if (input.isEmpty()) {
                messageLabel.setText("יש להקליד מספר פילוסוף");
                return;
            }

            String[] parts = input.split(",");

            for (int i = 0; i < parts.length; i++) {
                if (!parts[i].matches("\\d+")) {
                    messageLabel.setText("יש להקליד מספרים מופרדים בפסיקים, למשל 1,3,5");
                    return;
                }

                int philosopherNumber = Integer.parseInt(parts[i]);

                if (philosopherNumber < 1 || philosopherNumber > diningPanel.getDinersCount()) {
                    messageLabel.setText("מספר פילוסוף חייב להיות בין 1 ל-" + diningPanel.getDinersCount());
                    return;
                }
            }

            for (int i = 0; i < parts.length; i++) {
                int philosopherNumber = Integer.parseInt(parts[i]);
                diningPanel.stopOnePhilosopher(philosopherNumber);
            }

            messageLabel.setText("הופסקו פילוסופים: " + input);
        });

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        topPanel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        topPanel.add(titleLabel);
        topPanel.add(numberField);
        topPanel.add(updateButton);
        topPanel.add(stopLabel);
        topPanel.add(stopField);
        topPanel.add(stopButton);
        topPanel.add(restartButton);
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

    private static int getValidDinersCount(JTextField numberField, JLabel messageLabel) {
        String input = numberField.getText();

        if (input.isEmpty()) {
            messageLabel.setText("יש להקליד מספר");
            return -1;
        }

        if (!input.matches("\\d+")) {
            messageLabel.setText("יש להקליד מספרים בלבד");
            return -1;
        }

        int count;

        try {
            count = Integer.parseInt(input);
        } catch (NumberFormatException ex) {
            messageLabel.setText("המספר גדול מדי");
            return -1;
        }

        if (count > MAX_DINERS) {
            messageLabel.setText("הקלדת מעל הטווח, הטווח המותר הוא " + MIN_DINERS + " עד " + MAX_DINERS);
            return -1;
        }

        if (count < MIN_DINERS) {
            messageLabel.setText("הקלדת מתחת לטווח, הטווח המותר הוא " + MIN_DINERS + " עד " + MAX_DINERS);
            return -1;
        }

        return count;
    }
}