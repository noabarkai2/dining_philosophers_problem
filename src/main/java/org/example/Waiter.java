package org.example;

public class Waiter {
    private boolean[] forks;
    private int[] forkOwners;

    private int[] mealsCount;
    private int[] serviceScore;

    private boolean[] stopped;
    private boolean[] waiting;
    private boolean[] approvedToEat;

    private int[] waitingOrder;
    private int waitingCount;

    private DiningPanel diningPanel;
    private boolean active;

    private static int FREE = -1;
    private static int MAX_ALLOWED_GAP = 5;

    public Waiter(int count, DiningPanel diningPanel) {
        this.diningPanel = diningPanel;

        forks = new boolean[count];
        forkOwners = new int[count];

        mealsCount = new int[count];
        serviceScore = new int[count];

        stopped = new boolean[count];
        waiting = new boolean[count];
        approvedToEat = new boolean[count];

        waitingOrder = new int[count];
        waitingCount = 0;

        active = true;

        for (int i = 0; i < count; i++) {
            forkOwners[i] = FREE;
        }
    }

    public synchronized boolean takeForksAtomically(int id) {
        if (!active || stopped[id]) {
            return false;
        }

        waiting[id] = true;
        addToWaitingOrder(id);

        System.out.println("P" + (id + 1) + " waiting");

        approveAllPossiblePhilosophers();

        updateScreen();
        notifyAll();

        try {
            while (active && !stopped[id] && !approvedToEat[id]) {
                wait();
            }

            if (!active || stopped[id]) {
                waiting[id] = false;
                approvedToEat[id] = false;
                removeFromWaitingOrder(id);

                updateScreen();
                notifyAll();

                return false;
            }

            approvedToEat[id] = false;

            return true;

        } catch (InterruptedException e) {
            waiting[id] = false;
            approvedToEat[id] = false;
            removeFromWaitingOrder(id);

            updateScreen();
            notifyAll();

            return false;
        }
    }

    public synchronized void finishEating(int id) {
        int leftFork = getLeftFork(id);
        int rightFork = getRightFork(id);

        if (forkOwners[leftFork] == id) {
            forks[leftFork] = false;
            forkOwners[leftFork] = FREE;
        }

        if (forkOwners[rightFork] == id) {
            forks[rightFork] = false;
            forkOwners[rightFork] = FREE;
        }

        if (!stopped[id] && active) {
            mealsCount[id]++;
            serviceScore[id]++;
        }

        System.out.println("P" + (id + 1) + " finished. Meals: " + mealsCount[id]);
        printMeals();

        approveAllPossiblePhilosophers();

        updateScreen();
        notifyAll();
    }

    public synchronized boolean stopOnePhilosopher(int id) {
        if (id < 0 || id >= stopped.length) {
            return false;
        }

        if (stopped[id]) {
            return false;
        }

        stopped[id] = true;
        waiting[id] = false;
        approvedToEat[id] = false;
        removeFromWaitingOrder(id);

        int leftFork = getLeftFork(id);
        int rightFork = getRightFork(id);

        if (forkOwners[leftFork] == id) {
            forks[leftFork] = false;
            forkOwners[leftFork] = FREE;
        }

        if (forkOwners[rightFork] == id) {
            forks[rightFork] = false;
            forkOwners[rightFork] = FREE;
        }

        System.out.println("P" + (id + 1) + " stopped");
        printMeals();

        approveAllPossiblePhilosophers();

        updateScreen();
        notifyAll();

        return true;
    }

    public synchronized boolean resumeOnePhilosopher(int id) {
        if (id < 0 || id >= stopped.length) {
            return false;
        }

        if (!stopped[id]) {
            return false;
        }

        int averageScore = getAverageActiveServiceScore();

        if (averageScore == -1) {
            serviceScore[id] = mealsCount[id];
        } else {
            serviceScore[id] = averageScore;
        }

        stopped[id] = false;

        System.out.println("P" + (id + 1) + " resumed. Meals stayed: " + mealsCount[id]);
        printMeals();

        approveAllPossiblePhilosophers();

        updateScreen();
        notifyAll();

        return true;
    }

