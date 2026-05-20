package org.example;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class Fork {
    private static final Color BORDER_COLOR = new Color(90, 90, 90);

    private static final int HANDLE_WIDTH = 6;
    private static final int HANDLE_HEIGHT = 38;
    private static final int HANDLE_X = -3;
    private static final int HANDLE_Y = 5;
    private static final int HANDLE_ARC = 6;

    private static final int NECK_WIDTH = 12;
    private static final int NECK_HEIGHT = 10;
    private static final int NECK_X = -6;
    private static final int NECK_Y = -2;
    private static final int NECK_ARC = 6;

    private static final int LEFT_OUTER_TINE_X = -6;
    private static final int LEFT_INNER_TINE_X = -2;
    private static final int RIGHT_INNER_TINE_X = 2;
    private static final int RIGHT_OUTER_TINE_X = 6;

    private static final int OUTER_TINE_TOP_Y = -22;
    private static final int INNER_TINE_TOP_Y = -24;
    private static final int TINE_BOTTOM_Y = -2;

    private static final float SHAPE_BORDER_WIDTH = 1f;
    private static final float TINE_BORDER_WIDTH = 2.2f;
    private static final float TINE_COLOR_WIDTH = 1.2f;

    public void draw(Graphics2D g, int centerX, int centerY, double angle, Color forkColor) {
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        g2.translate(centerX, centerY);
        g2.rotate(angle + Math.PI / 2);

        drawFork(g2, forkColor);

        g2.dispose();
    }

    private void drawFork(Graphics2D g, Color forkColor) {
        Shape handle = new RoundRectangle2D.Double(
                HANDLE_X,
                HANDLE_Y,
                HANDLE_WIDTH,
                HANDLE_HEIGHT,
                HANDLE_ARC,
                HANDLE_ARC
        );

        Shape neck = new RoundRectangle2D.Double(
                NECK_X,
                NECK_Y,
                NECK_WIDTH,
                NECK_HEIGHT,
                NECK_ARC,
                NECK_ARC
        );

        g.setColor(forkColor);
        g.fill(handle);
        g.fill(neck);

        g.setColor(BORDER_COLOR);
        g.setStroke(new BasicStroke(SHAPE_BORDER_WIDTH));
        g.draw(handle);
        g.draw(neck);

        drawTinesBorder(g);
        drawTinesColor(g, forkColor);
    }

    private void drawTinesBorder(Graphics2D g) {
        g.setColor(BORDER_COLOR);
        g.setStroke(new BasicStroke(
                TINE_BORDER_WIDTH,
                BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND
        ));

        drawTines(g);
    }

    private void drawTinesColor(Graphics2D g, Color forkColor) {
        g.setColor(forkColor);
        g.setStroke(new BasicStroke(
                TINE_COLOR_WIDTH,
                BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND
        ));

        drawTines(g);
    }

    private void drawTines(Graphics2D g) {
        g.drawLine(LEFT_OUTER_TINE_X, OUTER_TINE_TOP_Y, LEFT_OUTER_TINE_X, TINE_BOTTOM_Y);
        g.drawLine(LEFT_INNER_TINE_X, INNER_TINE_TOP_Y, LEFT_INNER_TINE_X, TINE_BOTTOM_Y);
        g.drawLine(RIGHT_INNER_TINE_X, INNER_TINE_TOP_Y, RIGHT_INNER_TINE_X, TINE_BOTTOM_Y);
        g.drawLine(RIGHT_OUTER_TINE_X, OUTER_TINE_TOP_Y, RIGHT_OUTER_TINE_X, TINE_BOTTOM_Y);
    }
}