package ru.scompany.trackerapp.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class Epic extends Task {

    private final List<Integer> subtasksIds;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Duration duration;

    public Epic(int id, String name, String description) {
        super(id, name, description, TaskStatus.NEW, Duration.ZERO, LocalDateTime.now());
        this.subtasksIds = new ArrayList<>();
    }

    public void updateTimeAndDuration(Map<Integer, Subtask> subtasks) {
        this.duration = subtasksIds.stream()
                .map(subtaskId -> subtasks.get(subtaskId))
                .filter(Objects::nonNull)
                .map(Subtask::getDuration)
                .reduce(Duration.ZERO, Duration::plus);

        this.startTime = subtasksIds.stream()
                .map(subtaskId -> subtasks.get(subtaskId))
                .filter(Objects::nonNull)
                .map(Subtask::getStartTime)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        this.endTime = subtasksIds.stream()
                .map(subtaskId -> subtasks.get(subtaskId))
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

    public List<Integer> getSubtasksId() {
        return subtasksIds;
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

    public void clearSubtasksId() {
        subtasksIds.clear();
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", duration=" + duration.toMinutes() + " min" +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", subtaskIds=" + subtasksIds +
                '}';
    }

}