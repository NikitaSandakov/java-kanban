package test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.scompany.trackerapp.model.Task;
import ru.scompany.trackerapp.model.TaskStatus;
import ru.scompany.trackerapp.service.TaskManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {

    protected T taskManager;

    protected abstract T createTaskManager();

    @BeforeEach
    public void setUp() {
        taskManager = createTaskManager();
    }

    @Test
    public void testCreateAndGetTask() {
        Task task = new Task(1, "Task 1", "Description of Task 1", TaskStatus.NEW,
                Duration.ofHours(1), LocalDateTime.now());
        taskManager.createTask(task);

        List<Task> tasks = taskManager.getAllTask();
        assertEquals(1, tasks.size());
        assertEquals(task, tasks.get(0));

        Task retrievedTask = taskManager.getTask(task.getId());
        assertNotNull(retrievedTask);
        assertEquals(task.getId(), retrievedTask.getId());
    }

    @Test
    public void testCreateTaskWithDuplicateId() {
        Task task1 = new Task(1, "Task 1", "Description of Task 1", TaskStatus.NEW,
                Duration.ofHours(1), LocalDateTime.now());
        Task task2 = new Task(1, "Task 2", "Description of Task 2", TaskStatus.NEW,
                Duration.ofHours(2), LocalDateTime.now().plusDays(1));

        taskManager.createTask(task1);
        taskManager.createTask(task2);

        List<Task> tasks = taskManager.getAllTask();
        assertEquals(1, tasks.size());
        assertEquals(task2, tasks.get(0));
    }

    @Test
    public void testUpdateTask() {
        Task task = new Task(1, "Task 1", "Description of Task 1", TaskStatus.NEW,
                Duration.ofHours(1), LocalDateTime.now());
        taskManager.createTask(task);

        // Обновляем задачу
        task.setName("Updated Task 1");
        task.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateTask(task);

        Task updatedTask = taskManager.getTask(task.getId());
        assertNotNull(updatedTask);
        assertEquals("Updated Task 1", updatedTask.getName());
        assertEquals(TaskStatus.IN_PROGRESS, updatedTask.getStatus());
    }

    @Test
    public void testDeleteTask() {
        Task task = new Task(1, "Task 1", "Description of Task 1", TaskStatus.NEW,
                Duration.ofHours(1), LocalDateTime.now());
        taskManager.createTask(task);

        taskManager.removeTaskById(task.getId());

        Task deletedTask = taskManager.getTask(task.getId());
        assertNull(deletedTask, "Задача должна быть удалена");
    }

    @Test
    public void testHistoryTracking() {
        Task task1 = new Task(1, "Task 1", "Description of Task 1", TaskStatus.NEW,
                Duration.ofHours(1), LocalDateTime.now());
        taskManager.createTask(task1);

        taskManager.getTask(task1.getId());

        assertEquals(1, taskManager.getHistory().size(), "История должна содержать 1 задачу");
    }
}
