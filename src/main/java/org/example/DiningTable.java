package org.example;

import java.awt.*;

public class DiningTable {
    private static final int TABLE_SIZE = 500;
    private static final int TABLE_CENTER_Y_OFFSET = 20;

    private static final int PLATE_DISTANCE_FROM_TABLE_EDGE = 35;
    private static final int FORK_DISTANCE_FROM_TABLE_EDGE = 130;

    private static final int LARGE_PLATE_SIZE = 70;
    private static final int MEDIUM_PLATE_SIZE = 50;
    private static final int SMALL_PLATE_SIZE = 35;

    private static final int INNER_TABLE_PADDING = 35;
    private static final int TABLE_BORDER_WIDTH = 4;

    private static final int LEGEND_X = 20;
    private static final int LEGEND_START_Y = 25;
    private static final int LEGEND_GAP_Y = 25;

    private static final Color FREE_FORK_COLOR = new Color(180, 180, 180);

    private static final float GOLDEN_RATIO = 0.61803398875f;
    private static final float PASTEL_SATURATION = 0.35f;
    private static final float PASTEL_BRIGHTNESS = 0.95f;

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
        int centerY = panelHeight / 2 + TABLE_CENTER_Y_OFFSET;

        int tableRadius = TABLE_SIZE / 2;
        int tableX = centerX - tableRadius;
        int tableY = centerY - tableRadius;

        drawTable(g, tableX, tableY, TABLE_SIZE);

        double angleStep = 2 * Math.PI / dinersCount;

        int plateSize = calculatePlateSize();

        int plateDistanceFromCenter =
                tableRadius - plateSize / 2 - PLATE_DISTANCE_FROM_TABLE_EDGE;

        int forkDistanceFromCenter =
                tableRadius - FORK_DISTANCE_FROM_TABLE_EDGE;

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

    private int calculatePlateSize() {
        if (dinersCount <= 8) {
            return LARGE_PLATE_SIZE;
        }

        if (dinersCount <= 14) {
            return MEDIUM_PLATE_SIZE;
        }

        return SMALL_PLATE_SIZE;
    }

    private Color getForkColor(int forkOwner) {
        if (forkOwner == -1) {
            return FREE_FORK_COLOR;
        }

        return createPhilosopherColor(forkOwner);
    }

    // כל פילוסוף מקבל מספר אחר, ומהמספר הזה אנחנו יוצרים לו צבע אחר.
    // הרוויה נמוכה והבהירות גבוהה, כדי שהצבעים יהיו נעימים כמו פסטל.
    private Color createPhilosopherColor(int index) {
        float hue = (index * GOLDEN_RATIO) % 1.0f;

        return Color.getHSBColor(
                hue,
                PASTEL_SATURATION,
                PASTEL_BRIGHTNESS
        );
    }

    private void drawTable(Graphics2D g, int x, int y, int size) {
        g.setColor(new Color(139, 90, 43));
        g.fillOval(x, y, size, size);

        g.setColor(new Color(80, 45, 20));
        g.setStroke(new BasicStroke(TABLE_BORDER_WIDTH));
        g.drawOval(x, y, size, size);

        g.setColor(new Color(160, 105, 55));
        g.fillOval(
                x + INNER_TABLE_PADDING,
                y + INNER_TABLE_PADDING,
                size - INNER_TABLE_PADDING * 2,
                size - INNER_TABLE_PADDING * 2
        );
    }

    private void drawLegend(Graphics2D g) {
        g.setFont(new Font("Arial", Font.BOLD, 14));

        drawLegendItem(g, LEGEND_X, LEGEND_START_Y, new Color(80, 80, 80), "Thinking");
        drawLegendItem(g, LEGEND_X, LEGEND_START_Y + LEGEND_GAP_Y, new Color(230, 130, 0), "Hungry");
        drawLegendItem(g, LEGEND_X, LEGEND_START_Y + LEGEND_GAP_Y * 2, new Color(0, 150, 70), "Eating");
        drawLegendItem(g, LEGEND_X, LEGEND_START_Y + LEGEND_GAP_Y * 3, FREE_FORK_COLOR, "Free fork");
    }

    private void drawLegendItem(Graphics2D g, int x, int y, Color color, String text) {
        g.setColor(color);
        g.fillOval(x, y - 12, 14, 14);

        g.setColor(Color.BLACK);
        g.drawString(text, x + 22, y);
    }
}