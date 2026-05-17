package org.example;

public class Philosopher extends Thread {
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

            becomeHungry();
            tryToEat();
        }

        System.out.println("Philosopher " + id + " stopped");
    }

    private void think() {
        setPhilosopherState(PhilosopherState.THINKING);
        Utils.sleepRandom(1000, 3000);
    }

    private void becomeHungry() {
        setPhilosopherState(PhilosopherState.HUNGRY);
    }

    private void tryToEat() {
        boolean allowedToEat = waiter.askToEat(id);

        if (!allowedToEat || !isRunning()) {
            System.out.println("Philosopher " + id + " did not get permission to eat");
            return;
        }

        eat();

        waiter.finishEating(id);
    }

    private void eat() {
        setPhilosopherState(PhilosopherState.EATING);
        System.out.println("Philosopher " + id + " is EATING");

        Utils.sleepRandom(1000, 3000);
    }

    public synchronized void stopPhilosopher() {
        running = false;
    }

    private synchronized boolean isRunning() {
        return running;
    }

    private synchronized void setPhilosopherState(PhilosopherState state) {
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