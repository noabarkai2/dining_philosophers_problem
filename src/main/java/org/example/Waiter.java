package org.example;

public class Waiter {
    private boolean[] forksTaken;
    private boolean[] waiting;
    private int[] mealsCount;
    private int[] forkOwners;

    private int nextTurn;
    private boolean active;
    private DiningPanel diningPanel;

    public Waiter(int philosophersCount, DiningPanel diningPanel) {
        this.diningPanel = diningPanel;

        forksTaken = new boolean[philosophersCount];
        waiting = new boolean[philosophersCount];
        mealsCount = new int[philosophersCount];
        forkOwners = new int[philosophersCount];

        nextTurn = 0;
        active = true;

        for (int i = 0; i < forkOwners.length; i++) {
            forkOwners[i] = -1;
        }
    }

    public synchronized boolean askToEat(int philosopherId) {
        waiting[philosopherId] = true;
        updateScreen();

        try {
            while (active && findAllowedPhilosopher() != philosopherId) {
                wait();
            }

            if (!active) {
                waiting[philosopherId] = false;
                updateScreen();
                return false;
            }

            int leftFork = getLeftFork(philosopherId);
            int rightFork = getRightFork(philosopherId);

            forksTaken[leftFork] = true;
            forksTaken[rightFork] = true;

            forkOwners[leftFork] = philosopherId;
            forkOwners[rightFork] = philosopherId;

            waiting[philosopherId] = false;
            nextTurn = (philosopherId + 1) % forksTaken.length;
            updateScreen();
            notifyAll();

            return true;

        } catch (InterruptedException e) {
            System.out.println("Philosopher " + philosopherId + " stopped while waiting");
            waiting[philosopherId] = false;
            updateScreen();
            notifyAll();
            return false;
        }
    }

    public synchronized void finishEating(int philosopherId) {
        int leftFork = getLeftFork(philosopherId);
        int rightFork = getRightFork(philosopherId);

        forksTaken[leftFork] = false;
        forksTaken[rightFork] = false;

        forkOwners[leftFork] = -1;
        forkOwners[rightFork] = -1;

        mealsCount[philosopherId]++;
        System.out.println(
                "Philosopher " + philosopherId +
                        " finished eating. Meals: " + mealsCount[philosopherId]
        );

        updateScreen();
        notifyAll();
    }

    public synchronized void stopWaiter() {
        active = false;
        System.out.println("Waiter stopped");
        notifyAll();
        updateScreen();
    }

    private int findAllowedPhilosopher() {
        int selectedPhilosopher = -1;
        int lowestMealsCount = getLowestMealsCount();

        for (int i = 0; i < waiting.length; i++) {
            int philosopherId = (nextTurn + i) % waiting.length;

            if (waiting[philosopherId]
                    && mealsCount[philosopherId] == lowestMealsCount
                    && areForksFree(philosopherId)) {
                selectedPhilosopher = philosopherId;
                break;
            }
        }

        return selectedPhilosopher;
    }

    private int getLowestMealsCount() {
        int lowest = mealsCount[0];

        for (int i = 1; i < mealsCount.length; i++) {
            if (mealsCount[i] < lowest) {
                lowest = mealsCount[i];
            }
        }

        return lowest;
    }

    private boolean areForksFree(int philosopherId) {
        int leftFork = getLeftFork(philosopherId);
        int rightFork = getRightFork(philosopherId);

        return !forksTaken[leftFork] && !forksTaken[rightFork];
    }

    private int getLeftFork(int philosopherId) {
        return (philosopherId - 1 + forksTaken.length) % forksTaken.length;
    }

    private int getRightFork(int philosopherId) {
        return philosopherId;
    }

    public synchronized int getMealsCount(int philosopherId) {
        return mealsCount[philosopherId];
    }

    public synchronized int getForkOwner(int forkId) {
        return forkOwners[forkId];
    }

    private void updateScreen() {
        diningPanel.updateScreen();
    }
}