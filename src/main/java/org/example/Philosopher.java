package org.example;

public class Philosopher implements Runnable {
    private int id;
    private Waiter waiter;
    private DiningPanel diningPanel;
    private PhilosopherState state;
    private boolean running;

    public Philosopher(int id, Waiter waiter, DiningPanel diningPanel) {
        this.id = id;
        this.waiter = waiter;
        this.diningPanel = diningPanel;
        this.state = PhilosopherState.THINKING;
        this.running = true;
    }

    @Override
    public void run() {
        while (isRunning()) {
            think();

            if (!isRunning()) {
                break;
            }

            tryToEat();
        }

        System.out.println("Philosopher " + id + " stopped");
    }

    private void think() {
        setPhilosopherState(PhilosopherState.THINKING);
        Utils.sleepRandom(1000, 1500);
    }

    private void tryToEat() {
        setPhilosopherState(PhilosopherState.WAITING_FOR_BOTH_FORKS);

        boolean allowedToEat = waiter.askToEat(this);

        if (!allowedToEat || !isRunning()) {
            return;
        }

        eat();
        waiter.finishEating(id);
    }

    private void eat() {
        setPhilosopherState(PhilosopherState.EATING);
        System.out.println("Philosopher " + id + " is EATING");

        Utils.sleepRandom(1500, 2500);
    }

    public synchronized void stopPhilosopher() {
        running = false;
        setPhilosopherState(PhilosopherState.STOPPED);
    }

    private synchronized boolean isRunning() {
        return running;
    }

    public synchronized void setPhilosopherState(PhilosopherState state) {
        this.state = state;
        diningPanel.updateScreen();
    }

    public synchronized PhilosopherState getStateOfPhilosopher() {
        return state;
    }

    public int getPhilosopherId() {
        return id;
    }
}