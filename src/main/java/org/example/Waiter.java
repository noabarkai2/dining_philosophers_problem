package org.example;

public class Waiter {
    private static final int FREE_FORK_OWNER = -1;
    private static final int NO_SELECTED_PHILOSOPHER = -1;
    private static final int MAX_MEALS_GAP = 1;

    private boolean[] forksTaken;
    private boolean[] waiting;
    private boolean[] stopped;
    private int[] mealsCount;
    private int[] forkOwners;

    private int nextTurn;
    private boolean active;
    private DiningPanel diningPanel;

    public Waiter(int philosophersCount, DiningPanel diningPanel) {
        this.diningPanel = diningPanel;

        forksTaken = new boolean[philosophersCount];
        waiting = new boolean[philosophersCount];
        stopped = new boolean[philosophersCount];
        mealsCount = new int[philosophersCount];
        forkOwners = new int[philosophersCount];

        nextTurn = 0;
        active = true;

        for (int i = 0; i < forkOwners.length; i++) {
            forkOwners[i] = FREE_FORK_OWNER;
        }
    }

    public synchronized boolean askToEat(Philosopher philosopher) {
        int philosopherId = philosopher.getPhilosopherId();

        if (stopped[philosopherId]) {
            return false;
        }

        waiting[philosopherId] = true;
        updateWaitingState(philosopher);
        updateScreen();

        try {
            while (active && !stopped[philosopherId] && findAllowedPhilosopher() != philosopherId) {
                updateWaitingState(philosopher);
                wait();
            }

            if (!active || stopped[philosopherId]) {
                waiting[philosopherId] = false;
                philosopher.setPhilosopherState(PhilosopherState.STOPPED);
                updateScreen();
                notifyAll();
                return false;
            }

            takeForks(philosopherId);

            waiting[philosopherId] = false;
            philosopher.setPhilosopherState(PhilosopherState.EATING);

            nextTurn = (philosopherId + 1) % forksTaken.length;

            updateScreen();
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

        if (!stopped[philosopherId]) {
            mealsCount[philosopherId]++;
        }

        System.out.println(
                "Philosopher " + philosopherId +
                        " finished eating. Meals: " + mealsCount[philosopherId]
        );

        updateScreen();
        notifyAll();
    }

    public synchronized void stopOnePhilosopher(int philosopherId) {
        if (philosopherId < 0 || philosopherId >= stopped.length) {
            return;
        }

        stopped[philosopherId] = true;
        waiting[philosopherId] = false;

        System.out.println("Waiter stopped philosopher " + philosopherId);

        updateScreen();
        notifyAll();
    }

    public synchronized void stopWaiter() {
        active = false;
        System.out.println("Waiter stopped");
        notifyAll();
        updateScreen();
    }

    private void updateWaitingState(Philosopher philosopher) {
        int philosopherId = philosopher.getPhilosopherId();

        int leftFork = getLeftFork(philosopherId);
        int rightFork = getRightFork(philosopherId);

        boolean leftForkFree = !forksTaken[leftFork];
        boolean rightForkFree = !forksTaken[rightFork];

        if (!leftForkFree && !rightForkFree) {
            philosopher.setPhilosopherState(PhilosopherState.WAITING_FOR_BOTH_FORKS);
            return;
        }

        if (!leftForkFree) {
            philosopher.setPhilosopherState(PhilosopherState.WAITING_FOR_LEFT_FORK);
            return;
        }

        if (!rightForkFree) {
            philosopher.setPhilosopherState(PhilosopherState.WAITING_FOR_RIGHT_FORK);
            return;
        }

        philosopher.setPhilosopherState(PhilosopherState.WAITING_FOR_BOTH_FORKS);
    }

    private int findAllowedPhilosopher() {
        int selectedPhilosopher = findAllowedPhilosopherWithMealGap();

        if (selectedPhilosopher != NO_SELECTED_PHILOSOPHER) {
            return selectedPhilosopher;
        }

        return findAllowedPhilosopherWithoutMealGap();
    }

    private int findAllowedPhilosopherWithMealGap() {
        int selectedPhilosopher = NO_SELECTED_PHILOSOPHER;
        int lowestWaitingMeals = getLowestWaitingMeals();

        for (int i = 0; i < waiting.length; i++) {
            int philosopherId = (nextTurn + i) % waiting.length;

            if (canEatNow(philosopherId)
                    && mealsCount[philosopherId] <= lowestWaitingMeals + MAX_MEALS_GAP) {
                selectedPhilosopher = chooseBetterPhilosopher(selectedPhilosopher, philosopherId);
            }
        }

        return selectedPhilosopher;
    }

    private int findAllowedPhilosopherWithoutMealGap() {
        int selectedPhilosopher = NO_SELECTED_PHILOSOPHER;

        for (int i = 0; i < waiting.length; i++) {
            int philosopherId = (nextTurn + i) % waiting.length;

            if (canEatNow(philosopherId)) {
                selectedPhilosopher = chooseBetterPhilosopher(selectedPhilosopher, philosopherId);
            }
        }

        return selectedPhilosopher;
    }

    private int chooseBetterPhilosopher(int currentSelected, int candidate) {
        if (currentSelected == NO_SELECTED_PHILOSOPHER) {
            return candidate;
        }

        if (mealsCount[candidate] < mealsCount[currentSelected]) {
            return candidate;
        }

        return currentSelected;
    }

    private boolean canEatNow(int philosopherId) {
        return !stopped[philosopherId]
                && waiting[philosopherId]
                && areForksFree(philosopherId);
    }

    private int getLowestWaitingMeals() {
        int lowest = Integer.MAX_VALUE;

        for (int i = 0; i < waiting.length; i++) {
            if (!stopped[i] && waiting[i] && mealsCount[i] < lowest) {
                lowest = mealsCount[i];
            }
        }

        return lowest;
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

        forkOwners[leftFork] = FREE_FORK_OWNER;
        forkOwners[rightFork] = FREE_FORK_OWNER;
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