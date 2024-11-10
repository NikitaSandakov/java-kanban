package ru.scompany.trackerapp.service;

import ru.scompany.trackerapp.model.*;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

public class TaskManager {
    private int taskId = 0;
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();

    public List<Task> getAllTask() {
        return new ArrayList<>(tasks.values());
    }

    public List<Epic> getAllEpic() {
        return new ArrayList<>(epics.values());
    }

    public List<Subtask> getAllSubtask() {
        return new ArrayList<>(subtasks.values());
    }

    public void removeAllTask() {
        tasks.clear();
    }

    public HashMap<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    public void removeAllSubtask() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.clearSubtasksId();
            updateEpicStatus(epic.getId());
        }
    }

    public void removeAllEpic() {
        epics.clear();
        subtasks.clear();
    }

    public Task getTaskById(int id) {
        if (tasks.containsKey(id))
            return tasks.get(id);
        if (epics.containsKey(id))
            return epics.get(id);
        if (subtasks.containsKey(id))
            return subtasks.get(id);
        return null;
    }

    public void removeTaskById(int id) {
        if (tasks.remove(id) != null) {
            return;
        }
        if (subtasks.containsKey(id)) {
            int epicId = subtasks.remove(id).getEpicId();
            epics.get(epicId).removeSubtaskId(id);
            updateEpicStatus(epicId);
        } else if (epics.containsKey(id)) {
            Epic epic = epics.remove(id);
            for (int subtaskId : epic.getSubtasksId()) {
                subtasks.remove(subtaskId);
            }
        }
    }


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

    public void createTask(Task task) {
        int id = updateTaskId();
        task.setId(id);
        tasks.put(id, task);
    }

    public void createEpic(Epic epic) {
        int id = updateTaskId();
        epic.setId(id);
        epics.put(id, epic);
    }

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

    private void updateEpicStatus(int epicId) {
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

    public int updateTaskId() {
        return ++taskId;
    }

}