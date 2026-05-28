package org.example;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class Main {
    public static int WINDOW_WIDTH = 980;
    public static int WINDOW_HEIGHT = 760;

    private static int MIN_DINERS = 2;
    private static int START_DINERS = 5;
    private static int MAX_DINERS = START_DINERS + 2;

    public static void main(String[] args) {
        JFrame window = new JFrame("בעיית הפילוסופים הסועדים");

        DiningPanel diningPanel = new DiningPanel(START_DINERS);

        JTextField numberField = new JTextField(String.valueOf(START_DINERS));
        numberField.setHorizontalAlignment(JTextField.CENTER);
        numberField.setPreferredSize(new Dimension(70, 32));

        JButton updateButton = new JButton("עדכן");

        JTextField stopField = new JTextField("1,3");
        stopField.setHorizontalAlignment(JTextField.CENTER);
        stopField.setPreferredSize(new Dimension(100, 32));

        JButton stopButton = new JButton("הפסק");
        JButton resumeButton = new JButton("החזר");

        JLabel platesMessageLabel = new JLabel("טווח מותר: " + MIN_DINERS + " עד " + MAX_DINERS);
        JLabel philoMessageLabel = new JLabel(" ");

        platesMessageLabel.setForeground(new Color(120, 0, 0));
        philoMessageLabel.setForeground(new Color(120, 0, 0));

        Font font = new Font("Arial", Font.BOLD, 15);

        numberField.setFont(font);
        updateButton.setFont(font);
        stopField.setFont(font);
        stopButton.setFont(font);
        resumeButton.setFont(font);
        platesMessageLabel.setFont(font);
        philoMessageLabel.setFont(font);

        updateButton.addActionListener(e -> {
            int count = getValidDinersCount(numberField, platesMessageLabel);

            if (count == -1) {
                return;
            }

            diningPanel.setDinersCount(count);
            platesMessageLabel.setText("טווח מותר: " + MIN_DINERS + " עד " + MAX_DINERS);
            philoMessageLabel.setText(" ");
        });

        stopButton.addActionListener(e -> {
            String input = stopField.getText().replace(" ", "");

            if (!isValidPhilosophersInput(input, diningPanel, philoMessageLabel)) {
                return;
            }

            String[] parts = input.split(",");
            boolean allSucceeded = true;

            for (int i = 0; i < parts.length; i++) {
                int philosopherNumber = Integer.parseInt(parts[i]);

                if (!diningPanel.stopOnePhilosopher(philosopherNumber)) {
                    allSucceeded = false;
                }
            }

            if (allSucceeded) {
                philoMessageLabel.setText("הופסקו פילוסופים: " + input);
            } else {
                philoMessageLabel.setText("חלק מהפילוסופים לא הופסקו, ייתכן שכבר הופסקו");
            }
        });

        resumeButton.addActionListener(e -> {
            String input = stopField.getText().replace(" ", "");

            if (!isValidPhilosophersInput(input, diningPanel, philoMessageLabel)) {
                return;
            }

            String[] parts = input.split(",");
            boolean allSucceeded = true;

            for (int i = 0; i < parts.length; i++) {
                int philosopherNumber = Integer.parseInt(parts[i]);

                if (!diningPanel.resumeOnePhilosopher(philosopherNumber)) {
                    allSucceeded = false;
                }
            }

            if (allSucceeded) {
                philoMessageLabel.setText("הוחזרו פילוסופים: " + input);
            } else {
                philoMessageLabel.setText("חלק מהפילוסופים לא הוחזרו, ייתכן שכבר פעילים");
            }
        });

        JPanel platesControlsRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        platesControlsRow.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        platesControlsRow.setOpaque(false);
        platesControlsRow.add(new JLabel("מספר פילוסופים:"));
        platesControlsRow.add(numberField);
        platesControlsRow.add(updateButton);

        JPanel platesMessageRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        platesMessageRow.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        platesMessageRow.setOpaque(false);
        platesMessageRow.add(platesMessageLabel);

        JPanel platesPanel = new JPanel();
        platesPanel.setLayout(new BoxLayout(platesPanel, BoxLayout.Y_AXIS));
        platesPanel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        platesPanel.setBackground(Color.WHITE);
        platesPanel.setBorder(BorderFactory.createCompoundBorder(
                new TitledBorder("שליטה בכמות הפילוסופים"),
                new EmptyBorder(10, 10, 10, 10)
        ));

        platesPanel.add(platesControlsRow);
        platesPanel.add(Box.createVerticalStrut(8));
        platesPanel.add(platesMessageRow);

        JPanel philoControlsRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        philoControlsRow.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        philoControlsRow.setOpaque(false);
        philoControlsRow.add(new JLabel("פילוסופים להפסקה או החזרה:"));
        philoControlsRow.add(stopField);
        philoControlsRow.add(stopButton);
        philoControlsRow.add(resumeButton);

        JPanel philoMessageRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        philoMessageRow.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        philoMessageRow.setOpaque(false);
        philoMessageRow.add(philoMessageLabel);

        JPanel philosophersPanel = new JPanel();
        philosophersPanel.setLayout(new BoxLayout(philosophersPanel, BoxLayout.Y_AXIS));
        philosophersPanel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        philosophersPanel.setBackground(Color.WHITE);
        philosophersPanel.setBorder(BorderFactory.createCompoundBorder(
                new TitledBorder("שליטה בפילוסופים"),
                new EmptyBorder(10, 10, 10, 10)
        ));

        philosophersPanel.add(philoControlsRow);
        philosophersPanel.add(Box.createVerticalStrut(8));
        philosophersPanel.add(philoMessageRow);

        JPanel topPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        topPanel.setBorder(new EmptyBorder(12, 12, 12, 12));
        topPanel.setBackground(new Color(245, 245, 245));
        topPanel.add(platesPanel);
        topPanel.add(philosophersPanel);

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
        String input = numberField.getText().replace(" ", "");

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
            messageLabel.setText("מעל הטווח המותר (" + MIN_DINERS + " עד " + MAX_DINERS + ")");
            return -1;
        }

        if (count < MIN_DINERS) {
            messageLabel.setText("מתחת לטווח המותר (" + MIN_DINERS + " עד " + MAX_DINERS + ")");
            return -1;
        }

        return count;
    }

    private static boolean isValidPhilosophersInput(
            String input,
            DiningPanel diningPanel,
            JLabel messageLabel
    ) {
        if (input.isEmpty()) {
            messageLabel.setText("יש להקליד מספר פילוסוף או רשימה");
            return false;
        }

        String[] parts = input.split(",");
        boolean[] alreadyTyped = new boolean[diningPanel.getDinersCount() + 1];

        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];

            if (part.isEmpty()) {
                messageLabel.setText("יש להקליד מספר תקין בין פסיקים");
                return false;
            }

            if (!part.matches("\\d+")) {
                messageLabel.setText("יש להקליד מספרים בלבד");
                return false;
            }

            int philosopherNumber;

            try {
                philosopherNumber = Integer.parseInt(part);
            } catch (NumberFormatException ex) {
                messageLabel.setText("המספר גדול מדי");
                return false;
            }

            if (philosopherNumber < 1 || philosopherNumber > diningPanel.getDinersCount()) {
                messageLabel.setText("פילוסוף " + philosopherNumber + " לא קיים");
                return false;
            }

            if (alreadyTyped[philosopherNumber]) {
                messageLabel.setText("פילוסוף " + philosopherNumber + " הוקלד יותר מפעם אחת");
                return false;
            }

            alreadyTyped[philosopherNumber] = true;
        }

        return true;
    }
}