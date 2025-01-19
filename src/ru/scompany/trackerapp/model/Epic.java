package ru.scompany.trackerapp.model;

import java.util.List;
import java.util.ArrayList;

public class Epic extends Task {

    private final List<Integer> subtasksIds;

    public Epic(int id, String name, String description) {
        super(id, name, description , TaskStatus.NEW);
        this.subtasksIds = new ArrayList<>();
    }

    public List<Integer> getSubtasksId() {
        return subtasksIds;
    }

    public void addSubtaskId(int subtaskId) {
        subtasksIds.add(subtaskId);
    }

    public void removeSubtaskId(int subtaskId) {
        subtasksIds.remove(Integer.valueOf(subtaskId));
    }

    public void clearSubtasksId() {
        subtasksIds.clear();
    }


    @Override
    public String toString() {
        return "Epic{" +
                "id=" + id +
                "name=" + name +
                " , description='" + description + '\'' +
                " , status=" + status +
                " , subtaskIds=" + subtasksIds +
                '}';
    }

}