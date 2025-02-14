package ru.scompany.trackerapp.service;

import ru.scompany.trackerapp.model.Task;

import java.io.File;
import java.util.Set;
import java.util.TreeSet;

public class Managers {

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static InMemoryTaskManager getDefaultTaskManager() {
        return new InMemoryTaskManager();
    }

    public static FileBackedTaskManager getFileBackedTaskManager(File file) {
        return new FileBackedTaskManager(file, getDefaultHistory());
    }

    public static Set<Task> getPrioritizedTasks() {
        return new TreeSet<>(new TaskComparator());
    }

}