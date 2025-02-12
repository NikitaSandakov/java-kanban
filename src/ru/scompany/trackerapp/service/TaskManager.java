package ru.scompany.trackerapp.service;

import ru.scompany.trackerapp.model.Epic;
import ru.scompany.trackerapp.model.Subtask;
import ru.scompany.trackerapp.model.Task;

import java.io.IOException;
import java.util.List;

public interface TaskManager<T extends Task> {

    List<T> getAllTask();

    List<Epic> getAllEpic();

    List<Subtask> getAllSubtask();

    void removeAllTask();

    void removeAllSubtask();

    void removeAllEpic();

    T getTask(int id);

    Task getEpic(int id);

    Task getSubtask(int id);

    void removeTaskById(int id);

    List<Subtask> getSubtasksOfEpic(int epicId);

    void createTask(T task);

    void createEpic(Epic epic);

    void createSubtask(Subtask subtask) throws IOException;

    void updateTask(Task task);

    void updateEpicStatus(int epicId);

    int updateTaskId();

    List<Task> getHistory();

}
