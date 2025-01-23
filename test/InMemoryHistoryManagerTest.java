package test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.scompany.trackerapp.model.Task;
import ru.scompany.trackerapp.model.TaskStatus;
import ru.scompany.trackerapp.service.InMemoryHistoryManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        assertEquals(task, history.getFirst());
        assertEquals(1, history.size());
    }

    @Test
    public void testRemoveNode() {
        Task task = new Task(1, "Task 1", "Description of Task 1", TaskStatus.NEW);

        historyManager.add(task);
        List<Task> history = historyManager.getHistory();
        assertFalse(history.isEmpty());
        assertEquals(1, history.size());

        historyManager.remove(task.getId());
        history = historyManager.getHistory();
        assertTrue(history.isEmpty());
    }

    @Test
    public void testAddDuplicateTask() {
        Task task1 = new Task(1, "Task 1", "Description of Task 1", TaskStatus.NEW);
        Task task2 = new Task(1, "Task 1", "Description of Task 1", TaskStatus.NEW);

        historyManager.add(task1);
        historyManager.add(task2);

        List<Task> history = historyManager.getHistory();
        assertEquals(task1, history.get(0));
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

    @Test
    public void testLinkLastAndGetTasks() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();

        Task task1 = new Task(1, "Task 1", "Description of Task 1", TaskStatus.NEW);
        Task task2 = new Task(2, "Task 2", "Description of Task 2", TaskStatus.NEW);
        Task task3 = new Task(3, "Task 3", "Description of Task 3", TaskStatus.NEW);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        List<Task> tasks = historyManager.getHistory();

        assertEquals(3, tasks.size());

        assertEquals(task1, tasks.get(0));
        assertEquals(task2, tasks.get(1));
        assertEquals(task3, tasks.get(2));
    }

    @Test
    public void testAddTwoIdenticalTasks() {
        Task task1 = new Task(1, "Task 1", "Description of Task 1", TaskStatus.NEW);
        Task task2 = new Task(1, "Task 1", "Description of Task 1", TaskStatus.NEW);

        historyManager.add(task1);
        historyManager.add(task2);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task1, history.getFirst());
    }

}