package org.example;

public class DiningSimulation {
    private Philosopher[] philosophers;
    private Waiter waiter;

    public DiningSimulation(int philosophersCount, DiningPanel diningPanel) {
        waiter = new Waiter(philosophersCount, diningPanel);
        philosophers = new Philosopher[philosophersCount];

        for (int i = 0; i < philosophersCount; i++) {
            philosophers[i] = new Philosopher(i, waiter, diningPanel);
        }
    }

    public void start() {
        System.out.println("Simulation started");

        for (int i = 0; i < philosophers.length; i++) {
            philosophers[i].start();
        }
    }

    public void stop() {
        System.out.println("Stopping old simulation");

        for (int i = 0; i < philosophers.length; i++) {
            philosophers[i].stopPhilosopher();
        }

        waiter.stopWaiter();
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