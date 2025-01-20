package ru.scompany.trackerapp.test;

import org.junit.jupiter.api.Test;
import ru.scompany.trackerapp.model.Task;
import ru.scompany.trackerapp.model.TaskStatus;


import static org.junit.jupiter.api.Assertions.*;

public class TaskTest {

    @Test
    public void testTaskEquality() {
        Task task1 = new Task(1, "Task 1", "Description 1", TaskStatus.NEW);
        Task task2 = new Task(1, "Task 1", "Description 1", TaskStatus.NEW);

        assertEquals(task1, task2);
    }

}