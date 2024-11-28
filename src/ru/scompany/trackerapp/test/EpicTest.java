package ru.scompany.trackerapp.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


import ru.scompany.trackerapp.model.Epic;
import ru.scompany.trackerapp.model.Subtask;
import ru.scompany.trackerapp.model.TaskStatus;
import ru.scompany.trackerapp.service.InMemoryHistoryManager;
import ru.scompany.trackerapp.service.InMemoryTaskManager;

public class EpicTest {

    @Test
    public void testEpicEquality() {
        Epic epic1 = new Epic(1, "Task 1", "Description 1");
        Epic epic2 = new Epic(1, "Task 1", "Description 1");

        Assertions.assertEquals(epic1, epic2);
    }

    @Test
    public void testEpicCannotAddItselfAsSubTask() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager(new InMemoryHistoryManager());
        Epic epic = new Epic(1, "1 epic", "Description 1 epic");
        Epic epic1 = new Epic(1, "1 epic", "Description 1 epic");

        taskManager.createEpic(epic);

        taskManager.createSubtask(new Subtask(1, "Subtask", "Description", TaskStatus.NEW,
                epic.getId()));

        Assertions.assertEquals(epic, epic1);

    }

}