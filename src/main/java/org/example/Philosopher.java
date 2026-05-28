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

        setPhilosopherState(PhilosopherState.STOPPED);
    }

    private void think() {
        setPhilosopherState(PhilosopherState.THINKING);
        Utils.sleepRandom(0, 5000);
    }

    private void tryToEat() {
        setPhilosopherState(PhilosopherState.WAITING_FOR_BOTH_FORKS);

        boolean gotForks = waiter.takeForksAtomically(id);

        if (!gotForks || !isRunning()) {
            return;
        }

        eat();

        waiter.finishEating(id);
    }

    private void eat() {
        setPhilosopherState(PhilosopherState.EATING);
        Utils.sleepRandom(0, 1000);
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

        if (diningPanel != null) {
            diningPanel.updateScreen();
        }
    }

    public synchronized PhilosopherState getStateOfPhilosopher() {
        return state;
    }

    public int getPhilosopherId() {
        return id;
    }
}