package ru.scompany.trackerapp.service;

import ru.scompany.trackerapp.model.Epic;
import ru.scompany.trackerapp.model.Subtask;
import ru.scompany.trackerapp.model.Task;

import java.util.List;

public interface HistoryManager {

    void add(Task task);

    void add(Subtask subtask);

    void add(Epic epic);

    List<Task> getHistory();

    void remove(int id);

}