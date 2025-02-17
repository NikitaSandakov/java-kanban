package ru.scompany.trackerapp.service;

import ru.scompany.trackerapp.model.Epic;
import ru.scompany.trackerapp.model.Subtask;
import ru.scompany.trackerapp.model.Task;

import java.util.List;

public interface TaskManager {

    List<Task> getAllTask();

    List<Epic> getAllEpic();

    List<Subtask> getAllSubtask();

    void removeAllTask();

    void removeAllSubtask();

    void removeAllEpic();

    Task getTask(int id);

    Epic getEpic(int id);

    Subtask getSubtask(int id);

    void removeTaskById(int id);

    void removeSubtaskById(int id);

    void removeEpicById(int id);

    List<Subtask> getSubtasksOfEpic(int epicId);

    void createTask(Task task);

    void createEpic(Epic epic);

    void createSubtask(Subtask subtask);

    void updateTask(Task task);

    void updateEpicStatus(int epicId);

    int updateTaskId();

    List<Task> getHistory();

    List<Task> getPrioritizedTasks();

}