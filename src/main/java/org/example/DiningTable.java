package org.example;

import java.awt.*;

public class DiningTable {
    private final int dinersCount;
    private final DiningSimulation simulation;

    private final Plate plate;
    private final Fork fork;

    public DiningTable(int dinersCount, DiningSimulation simulation) {
        this.dinersCount = dinersCount;
        this.simulation = simulation;
        this.plate = new Plate();
        this.fork = new Fork();
    }

    public void draw(Graphics2D g, int panelWidth, int panelHeight) {
        int centerX = panelWidth / 2;
        int centerY = panelHeight / 2 + 20;

        int tableSize = 500;
        int tableRadius = tableSize / 2;

        int tableX = centerX - tableRadius;
        int tableY = centerY - tableRadius;

        drawTable(g, tableX, tableY, tableSize);

        double angleStep = 2 * Math.PI / dinersCount;

        int plateSize = calculatePlateSize(tableRadius);
        int plateDistanceFromCenter = tableRadius - plateSize / 2 - 35;
        int forkDistanceFromCenter = tableRadius - 95;

        for (int i = 0; i < dinersCount; i++) {
            double plateAngle = -Math.PI / 2 + i * angleStep;

            int plateX = centerX + (int) (Math.cos(plateAngle) * plateDistanceFromCenter);
            int plateY = centerY + (int) (Math.sin(plateAngle) * plateDistanceFromCenter);

            Color philosopherColor = createPhilosopherColor(i);
            PhilosopherState state = simulation.getPhilosopherState(i);
            int mealsCount = simulation.getMealsCount(i);

            plate.draw(g, plateX, plateY, i + 1, philosopherColor, plateSize, state, mealsCount);

            double forkAngle = plateAngle + angleStep / 2;

            int forkX = centerX + (int) (Math.cos(forkAngle) * forkDistanceFromCenter);
            int forkY = centerY + (int) (Math.sin(forkAngle) * forkDistanceFromCenter);

            int forkOwner = simulation.getForkOwner(i);
            Color forkColor = getForkColor(forkOwner);

            fork.draw(g, forkX, forkY, forkAngle, forkColor);
        }

        drawLegend(g);
    }

    private Color getForkColor(int forkOwner) {
        if (forkOwner == -1) {
            return new Color(180, 180, 180);
        }

        return createPhilosopherColor(forkOwner);
    }

    private int calculatePlateSize(int tableRadius) {
        int distanceFromCenter = tableRadius - 55;

        double circleLength = 2 * Math.PI * distanceFromCenter;
        int sizeBySpace = (int) (circleLength / dinersCount * 0.75);

        int maxSize = 70;
        int minSize = 28;

        if (sizeBySpace > maxSize) {
            return maxSize;
        }

        if (sizeBySpace < minSize) {
            return minSize;
        }

        return sizeBySpace;
    }

    private Color createPhilosopherColor(int index) {
        float goldenRatio = 0.61803398875f;

        float hue = (index * goldenRatio) % 1.0f;
        float saturation = 0.55f;
        float brightness = 1.0f;

        return Color.getHSBColor(hue, saturation, brightness);
    }

    private void drawTable(Graphics2D g, int x, int y, int size) {
        g.setColor(new Color(139, 90, 43));
        g.fillOval(x, y, size, size);

        g.setColor(new Color(80, 45, 20));
        g.setStroke(new BasicStroke(4));
        g.drawOval(x, y, size, size);

        g.setColor(new Color(160, 105, 55));
        g.fillOval(x + 35, y + 35, size - 70, size - 70);
    }

    private void drawLegend(Graphics2D g) {
        g.setFont(new Font("Arial", Font.BOLD, 14));

        drawLegendItem(g, 20, 25, new Color(80, 80, 80), "Thinking");
        drawLegendItem(g, 20, 50, new Color(230, 130, 0), "Hungry");
        drawLegendItem(g, 20, 75, new Color(0, 150, 70), "Eating");
        drawLegendItem(g, 20, 100, new Color(180, 180, 180), "Free fork");
    }

    private void drawLegendItem(Graphics2D g, int x, int y, Color color, String text) {
        g.setColor(color);
        g.fillOval(x, y - 12, 14, 14);

        g.setColor(Color.BLACK);
        g.drawString(text, x + 22, y);
    }
}