package ru.scompany.trackerapp.service;

import ru.scompany.trackerapp.model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> history = new ArrayList<>();
    private static final int MAX_HISTORY_SIZE = 10;

    @Override
    public void add(Task task) {
        if (history.size() >= MAX_HISTORY_SIZE) {
            history.remove(0);
        }
        history.add(task.copy());
    }


    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(history);
    }
}
