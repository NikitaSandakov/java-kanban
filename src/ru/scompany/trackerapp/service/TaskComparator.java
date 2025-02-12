package ru.scompany.trackerapp.service;

import ru.scompany.trackerapp.model.Task;
import java.util.Comparator;

public class TaskComparator implements Comparator<Task> {
    @Override
    public int compare(Task t1, Task t2) {
        if (t1.getStartTime() == null && t2.getStartTime() == null) {
            return 0;
        } else if (t1.getStartTime() == null) {
            return 1;
        } else if (t2.getStartTime() == null) {
            return -1;
        }
        return t1.getStartTime().compareTo(t2.getStartTime());
    }
}