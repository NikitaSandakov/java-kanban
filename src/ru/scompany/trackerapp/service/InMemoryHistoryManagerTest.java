package ru.scompany.trackerapp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.scompany.trackerapp.model.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryHistoryManagerTest {

    private InMemoryHistoryManager historyManager;

    @BeforeEach
    public void setUp() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    public void testTaskPreservesPreviousStateInHistory() {
        Task originalTask = new Task(1,"Task 1", "Description of Task 1", TaskStatus.NEW);
        historyManager.add(originalTask);

        originalTask.setName("Updated Task 1");

        List<Task> history = historyManager.getHistory();

        assertEquals(1, history.size());
        assertEquals("Task 1", history.get(0).getName());
        assertNotEquals("Updated Task 1", history.get(0).getName());
    }
}
