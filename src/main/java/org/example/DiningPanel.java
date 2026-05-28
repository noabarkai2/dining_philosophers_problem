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
        if (simulation != null) {
            simulation.stop();
        }

        startNewSimulation(dinersCount);
        repaint();
    }

    public boolean stopOnePhilosopher(int philosopherNumber) {
        if (simulation == null) {
            return false;
        }

        boolean success = simulation.stopOnePhilosopher(philosopherNumber);
        repaint();

        return success;
    }

    public boolean resumeOnePhilosopher(int philosopherNumber) {
        if (simulation == null) {
            return false;
        }

        boolean success = simulation.resumeOnePhilosopher(philosopherNumber, this);
        repaint();

        return success;
    }

    public int getDinersCount() {
        if (simulation == null) {
            return 0;
        }

        return simulation.getPhilosophersCount();
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

        if (diningTable != null) {
            diningTable.draw((Graphics2D) g, getWidth(), getHeight());
        }
    }
}