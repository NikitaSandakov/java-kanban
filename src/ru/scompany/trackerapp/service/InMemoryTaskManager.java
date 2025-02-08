package ru.scompany.trackerapp.service;

import ru.scompany.trackerapp.model.*;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

public class InMemoryTaskManager implements TaskManager<Task> {
    private int taskId = 0;
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    public final HistoryManager historyManager;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    @Override
    public List<Task> getAllTask() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getAllEpic() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getAllSubtask() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void removeAllTask() {
        for (Task task : tasks.values()) {
            historyManager.remove(task.getId());
        }
        tasks.clear();
    }

    @Override
    public void removeAllSubtask() {
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getId());
        }
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.clearSubtasksId();
            updateEpicStatus(epic.getId());
        }
    }

    @Override
    public void removeAllEpic() {
        for (Epic epic : epics.values()) {
            historyManager.remove(epic.getId());
            for (int subtaskId : epic.getSubtasksId()) {
                historyManager.remove(subtaskId);
            }
        }
        epics.clear();
        subtasks.clear();
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public Task getEpic(int id) {
        Task epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }

    @Override
    public Task getSubtask(int id) {
        Task subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.add(subtask);
        }
        return subtask;
    }

    public Map<Integer, Task> getTasks() {
        return tasks;
    }

    public Map<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    public Map<Integer, Epic> getEpics() {
        return epics;
    }

    @Override
    public void removeTaskById(int id) {
        if (tasks.remove(id) != null) {
            historyManager.remove(id);
            return;
        }
        if (subtasks.containsKey(id)) {
            int epicId = subtasks.remove(id).getEpicId();
            epics.get(epicId).removeSubtaskId(id);
            historyManager.remove(id);
            updateEpicStatus(epicId);
        } else if (epics.containsKey(id)) {
            Epic epic = epics.remove(id);
            for (int subtaskId : epic.getSubtasksId()) {
                subtasks.remove(subtaskId);
                historyManager.remove(subtaskId);
            }
            historyManager.remove(epic.getId());
        }
    }

    @Override
    public List<Subtask> getSubtasksOfEpic(int epicId) {
        List<Subtask> subtaskOfEpic = new ArrayList<>();

        Epic epic = epics.get(epicId);
        if (epic != null) {
            for (int subtaskId : epic.getSubtasksId()) {
                Subtask subtask = subtasks.get(subtaskId);
                subtaskOfEpic.add(subtask);

            }
        }
        return subtaskOfEpic;
    }

    @Override
    public void createTask(Task task) {
        int id = updateTaskId();
        task.setId(id);
        tasks.put(id, task);
    }

    @Override
    public void createEpic(Epic epic) {
        int id = updateTaskId();
        epic.setId(id);
        epics.put(id, epic);
    }

    @Override
    public void createSubtask(Subtask subtask) {
        int id = updateTaskId();
        subtask.setId(id);
        subtasks.put(id, subtask);
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.addSubtaskId(id);
            updateEpicStatus(epic.getId());
        }
    }


    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        } else if (subtasks.containsKey(task.getId())) {
            subtasks.put(task.getId(), (Subtask) task);
            updateEpicStatus(((Subtask) task).getEpicId());
        } else if (epics.containsKey(task.getId())) {
            Epic oldEpic = epics.get(task.getId());
            oldEpic.setName(task.getName());
            oldEpic.setDescription(task.getDescription());
        }
    }

    @Override
    public void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null)
            return;

        boolean allNew = true;
        boolean allDone = true;

        for (int subtaskId : epic.getSubtasksId()) {
            TaskStatus status = subtasks.get(subtaskId).getStatus();
            if (status != TaskStatus.NEW) {
                allNew = false;
            }
            if (status != TaskStatus.DONE) {
                allDone = false;
            }
        }

        if (allNew) {
            epic.setStatus(TaskStatus.NEW);
        } else if (allDone) {
            epic.setStatus(TaskStatus.DONE);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    @Override
    public int updateTaskId() {
        return ++taskId;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

}