package test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.scompany.trackerapp.model.Task;
import ru.scompany.trackerapp.model.Epic;
import ru.scompany.trackerapp.model.Subtask;
import ru.scompany.trackerapp.model.TaskStatus;
import ru.scompany.trackerapp.service.InMemoryTaskManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest {

    private InMemoryTaskManager taskManager;

    @BeforeEach
    public void setUp() {
        taskManager = new InMemoryTaskManager();
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
    public void testCreateAndGetEpic() {
        Epic epic = new Epic(1, "Epic 1", "Description of Epic 1");
        taskManager.createEpic(epic);

        List<Epic> epics = taskManager.getAllEpic();
        assertEquals(1, epics.size());
        assertEquals(epic, epics.get(0));

        Task retrievedEpic = taskManager.getEpic(epic.getId());
        assertNotNull(retrievedEpic);
        assertEquals(epic.getId(), retrievedEpic.getId());
    }

    @Test
    public void testCreateAndGetSubtask() {
        Epic epic = new Epic(1, "Epic 1", "Description of Epic 1");
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask(2, "Subtask 1", "Description of Subtask 1", TaskStatus.NEW,
                Duration.ofHours(1), LocalDateTime.now(), epic.getId());
        taskManager.createSubtask(subtask);

        List<Subtask> subtasks = taskManager.getAllSubtask();
        assertEquals(1, subtasks.size());
        assertEquals(subtask, subtasks.get(0));

        Subtask retrievedSubtask = (Subtask) taskManager.getSubtask(subtask.getId());
        assertNotNull(retrievedSubtask);
        assertEquals(subtask.getId(), retrievedSubtask.getId());
    }

    @Test
    public void testSubtaskHasCorrectEpic() {
        Epic epic = new Epic(1, "Epic 1", "Description of Epic 1");
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask(2, "Subtask 1", "Description of Subtask 1", TaskStatus.NEW,
                Duration.ofHours(1), LocalDateTime.now(), epic.getId());
        taskManager.createSubtask(subtask);

        Subtask retrievedSubtask = (Subtask) taskManager.getSubtask(subtask.getId());
        assertNotNull(retrievedSubtask);

        assertEquals(epic.getId(), retrievedSubtask.getEpicId());
    }


    @Test
    public void testEpicStatusCalculation() {
        Epic epic = new Epic(1, "Epic 1", "Description of Epic 1");
        taskManager.createEpic(epic);

        Subtask subtask1 = new Subtask(2, "Subtask 1", "Description of Subtask 1", TaskStatus.NEW,
                Duration.ofHours(1), LocalDateTime.now(), epic.getId());
        taskManager.createSubtask(subtask1);

        Subtask subtask2 = new Subtask(3, "Subtask 2", "Description of Subtask 2", TaskStatus.DONE,
                Duration.ofHours(1), LocalDateTime.now().plusHours(2), epic.getId());
        taskManager.createSubtask(subtask2);

        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }


    @Test
    public void testRemoveTaskById() {
        Task task = new Task(1, "Task 1", "Description of Task 1", TaskStatus.NEW,
                Duration.ofHours(1), LocalDateTime.now());
        taskManager.createTask(task);
        taskManager.removeTaskById(task.getId());

        assertNull(taskManager.getTask(task.getId()));
    }

    @Test
    public void testRemoveEpicAndSubtasks() {
        Epic epic = new Epic(1, "Epic 1", "Description of Epic 1");
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask(2, "Subtask 1", "Description of Subtask 1", TaskStatus.NEW,
                Duration.ofHours(1), LocalDateTime.now(), epic.getId());
        taskManager.createSubtask(subtask);

        taskManager.removeTaskById(epic.getId());

        assertNull(taskManager.getEpic(epic.getId()));
        assertNull(taskManager.getSubtask(subtask.getId()));
    }

    @Test
    public void testTaskImmutabilityAfterAdding() {
        Task originalTask = new Task(1, "Task 1", "Description of Task 1", TaskStatus.NEW,
                Duration.ofHours(1), LocalDateTime.now());
        taskManager.createTask(originalTask);

        Task retrievedTask = taskManager.getTask(originalTask.getId());

        assertEquals(originalTask.getId(), retrievedTask.getId());
        assertEquals(originalTask.getName(), retrievedTask.getName());
        assertEquals(originalTask.getDescription(), retrievedTask.getDescription());
        assertEquals(originalTask.getStatus(), retrievedTask.getStatus());
    }

    @Test
    public void testHistoryIsEmptyAfterTaskDeletion() {
        Task task = new Task(1, "Task 1", "Description of Task 1", TaskStatus.NEW,
                Duration.ofHours(1), LocalDateTime.now());
        taskManager.createTask(task);

        taskManager.getTask(task.getId());

        taskManager.removeTaskById(task.getId());

        List<Task> history = taskManager.getHistory();
        assertTrue(history.isEmpty());
    }

    @Test
    public void testHistoryAfterEpicAndSubtaskDeletion() {
        Epic epic = new Epic(1, "Epic 1", "Description of Epic 1");
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask(2, "Subtask 1", "Description of Subtask 1", TaskStatus.NEW,
                Duration.ofHours(1), LocalDateTime.now(), epic.getId());
        taskManager.createSubtask(subtask);

        taskManager.getEpic(epic.getId());
        taskManager.getSubtask(subtask.getId());

        taskManager.removeTaskById(epic.getId());

        List<Task> history = taskManager.getHistory();
        assertTrue(history.isEmpty());
    }

    @Test
    public void testTimeIntervalsDoNotOverlap() {
        Task task1 = new Task(1, "Task 1", "Description of Task 1", TaskStatus.NEW,
                Duration.ofHours(1), LocalDateTime.now().plusHours(1));
        taskManager.createTask(task1);

        Task task2 = new Task(2, "Task 2", "Description of Task 2", TaskStatus.NEW,
                Duration.ofHours(1), LocalDateTime.now().plusHours(1));

        assertThrows(IllegalArgumentException.class, () -> taskManager.createTask(task2));
    }


}
