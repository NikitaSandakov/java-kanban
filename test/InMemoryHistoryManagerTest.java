package test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.scompany.trackerapp.model.Task;
import ru.scompany.trackerapp.model.TaskStatus;
import ru.scompany.trackerapp.service.HistoryManager;
import ru.scompany.trackerapp.service.Managers;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;



public class InMemoryHistoryManagerTest {

    private HistoryManager historyManager;

    @BeforeEach
    public void setUp() {
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    public void testTaskPreservesPreviousStateInHistory() {
        Task task = new Task(1, "Task 1", "Description of Task 1", TaskStatus.NEW, Duration.ofHours(1), LocalDateTime.now());
        historyManager.add(task);

        List<Task> history = historyManager.getHistory();

        assertEquals(1, history.size());
        assertEquals("Task 1", history.get(0).getName());
    }

    @Test
    public void testAddNewTask() {
        Task task = new Task(1, "Task 1", "Description of Task 1", TaskStatus.NEW, Duration.ofHours(1), LocalDateTime.now());
        historyManager.add(task);

        List<Task> history = historyManager.getHistory();
        assertEquals(task, history.get(0));
        assertEquals(1, history.size());
    }

    @Test
    public void testRemoveNode() {
        Task task = new Task(1, "Task 1", "Description of Task 1", TaskStatus.NEW, Duration.ofHours(1), LocalDateTime.now());

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
        Task task1 = new Task(1, "Task 1", "Description of Task 1", TaskStatus.NEW, Duration.ofHours(1), LocalDateTime.now());
        Task task2 = new Task(1, "Task 1", "Description of Task 1", TaskStatus.NEW, Duration.ofHours(1), LocalDateTime.now());

        historyManager.add(task1);
        historyManager.add(task2);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task1, history.get(0));
    }

    @Test
    public void testRemoveNonExistentTask() {
        Task task = new Task(1, "Task 1", "Description of Task 1", TaskStatus.NEW, Duration.ofHours(1), LocalDateTime.now());
        historyManager.add(task);
        historyManager.remove(2);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
    }

    @Test
    public void testGetHistory() {
        Task task1 = new Task(1, "Task 1", "Description of Task 1", TaskStatus.NEW, Duration.ofHours(1), LocalDateTime.now());
        Task task2 = new Task(2, "Task 2", "Description of Task 2", TaskStatus.NEW, Duration.ofHours(1), LocalDateTime.now());

        historyManager.add(task1);
        historyManager.add(task2);

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(task1, history.get(0));
        assertEquals(task2, history.get(1));
    }

    @Test
    public void testLinkLastAndGetTasks() {
        HistoryManager historyManager = Managers.getDefaultHistory();

        Task task1 = new Task(1, "Task 1", "Description of Task 1", TaskStatus.NEW, Duration.ofHours(1), LocalDateTime.now());
        Task task2 = new Task(2, "Task 2", "Description of Task 2", TaskStatus.NEW, Duration.ofHours(1), LocalDateTime.now());
        Task task3 = new Task(3, "Task 3", "Description of Task 3", TaskStatus.NEW, Duration.ofHours(1), LocalDateTime.now());

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
        Task task1 = new Task(1, "Task 1", "Description of Task 1", TaskStatus.NEW,
                Duration.ofHours(1), LocalDateTime.now());
        Task task2 = new Task(1, "Task 1", "Description of Task 1", TaskStatus.NEW,
                Duration.ofHours(1), LocalDateTime.now());

        historyManager.add(task1);
        historyManager.add(task2);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task1, history.get(0));
    }

    @Test
    public void testEmptyHistory() {
        List<Task> history = historyManager.getHistory();
        assertTrue(history.isEmpty(), "История должна быть пуста.");
    }

    @Test
    public void testRemoveTaskFromStart() {
        Task task1 = new Task(1, "Task 1", "Description of Task 1", TaskStatus.NEW,
                Duration.ofHours(1), LocalDateTime.now());
        Task task2 = new Task(2, "Task 2", "Description of Task 2", TaskStatus.IN_PROGRESS,
                Duration.ofHours(1), LocalDateTime.now().plusDays(1));
        historyManager.add(task1);
        historyManager.add(task2);

        historyManager.remove(task1.getId());

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "В истории должна остаться одна задача.");
        assertEquals(task2, history.get(0), "Оставшаяся задача должна быть task2.");
    }

    @Test
    public void testRemoveTaskFromMiddle() {
        Task task1 = new Task(1, "Task 1", "Description of Task 1", TaskStatus.NEW,
                Duration.ofHours(1), LocalDateTime.now());
        Task task2 = new Task(2, "Task 2", "Description of Task 2", TaskStatus.IN_PROGRESS,
                Duration.ofHours(2), LocalDateTime.now().plusDays(1));
        Task task3 = new Task(3, "Task 3", "Description of Task 3", TaskStatus.DONE,
                Duration.ofHours(3), LocalDateTime.now().plusDays(2));
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(task2.getId());

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size(), "После удаления задачи из середины в истории должно остаться 2 задачи.");
        assertEquals(task1, history.get(0), "Первая задача должна быть task1.");
        assertEquals(task3, history.get(1), "Вторая задача должна быть task3.");
    }

    @Test
    public void testRemoveTaskFromEnd() {
        Task task1 = new Task(1, "Task 1", "Description of Task 1", TaskStatus.NEW,
                Duration.ofHours(1), LocalDateTime.now());
        Task task2 = new Task(2, "Task 2", "Description of Task 2", TaskStatus.IN_PROGRESS,
                Duration.ofHours(2), LocalDateTime.now().plusDays(1));
        historyManager.add(task1);
        historyManager.add(task2);

        historyManager.remove(task2.getId());

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "После удаления задачи с конца в истории должна остаться 1 задача.");
        assertEquals(task1, history.get(0), "Оставшаяся задача должна быть task1.");
    }

}