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

            takeForks(philosopherId);

            waiting[philosopherId] = false;
            nextTurn = (philosopherId + 1) % forksTaken.length;

            updateScreen();
            //אחרי לקיחת מזלגות- כדי לאפשר לעוד פילוסופים לא צמודים לאכול במקביל.
            notifyAll();

            return true;

        } catch (InterruptedException e) {
            waiting[philosopherId] = false;
            updateScreen();
            notifyAll();
            return false;
        }
    }

    public synchronized void finishEating(int philosopherId) {
        releaseForks(philosopherId);

        mealsCount[philosopherId]++;

        System.out.println(
                "Philosopher " + philosopherId +
                        " finished eating. Meals: " + mealsCount[philosopherId]
        );

        updateScreen();
        //אחרי שחרור מזלגות- כדי לתת למי שהיה חסום בגלל מזלגות הזדמנות לאכול.
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
        int lowestMealsCount = Integer.MAX_VALUE;

        for (int i = 0; i < waiting.length; i++) {
            int philosopherId = (nextTurn + i) % waiting.length;

            if (waiting[philosopherId] && areForksFree(philosopherId)) {
                if (mealsCount[philosopherId] < lowestMealsCount) {
                    selectedPhilosopher = philosopherId;
                    lowestMealsCount = mealsCount[philosopherId];
                }
            }
        }

        return selectedPhilosopher;
    }

    private void takeForks(int philosopherId) {
        int leftFork = getLeftFork(philosopherId);
        int rightFork = getRightFork(philosopherId);

        forksTaken[leftFork] = true;
        forksTaken[rightFork] = true;

        forkOwners[leftFork] = philosopherId;
        forkOwners[rightFork] = philosopherId;
    }

    private void releaseForks(int philosopherId) {
        int leftFork = getLeftFork(philosopherId);
        int rightFork = getRightFork(philosopherId);

        forksTaken[leftFork] = false;
        forksTaken[rightFork] = false;

        forkOwners[leftFork] = -1;
        forkOwners[rightFork] = -1;
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