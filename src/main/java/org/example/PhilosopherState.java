package org.example;

public enum PhilosopherState {
    THINKING,
    WAITING_FOR_BOTH_FORKS, // המצב החדש והאטומי שלנו
    EATING,
    STOPPED
}