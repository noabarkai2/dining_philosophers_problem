package org.example;

public class Philosopher implements Runnable {
    private int id;
    private Waiter waiter;
    private DiningPanel diningPanel;
    private PhilosopherState state;
    private boolean running;

    private int firstForkToPick;
    private int secondForkToPick;

    public Philosopher(int id, Waiter waiter, DiningPanel diningPanel) {
        this.id = id;
        this.waiter = waiter;
        this.diningPanel = diningPanel;
        this.state = PhilosopherState.THINKING;
        this.running = true;

        int totalPhilosophers = waiter.getTotalPhilosophers();
        int leftForkIndex = (id - 1 + totalPhilosophers) % totalPhilosophers;
        int rightForkIndex = id;

        // היררכיית משאבים למניעת Deadlock
        this.firstForkToPick = Math.min(leftForkIndex, rightForkIndex);
        this.secondForkToPick = Math.max(leftForkIndex, rightForkIndex);
    }

    @Override
    public void run() {
        while (isRunning()) {
            think();
            if (!isRunning()) break;
            tryToEat();
        }
        setPhilosopherState(PhilosopherState.STOPPED);
    }

    private void think() {
        setPhilosopherState(PhilosopherState.THINKING);
        Utils.sleepRandom(0, 5000);
    }

    private void tryToEat() {
        // מניעת הרעבה
        while (isRunning() && !waiter.isFairToEat(id)) {
            Utils.sleep(100);
        }
        if (!isRunning()) return;

        // ניסיון להרים מזלג ראשון
        setPhilosopherState(PhilosopherState.WAITING_FOR_FIRST_FORK);
        while (isRunning() && !waiter.tryTakeFork(firstForkToPick, id)) {
            Utils.sleep(100);
        }
        if (!isRunning()) {
            waiter.putForks(firstForkToPick, -1);
            return;
        }

        Utils.sleepRandom(0, 1000);
        // ניסיון להרים מזלג שני
        setPhilosopherState(PhilosopherState.WAITING_FOR_SECOND_FORK);
        while (isRunning() && !waiter.tryTakeFork(secondForkToPick, id)) {
            Utils.sleep(100);
        }
        if (!isRunning()) {
            waiter.putForks(firstForkToPick, secondForkToPick);
            return;
        }

        eat();

        // החזרת מזלגות
        waiter.putForks(firstForkToPick, secondForkToPick);
    }

    private void eat() {
        setPhilosopherState(PhilosopherState.EATING);
        waiter.finishEating(id);
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