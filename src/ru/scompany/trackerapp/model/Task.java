package ru.scompany.trackerapp.model;
import ru.scompany.trackerapp.service.*;

import java.util.Objects;

public class Task {
    protected int id;
    protected String name;
    protected String description;
    protected TaskStatus status;

    public Task (int id, String name, String description, TaskStatus status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Task tasks = (Task) obj;
        return id == tasks.id &&
                Objects.equals(description, tasks.description) &&
                status == tasks.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, description, status);
    }

    @Override
    public String toString() {
        return "Tasks{" +
                "id=" + id +
                "name=" + name +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }

}