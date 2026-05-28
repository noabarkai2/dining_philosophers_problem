package org.example;

import java.awt.*;

public class DiningTable {
    private static int TABLE_SIZE = 450;
    private static int TABLE_CENTER_Y_OFFSET = 0;

    private static int PLATE_SIZE = 64;
    private static int PLATE_DISTANCE_FROM_TABLE_EDGE = 35;

    private static int FREE_FORK_DISTANCE_FROM_TABLE_EDGE = 115;
    private static int TAKEN_FORK_SHIFT_TOWARD_PLATE = 18;

    private static int PHILOSOPHER_HEAD_SIZE = 22;
    private static int PHILOSOPHER_BODY_WIDTH = 28;
    private static int PHILOSOPHER_BODY_HEIGHT = 34;
    private static int PHILOSOPHER_DISTANCE_FROM_TABLE = 22;

    private static int INNER_TABLE_PADDING = 35;
    private static int TABLE_BORDER_WIDTH = 4;

    private static int LEGEND_X = 20;
    private static int LEGEND_START_Y = 25;
    private static int LEGEND_GAP_Y = 25;

    private static int FREE_FORK_OWNER = -1;

    private static Color FREE_FORK_COLOR = new Color(180, 180, 180);
    private static Color THINKING_COLOR = new Color(80, 80, 80);
    private static Color WAITING_BOTH_COLOR = new Color(200, 90, 0);
    private static Color EATING_COLOR = new Color(0, 150, 70);
    private static Color STOPPED_COLOR = new Color(160, 40, 40);

    private static float GOLDEN_RATIO = 0.61803398875f;
    private static float PASTEL_SATURATION = 0.35f;
    private static float PASTEL_BRIGHTNESS = 0.95f;

    private int dinersCount;
    private DiningSimulation simulation;

    private Plate plate;
    private Fork fork;

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

        int plateDistanceFromCenter =
                tableRadius - PLATE_SIZE / 2 - PLATE_DISTANCE_FROM_TABLE_EDGE;

        for (int i = 0; i < dinersCount; i++) {
            double plateAngle = -Math.PI / 2 + i * angleStep;

            int plateX = centerX + (int) (Math.cos(plateAngle) * plateDistanceFromCenter);
            int plateY = centerY + (int) (Math.sin(plateAngle) * plateDistanceFromCenter);

            Color philosopherColor = createPhilosopherColor(i);
            PhilosopherState state = simulation.getPhilosopherState(i);
            int mealsCount = simulation.getMealsCount(i);

            drawPhilosopher(
                    g,
                    centerX,
                    centerY,
                    tableRadius,
                    plateAngle,
                    philosopherColor,
                    state,
                    i + 1
            );

            plate.draw(
                    g,
                    plateX,
                    plateY,
                    i + 1,
                    philosopherColor,
                    PLATE_SIZE,
                    state,
                    mealsCount
            );
        }

        for (int i = 0; i < dinersCount; i++) {
            drawForkForIndex(
                    g,
                    centerX,
                    centerY,
                    tableRadius,
                    angleStep,
                    i,
                    plateDistanceFromCenter
            );
        }

