package ru.scompany.trackerapp.model;

import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

public class TaskTest {

    @Test
    public void testTaskEquality() {
        Task task1 = new Task(1, "Task 1", "Description 1", TaskStatus.NEW);
        Task task2 = new Task(1, "Task 1", "Description 1", TaskStatus.NEW);

        assertEquals(task1, task2);
    }

}