    public synchronized void stopWaiter() {
        active = false;

        for (int i = 0; i < waiting.length; i++) {
            waiting[i] = false;
            approvedToEat[i] = false;
        }

        waitingCount = 0;

        System.out.println("Waiter stopped");

        updateScreen();
        notifyAll();
    }

    private void approveAllPossiblePhilosophers() {
        boolean approvedSomeone = true;

        while (approvedSomeone) {
            approvedSomeone = false;

            for (int i = 0; i < waitingCount; i++) {
                int id = waitingOrder[i];

                if (stopped[id]) {
                    continue;
                }

                if (!waiting[id]) {
                    continue;
                }

                if (!areForksFree(id)) {
                    continue;
                }

                if (hasWaitingPhilosopherFarBehindWhoCanEat(id)) {
                    continue;
                }

                approvePhilosopher(id);
                removeFromWaitingOrder(id);
                approvedSomeone = true;
                break;
            }
        }
    }

    private void approvePhilosopher(int id) {
        int leftFork = getLeftFork(id);
        int rightFork = getRightFork(id);

        forks[leftFork] = true;
        forks[rightFork] = true;

        forkOwners[leftFork] = id;
        forkOwners[rightFork] = id;

        waiting[id] = false;
        approvedToEat[id] = true;

        System.out.println(
                "P" + (id + 1) +
                        " eating with forks " + leftFork + " and " + rightFork
        );
    }

    private boolean hasWaitingPhilosopherFarBehindWhoCanEat(int id) {
        for (int i = 0; i < waitingCount; i++) {
            int otherId = waitingOrder[i];

            if (otherId == id) {
                continue;
            }

            if (stopped[otherId]) {
                continue;
            }

            if (!waiting[otherId]) {
                continue;
            }

            if (!areForksFree(otherId)) {
                continue;
            }

            if (serviceScore[id] > serviceScore[otherId] + MAX_ALLOWED_GAP) {
                return true;
            }
        }

        return false;
    }

    private void addToWaitingOrder(int id) {
        if (isInWaitingOrder(id)) {
            return;
        }

        waitingOrder[waitingCount] = id;
        waitingCount++;
    }

    private void removeFromWaitingOrder(int id) {
        int index = -1;

        for (int i = 0; i < waitingCount; i++) {
            if (waitingOrder[i] == id) {
                index = i;
                break;
            }
        }

        if (index == -1) {
            return;
        }

        for (int i = index; i < waitingCount - 1; i++) {
            waitingOrder[i] = waitingOrder[i + 1];
        }

        waitingCount--;
    }

    private boolean isInWaitingOrder(int id) {
        for (int i = 0; i < waitingCount; i++) {
            if (waitingOrder[i] == id) {
                return true;
            }
        }

        return false;
    }

    private int getAverageActiveServiceScore() {
        int sum = 0;
        int count = 0;

        for (int i = 0; i < serviceScore.length; i++) {
            if (!stopped[i]) {
                sum += serviceScore[i];
                count++;
            }
        }

        if (count == 0) {
            return -1;
        }

        return sum / count;
    }

    private boolean areForksFree(int id) {
        int leftFork = getLeftFork(id);
        int rightFork = getRightFork(id);

        return !forks[leftFork] && !forks[rightFork];
    }

    private int getLeftFork(int id) {
        return (id - 1 + forks.length) % forks.length;
    }

    private int getRightFork(int id) {
        return id;
    }

    public synchronized int getMealsCount(int id) {
        return mealsCount[id];
    }

    public synchronized int getForkOwner(int forkId) {
        return forkOwners[forkId];
    }

    private void updateScreen() {
        if (diningPanel != null) {
            diningPanel.updateScreen();
        }
    }

    private void printMeals() {
        System.out.print("Meals: ");

        for (int i = 0; i < mealsCount.length; i++) {
            System.out.print("P" + (i + 1) + "=" + mealsCount[i] + " ");
        }

        System.out.println();
    }
}