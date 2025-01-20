package ru.scompany.trackerapp.service;

public class Managers {
    public static InMemoryHistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
