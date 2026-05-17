package org.example;

import java.util.Random;

public class Utils {
    private static Random random = new Random();

    private Utils() {
    }

    public static void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            System.out.println("Sleep was stopped");
        }
    }

    public static void sleepRandom(int minMilliseconds, int maxMilliseconds) {
        int time = minMilliseconds + random.nextInt(maxMilliseconds - minMilliseconds + 1);
        sleep(time);
    }
}