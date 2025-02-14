package ru.scompany.trackerapp.service;

import ru.scompany.trackerapp.model.Task;

import java.util.List;

public interface HistoryManager {

    void add(Task task);

    List<Task> getHistory();

    void remove(int id);

}