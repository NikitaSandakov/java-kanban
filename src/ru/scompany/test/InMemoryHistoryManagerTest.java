package test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.scompany.trackerapp.model.*;
import ru.scompany.trackerapp.service.InMemoryHistoryManager;

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
        Task task = new Task(1, "Task 1", "Description of Task 1", TaskStatus.NEW);
        historyManager.add(task);

        List<Task> history = historyManager.getHistory();

        assertEquals(1, history.size());
        assertEquals("Task 1", history.getFirst().getName());
    }

    @Test
    public void testAddNewTask() {
        Task task = new Task(1, "Task 1", "Description of Task 1", TaskStatus.NEW);
        historyManager.add(task);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task, history.getFirst());
    }

    @Test
    public void testAddDuplicateTask() {
        Task task1 = new Task(1, "Task 1", "Description of Task 1", TaskStatus.NEW);
        Task task2 = new Task(1, "Task 1 Updated", "Description of Task 1 Updated", TaskStatus.NEW);

        historyManager.add(task1);
        historyManager.add(task2);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task2, history.getFirst());
    }

    @Test
    public void testRemoveNode() {
        Task task = new Task(1, "Task 1", "Description of Task 1", TaskStatus.NEW);
        historyManager.add(task);
        historyManager.remove(task.getId());

        List<Task> history = historyManager.getHistory();
        assertTrue(history.isEmpty());
    }

    @Test
    public void testRemoveNonExistentTask() {
        Task task = new Task(1, "Task 1", "Description of Task 1", TaskStatus.NEW);
        historyManager.add(task);
        historyManager.remove(2);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
    }

    @Test
    public void testGetHistory() {
        Task task1 = new Task(1, "Task 1", "Description of Task 1", TaskStatus.NEW);
        Task task2 = new Task(2, "Task 2", "Description of Task 2", TaskStatus.NEW);

        historyManager.add(task1);
        historyManager.add(task2);

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(task1, history.get(0));
        assertEquals(task2, history.get(1));
    }
}