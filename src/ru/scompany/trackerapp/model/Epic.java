package ru.scompany.trackerapp.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class Epic extends Task {

    private List<Integer> subtasksIds;

    public Epic(int id, String name, String description) {
        super(id, name, description, TaskStatus.NEW, Duration.ZERO, LocalDateTime.now());
        this.subtasksIds = new ArrayList<>();
    }

    public void updateTimeAndDuration(Map<Integer, Subtask> subtasks) {
        if (subtasks == null || subtasks.isEmpty()) {
            this.duration = Duration.ZERO;
            this.startTime = null;
            this.endTime = null;
            return;
        }

        this.duration = subtasksIds.stream()
                .map(subtasks::get)
                .filter(Objects::nonNull)
                .map(Subtask::getDuration)
                .reduce(Duration.ZERO, Duration::plus);

        this.startTime = subtasksIds.stream()
                .map(subtasks::get)
                .filter(Objects::nonNull)
                .map(Subtask::getStartTime)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        this.endTime = subtasksIds.stream()
                .map(subtasks::get)
                .filter(Objects::nonNull)
                .map(Subtask::getEndTime)
                .max(LocalDateTime::compareTo)
                .orElse(null);
    }

    @Override
    public Duration getDuration() {
        return duration;
    }

    @Override
    public LocalDateTime getStartTime() {
        return startTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public List<Integer> getSubtasksIds() {
        return subtasksIds;
    }

    public void setSubtasksIds(List<Integer> subtasksIds) {
        this.subtasksIds = subtasksIds != null ? subtasksIds : new ArrayList<>();
    }

    public void addSubtaskId(int subtaskId, Map<Integer, Subtask> subtasks) {
        if (!subtasksIds.contains(subtaskId)) {
            subtasksIds.add(subtaskId);
            updateTimeAndDuration(subtasks);
        }
    }

    public void removeSubtaskId(int subtaskId, Map<Integer, Subtask> subtasks) {
        if (subtasksIds.remove(Integer.valueOf(subtaskId))) {
            updateTimeAndDuration(subtasks);
        }
    }

    public void clearSubtasksIds() {
        subtasksIds.clear();
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", duration=" + (duration != null ? duration.toMinutes() + " min" : "null") +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", subtaskIds=" + subtasksIds +
                '}';
    }

}