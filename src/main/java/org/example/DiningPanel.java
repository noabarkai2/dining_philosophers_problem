package org.example;

import javax.swing.*;
import java.awt.*;

public class DiningPanel extends JPanel {
    private DiningTable diningTable;
    private DiningSimulation simulation;

    public DiningPanel(int dinersCount) {
        setBackground(new Color(230, 230, 230));
        startNewSimulation(dinersCount);
    }

    public void setDinersCount(int dinersCount) {
        simulation.stop();
        startNewSimulation(dinersCount);
        repaint();
    }

    private void startNewSimulation(int dinersCount) {
        System.out.println("Starting new simulation with " + dinersCount + " philosophers");

        simulation = new DiningSimulation(dinersCount, this);
        diningTable = new DiningTable(dinersCount, simulation);
        simulation.start();
    }

    public void updateScreen() {
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D graphics2D = (Graphics2D) g;

        diningTable.draw(graphics2D, getWidth(), getHeight());
    }
}