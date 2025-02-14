package ru.scompany.trackerapp.service;

import ru.scompany.trackerapp.model.Epic;
import ru.scompany.trackerapp.model.Subtask;
import ru.scompany.trackerapp.model.Task;
import ru.scompany.trackerapp.model.TaskStatus;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

import java.util.Set;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;

public class InMemoryTaskManager implements TaskManager {
    private int taskId = 0;
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Set<Task> prioritizedTasks;
    public HistoryManager historyManager;

    public InMemoryTaskManager() {
        this.prioritizedTasks = Managers.getPrioritizedTasks();
        this.historyManager = Managers.getDefaultHistory();
    }

    public boolean checkForTimeOverlap(Task newTask) {
        return getPrioritizedTasks().stream()
                .anyMatch(existingTask -> isTimeOverlap(newTask, existingTask));
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
        tasks.values().forEach(task -> {
            prioritizedTasks.remove(task);
            historyManager.add(task);
        });
        tasks.clear();
    }

    @Override
    public void removeAllSubtask() {
        subtasks.values().forEach(subtask -> {
            prioritizedTasks.remove(subtask);
            historyManager.add(subtask);
        });
        subtasks.clear();
        epics.values().forEach(epic -> {
            epic.clearSubtasksId();
            updateEpicStatus(epic.getId());
        });
    }

    @Override
    public void removeAllEpic() {
        epics.values().forEach(epic -> {
            prioritizedTasks.remove(epic);
            historyManager.add(epic);
            epic.getSubtasksId().forEach(subtaskId -> {
                prioritizedTasks.remove(subtasks.get(subtaskId));
                historyManager.add(subtasks.get(subtaskId));
            });
        });
        epics.clear();
        subtasks.clear();
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            prioritizedTasks.add(task);
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public Task getEpic(int id) {
        Task epic = epics.get(id);
        if (epic != null) {
            prioritizedTasks.add(epic);
            historyManager.add(epic);
        }
        return epic;
    }

    public Task getSubtask(int id) {
        Task subtask = subtasks.get(id);
        if (subtask != null) {
            prioritizedTasks.add(subtask);
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
        if (tasks.remove(id) != null || subtasks.remove(id) != null) {
            prioritizedTasks.removeIf(task -> task.getId() == id);
            historyManager.remove(id);
        } else if (epics.containsKey(id)) {
            Epic epic = epics.remove(id);
            if (epic != null) {
                epic.getSubtasksId()
                        .forEach(subtaskId -> {
                            subtasks.remove(subtaskId);
                            historyManager.remove(subtaskId);
                        });
            }
            historyManager.remove(id);
        }
        if (this instanceof FileBackedTaskManager) {
            ((FileBackedTaskManager) this).save();
        }
    }

    @Override
    public List<Subtask> getSubtasksOfEpic(int epicId) {
        return epics.containsKey(epicId)
                ? epics.get(epicId).getSubtasksId().stream()
                .map(subtasks::get)
                .toList()
                : Collections.emptyList();
    }

    @Override
    public void createTask(Task task) {
        if (checkForTimeOverlap(task)) {
            throw new IllegalArgumentException("Задача пересекается по времени с другой задачей");
        }
        int id = updateTaskId();
        task.setId(id);
        tasks.put(id, task);
        prioritizedTasks.add(task);
        if (this instanceof FileBackedTaskManager) {
            ((FileBackedTaskManager) this).save();
        }
    }

    @Override
    public void createEpic(Epic epic) {
        int id = updateTaskId();
        epic.setId(id);
        epics.put(id, epic);
        prioritizedTasks.add(epic);
        if (this instanceof FileBackedTaskManager) {
            ((FileBackedTaskManager) this).save();
        }
    }

    @Override
    public void createSubtask(Subtask subtask) {
        if (checkForTimeOverlap(subtask)) {
            throw new IllegalArgumentException("Подзадача пересекается по времени с другой задачей");
        }
        int id = updateTaskId();
        subtask.setId(id);
        subtasks.put(id, subtask);

        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.addSubtaskId(id, subtasks);
            updateEpicStatus(epic.getId());
        }
        prioritizedTasks.add(subtask);
        if (this instanceof FileBackedTaskManager) {
            ((FileBackedTaskManager) this).save();
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
        prioritizedTasks.add(task);

        if (this instanceof FileBackedTaskManager) {
            ((FileBackedTaskManager) this).save();
        }
    }

    @Override
    public void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) return;

        List<TaskStatus> statuses = epic.getSubtasksId().stream()
                .map(subtasks::get)
                .map(Subtask::getStatus)
                .toList();

        if (statuses.isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
        } else if (statuses.stream().allMatch(status -> status == TaskStatus.NEW)) {
            epic.setStatus(TaskStatus.NEW);
        } else if (statuses.stream().allMatch(status -> status == TaskStatus.DONE)) {
            epic.setStatus(TaskStatus.DONE);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
        if (this instanceof FileBackedTaskManager) {
            ((FileBackedTaskManager) this).save();
        }
    }

    @Override
    public int updateTaskId() {
        taskId++;
        if (this instanceof FileBackedTaskManager) {
            ((FileBackedTaskManager) this).save();
        }
        return taskId;

    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    private boolean isTimeOverlap(Task task1, Task task2) {
        LocalDateTime start1 = task1.getStartTime();
        LocalDateTime end1 = task1.getEndTime();
        LocalDateTime start2 = task2.getStartTime();
        LocalDateTime end2 = task2.getEndTime();

        if (start1 == null || end1 == null || start2 == null || end2 == null) {
            return false;
        }

        return start1.isBefore(end2) && start2.isBefore(end1);
    }

}