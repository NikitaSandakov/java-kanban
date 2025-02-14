package test;

import org.junit.jupiter.api.Test;
import ru.scompany.trackerapp.model.Task;
import ru.scompany.trackerapp.model.TaskStatus;
import ru.scompany.trackerapp.service.HistoryManager;
import ru.scompany.trackerapp.service.Managers;
import ru.scompany.trackerapp.service.InMemoryHistoryManager;
import ru.scompany.trackerapp.service.InMemoryTaskManager;
import ru.scompany.trackerapp.service.FileBackedTaskManager;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class ManagersTest {

    @Test
    public void testGetDefaultHistoryReturnsInitializedManager() {
        HistoryManager historyManager = Managers.getDefaultHistory();

        assertNotNull(historyManager, "HistoryManager не должен быть нулевым");
        assertInstanceOf(InMemoryHistoryManager.class, historyManager);
    }

    @Test
    public void testGetDefaultTaskManagerReturnsInitializedManager() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        assertNotNull(taskManager, "InMemoryTaskManager не должен быть нулевым");
        assertInstanceOf(InMemoryTaskManager.class, taskManager);
    }

    @Test
    public void testGetFileBackedTaskManagerReturnsInitializedManager() {
        File file = new File("test_tasks.csv");
        FileBackedTaskManager fileBackedTaskManager = Managers.getFileBackedTaskManager(file);

        assertNotNull(fileBackedTaskManager, "FileBackedTaskManager не должен быть нулевым");
        assertInstanceOf(FileBackedTaskManager.class, fileBackedTaskManager);
    }

    @Test
    public void testGetPrioritizedTasksOrder() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        Task task1 = new Task(2, "Task 2", "Description 2", TaskStatus.NEW, Duration.ofHours(0), LocalDateTime.now());
        Task task2 = new Task(1, "Task 1", "Description 1", TaskStatus.NEW, Duration.ofHours(2), LocalDateTime.now().plusHours(3));

        taskManager.createTask(task2);
        taskManager.createTask(task1);

        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();

        assertEquals("Первой должна быть task1", task1, prioritizedTasks.get(0));
        assertEquals("Второй должна быть task2", task2, prioritizedTasks.get(1));
    }

}