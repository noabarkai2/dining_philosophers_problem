package org.example;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.RoundRectangle2D;

public class Fork {
    private static final Color BORDER = new Color(90, 90, 90);

    public void draw(Graphics2D g, int centerX, int centerY, double angle, Color forkColor) {
        AffineTransform oldTransform = g.getTransform();

        g.translate(centerX, centerY);
        g.rotate(angle + Math.PI / 2);

        drawFork(g, forkColor);

        g.setTransform(oldTransform);
    }

    private void drawFork(Graphics2D g, Color forkColor) {
        g.setColor(forkColor);

        Shape handle = new RoundRectangle2D.Double(-3, 5, 6, 38, 6, 6);
        g.fill(handle);

        Shape neck = new RoundRectangle2D.Double(-6, -2, 12, 10, 6, 6);
        g.fill(neck);

        g.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawLine(-6, -22, -6, -2);
        g.drawLine(-2, -24, -2, -2);
        g.drawLine(2, -24, 2, -2);
        g.drawLine(6, -22, 6, -2);

        g.setColor(BORDER);
        g.setStroke(new BasicStroke(1f));

        g.draw(handle);
        g.draw(neck);

        g.setStroke(new BasicStroke(1.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawLine(-6, -22, -6, -2);
        g.drawLine(-2, -24, -2, -2);
        g.drawLine(2, -24, 2, -2);
        g.drawLine(6, -22, 6, -2);
    }
}