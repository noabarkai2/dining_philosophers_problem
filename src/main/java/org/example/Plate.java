package org.example;

import java.awt.*;

public class Plate {

    public void draw(
            Graphics2D g,
            int centerX,
            int centerY,
            int number,
            Color plateColor,
            int size,
            PhilosopherState state,
            int mealsCount
    ) {
        int x = centerX - size / 2;
        int y = centerY - size / 2;

        g.setColor(plateColor);
        g.fillOval(x, y, size, size);

        g.setColor(getBorderColor(state));
        g.setStroke(new BasicStroke(4));
        g.drawOval(x, y, size, size);

        int innerGap = Math.max(5, size / 4);

        g.setColor(new Color(255, 255, 255, 120));
        g.fillOval(x + innerGap, y + innerGap, size - innerGap * 2, size - innerGap * 2);

        drawNumber(g, centerX, centerY, number, size);
        drawStatus(g, centerX, y + size + 13, state, mealsCount, size);
    }

    private void drawNumber(Graphics2D g, int centerX, int centerY, int number, int size) {
        int fontSize = Math.max(10, size / 3);

        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, fontSize));

        String text = String.valueOf(number);
        FontMetrics metrics = g.getFontMetrics();

        int textX = centerX - metrics.stringWidth(text) / 2;
        int textY = centerY + metrics.getAscent() / 2 - 3;

        g.drawString(text, textX, textY);
    }

    private void drawStatus(
            Graphics2D g,
            int centerX,
            int y,
            PhilosopherState state,
            int mealsCount,
            int size
    ) {
        String text = getStateText(state) + " | " + mealsCount;

        int fontSize = Math.max(8, Math.min(11, size / 3));

        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, fontSize));

        FontMetrics metrics = g.getFontMetrics(); //איפה למקם טקסט כדי שיהיה באמצע
        int textX = centerX - metrics.stringWidth(text) / 2;

        g.drawString(text, textX, y);
    }

    private String getStateText(PhilosopherState state) {
        if (state == PhilosopherState.EATING) {
            return "Eating";
        }

        if (state == PhilosopherState.HUNGRY) {
            return "Hungry";
        }

        return "Thinking";
    }

    private Color getBorderColor(PhilosopherState state) {
        if (state == PhilosopherState.EATING) {
            return new Color(0, 150, 70);
        }

        if (state == PhilosopherState.HUNGRY) {
            return new Color(230, 130, 0);
        }

        return new Color(80, 80, 80);
    }
}