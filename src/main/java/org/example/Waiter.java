package org.example;

public class Waiter {
    private static final int FREE = -1;
    private boolean[] forks;
    private int[] forkOwners;
    private int[] mealsCount;
    private boolean[] stopped;
    private DiningPanel diningPanel;
    private boolean active;

    public Waiter(int count, DiningPanel diningPanel) {
        this.diningPanel = diningPanel;
        this.active = true;

        forks = new boolean[count];
        forkOwners = new int[count];
        mealsCount = new int[count];
        stopped = new boolean[count];

        for (int i = 0; i < count; i++) {
            forks[i] = true; // המזלג פנוי
            forkOwners[i] = FREE;
        }
    }

    // ניסיון להרים מזלג בודד אם הוא פנוי הפילוסוף מקבל אותו
    public synchronized boolean tryTakeFork(int forkIndex, int philosopherId) {
        if (!active || stopped[philosopherId]) {
            return false;
        }
        if (forks[forkIndex]) {
            forks[forkIndex] = false;
            forkOwners[forkIndex] = philosopherId;
            updateScreen();
            return true;
        }
        return false;
    }

    // החזרת המזלגות לשולחן
    public synchronized void putForks(int firstFork, int secondFork) {
        if (firstFork >= 0) {
            forks[firstFork] = true;
            forkOwners[firstFork] = FREE;
        }
        if (secondFork >= 0) {
            forks[secondFork] = true;
            forkOwners[secondFork] = FREE;
        }
        updateScreen();
    }

    // בדיקת הוגנות למניעת הרעבה ושמירה על סדר גודל אכילות
    public synchronized boolean isFairToEat(int id) {
        if (!active || stopped[id]) return false;

        int myMeals = mealsCount[id];
        int minMeals = Integer.MAX_VALUE;

        for (int i = 0; i < mealsCount.length; i++) {
            if (!stopped[i] && mealsCount[i] < minMeals) {
                minMeals = mealsCount[i];
            }
        }

        if (minMeals == Integer.MAX_VALUE) return true;
        return (myMeals - minMeals) <= 3; // מוודא שאין פער של יותר מ-3 ארוחות
    }

    public synchronized void finishEating(int id) {
        if (!stopped[id] && active) {
            mealsCount[id]++;
        }
        System.out.println("P" + (id + 1) + " finished. Meals: " + mealsCount[id]);
        updateScreen();
    }

    public synchronized boolean stopOnePhilosopher(int id) {
        if (id < 0 || id >= stopped.length || stopped[id]) {
            return false;
        }
        stopped[id] = true;

        // משחרר את המזלגות של הפילוסוף שהופסק
        for (int i = 0; i < forkOwners.length; i++) {
            if (forkOwners[i] == id) {
                forks[i] = true;
                forkOwners[i] = FREE;
            }
        }

        System.out.println("P" + (id + 1) + " stopped");
        updateScreen();
        return true;
    }

    public synchronized boolean resumeOnePhilosopher(int id) {
        if (id < 0 || id >= stopped.length || !stopped[id]) {
            return false;
        }
        stopped[id] = false;
        System.out.println("P" + (id + 1) + " resumed");
        updateScreen();
        return true;
    }

    public synchronized void stopWaiter() {
        active = false;
        System.out.println("Waiter stopped");
        updateScreen();
    }

    public synchronized int getMealsCount(int id) {
        return mealsCount[id];
    }

    public synchronized int getForkOwner(int forkId) {
        return forkOwners[forkId];
    }

    public int getTotalPhilosophers() {
        return forks.length;
    }

    private void updateScreen() {
        if (diningPanel != null) {
            diningPanel.updateScreen();
        }
    }
}