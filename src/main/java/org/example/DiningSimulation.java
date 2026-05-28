package org.example;

public class DiningSimulation {
    private Philosopher[] philosophers;
    private Thread[] philosopherThreads;
    private Waiter waiter;
    private DiningPanel diningPanel;

    public DiningSimulation(int philosophersCount, DiningPanel diningPanel) {
        this.diningPanel = diningPanel;

        waiter = new Waiter(philosophersCount, diningPanel);
        philosophers = new Philosopher[philosophersCount];
        philosopherThreads = new Thread[philosophersCount];

        for (int i = 0; i < philosophersCount; i++) {
            philosophers[i] = new Philosopher(i, waiter, diningPanel);
        }
    }

    public void start() {
        System.out.println("Simulation started");

        for (int i = 0; i < philosophers.length; i++) {
            philosopherThreads[i] = new Thread(philosophers[i]);
            philosopherThreads[i].start();
        }
    }

    public void stop() {
        System.out.println("Stopping simulation");

        for (int i = 0; i < philosophers.length; i++) {
            philosophers[i].stopPhilosopher();
        }

        waiter.stopWaiter();
    }

    public boolean stopOnePhilosopher(int philosopherNumber) {
        int index = philosopherNumber - 1;

        if (index < 0 || index >= philosophers.length) {
            return false;
        }

        boolean waiterStopped = waiter.stopOnePhilosopher(index);

        if (!waiterStopped) {
            return false;
        }

        philosophers[index].stopPhilosopher();

        return true;
    }

    public boolean resumeOnePhilosopher(int philosopherNumber, DiningPanel diningPanel) {
        int index = philosopherNumber - 1;

        if (index < 0 || index >= philosophers.length) {
            return false;
        }

        if (philosopherThreads[index] != null && philosopherThreads[index].isAlive()) {
            System.out.println("Cannot resume P" + philosopherNumber + ". Old thread is still running.");
            return false;
        }

        if (philosophers[index].getStateOfPhilosopher() != PhilosopherState.STOPPED) {
            return false;
        }

        boolean waiterResumed = waiter.resumeOnePhilosopher(index);

        if (!waiterResumed) {
            return false;
        }

        philosophers[index] = new Philosopher(index, waiter, diningPanel);
        philosopherThreads[index] = new Thread(philosophers[index]);
        philosopherThreads[index].start();

        System.out.println("P" + philosopherNumber + " resumed successfully");

        return true;
    }

    public int getPhilosophersCount() {
        return philosophers.length;
    }

    public PhilosopherState getPhilosopherState(int index) {
        return philosophers[index].getStateOfPhilosopher();
    }

    public int getMealsCount(int index) {
        return waiter.getMealsCount(index);
    }

    public int getForkOwner(int forkId) {
        return waiter.getForkOwner(forkId);
    }
}