        drawLegend(g);
    }

    private void drawForkForIndex(
            Graphics2D g,
            int centerX,
            int centerY,
            int tableRadius,
            double angleStep,
            int forkIndex,
            int plateDistanceFromCenter
    ) {
        double freeForkAngle = -Math.PI / 2 + forkIndex * angleStep + angleStep / 2;

        int freeForkDistanceFromCenter = tableRadius - FREE_FORK_DISTANCE_FROM_TABLE_EDGE;

        int freeForkX = centerX + (int) (Math.cos(freeForkAngle) * freeForkDistanceFromCenter);
        int freeForkY = centerY + (int) (Math.sin(freeForkAngle) * freeForkDistanceFromCenter);

        int forkOwner = simulation.getForkOwner(forkIndex);
        Color forkColor = getForkColor(forkOwner);

        if (forkOwner == FREE_FORK_OWNER) {
            fork.draw(g, freeForkX, freeForkY, freeForkAngle, forkColor);
            return;
        }

        double ownerPlateAngle = -Math.PI / 2 + forkOwner * angleStep;

        int ownerPlateX = centerX + (int) (Math.cos(ownerPlateAngle) * plateDistanceFromCenter);
        int ownerPlateY = centerY + (int) (Math.sin(ownerPlateAngle) * plateDistanceFromCenter);

        int dx = ownerPlateX - freeForkX;
        int dy = ownerPlateY - freeForkY;

        double length = Math.sqrt(dx * dx + dy * dy);

        int takenForkX = freeForkX;
        int takenForkY = freeForkY;

        if (length != 0) {
            takenForkX += (int) (dx / length * TAKEN_FORK_SHIFT_TOWARD_PLATE);
            takenForkY += (int) (dy / length * TAKEN_FORK_SHIFT_TOWARD_PLATE);
        }

        fork.draw(g, takenForkX, takenForkY, freeForkAngle, forkColor);
    }

    private void drawPhilosopher(
            Graphics2D g,
            int tableCenterX,
            int tableCenterY,
            int tableRadius,
            double angle,
            Color philosopherColor,
            PhilosopherState state,
            int number
    ) {
        int distanceFromCenter = tableRadius + PHILOSOPHER_DISTANCE_FROM_TABLE;

        int centerX = tableCenterX + (int) (Math.cos(angle) * distanceFromCenter);
        int centerY = tableCenterY + (int) (Math.sin(angle) * distanceFromCenter);

        int headX = centerX - PHILOSOPHER_HEAD_SIZE / 2;
        int headY = centerY - PHILOSOPHER_BODY_HEIGHT / 2 - PHILOSOPHER_HEAD_SIZE / 2;

        int bodyX = centerX - PHILOSOPHER_BODY_WIDTH / 2;
        int bodyY = centerY - PHILOSOPHER_BODY_HEIGHT / 2 + 8;

        g.setColor(new Color(255, 230, 200));
        g.fillOval(headX, headY, PHILOSOPHER_HEAD_SIZE, PHILOSOPHER_HEAD_SIZE);

        g.setColor(Color.DARK_GRAY);
        g.drawOval(headX, headY, PHILOSOPHER_HEAD_SIZE, PHILOSOPHER_HEAD_SIZE);

        g.setColor(philosopherColor);
        g.fillRoundRect(
                bodyX,
                bodyY,
                PHILOSOPHER_BODY_WIDTH,
                PHILOSOPHER_BODY_HEIGHT,
                12,
                12
        );

        g.setColor(getStateColor(state));
        g.setStroke(new BasicStroke(3));
        g.drawRoundRect(
                bodyX,
                bodyY,
                PHILOSOPHER_BODY_WIDTH,
                PHILOSOPHER_BODY_HEIGHT,
                12,
                12
        );

        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 12));

        String text = String.valueOf(number);
        FontMetrics metrics = g.getFontMetrics();

        int textX = centerX - metrics.stringWidth(text) / 2;
        int textY = bodyY + 22;

        g.drawString(text, textX, textY);
    }

    private Color getForkColor(int forkOwner) {
        if (forkOwner == FREE_FORK_OWNER) {
            return FREE_FORK_COLOR;
        }

        return createPhilosopherColor(forkOwner);
    }

    private Color getStateColor(PhilosopherState state) {
        if (state == PhilosopherState.EATING) {
            return EATING_COLOR;
        }

        if (state == PhilosopherState.WAITING_FOR_BOTH_FORKS) {
            return WAITING_BOTH_COLOR;
        }

        if (state == PhilosopherState.STOPPED) {
            return STOPPED_COLOR;
        }

        return THINKING_COLOR;
    }

    private Color createPhilosopherColor(int index) {
        float hue = (index * GOLDEN_RATIO) % 1.0f;
        return Color.getHSBColor(hue, PASTEL_SATURATION, PASTEL_BRIGHTNESS);
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

        drawLegendItem(g, LEGEND_X, LEGEND_START_Y, THINKING_COLOR, "Thinking");
        drawLegendItem(g, LEGEND_X, LEGEND_START_Y + LEGEND_GAP_Y, WAITING_BOTH_COLOR, "Waiting both forks");
        drawLegendItem(g, LEGEND_X, LEGEND_START_Y + LEGEND_GAP_Y * 2, EATING_COLOR, "Eating");
        drawLegendItem(g, LEGEND_X, LEGEND_START_Y + LEGEND_GAP_Y * 3, FREE_FORK_COLOR, "Free fork");
        drawLegendItem(g, LEGEND_X, LEGEND_START_Y + LEGEND_GAP_Y * 4, STOPPED_COLOR, "Stopped");
    }

    private void drawLegendItem(Graphics2D g, int x, int y, Color color, String text) {
        g.setColor(color);
        g.fillOval(x, y - 12, 14, 14);

        g.setColor(Color.BLACK);
        g.drawString(text, x + 22, y);
    }
}