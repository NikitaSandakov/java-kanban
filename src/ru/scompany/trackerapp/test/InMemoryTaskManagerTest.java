package ru.scompany.trackerapp.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.scompany.trackerapp.model.Task;
import ru.scompany.trackerapp.model.Epic;
import ru.scompany.trackerapp.model.Subtask;
import ru.scompany.trackerapp.model.TaskStatus;
import ru.scompany.trackerapp.service.HistoryManager;
import ru.scompany.trackerapp.service.InMemoryHistoryManager;
import ru.scompany.trackerapp.service.InMemoryTaskManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest {

    private InMemoryTaskManager taskManager;

    @BeforeEach
    public void setUp() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        taskManager = new InMemoryTaskManager(historyManager);
    }

    @Test
    public void testCreateAndGetTask() {
        Task task = new Task(1,"Task 1", "Description of Task 1", TaskStatus.NEW);
        taskManager.createTask(task);

        List<Task> tasks = taskManager.getAllTask();
        assertEquals(1, tasks.size());
        assertEquals(task, tasks.getFirst());

        Task retrievedTask = taskManager.getTask(task.getId());
        assertNotNull(retrievedTask);
        assertEquals(task.getId(), retrievedTask.getId());
    }

    @Test
    public void testCreateAndGetEpic() {
        Epic epic = new Epic(1,"Epic 1", "Description of Epic 1");
        taskManager.createEpic(epic);

        List<Epic> epics = taskManager.getAllEpic();
        assertEquals(1, epics.size());
        assertEquals(epic, epics.getFirst());

        Task retrievedEpic = taskManager.getEpic(epic.getId());
        assertNotNull(retrievedEpic);
        assertEquals(epic.getId(), retrievedEpic.getId());
    }

    @Test
    public void testCreateAndGetSubtask() {
        Epic epic = new Epic(1,"Epic 1", "Description of Epic 1");
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask(2,"Subtask 1", "Description of Subtask 1", TaskStatus.NEW, epic.getId());
        taskManager.createSubtask(subtask);

        List<Subtask> subtasks = taskManager.getAllSubtask();
        assertEquals(1, subtasks.size());
        assertEquals(subtask, subtasks.getFirst());

        Task retrievedSubtask = taskManager.getSubtask(subtask.getId());
        assertNotNull(retrievedSubtask);
        assertEquals(subtask.getId(), retrievedSubtask.getId());
    }

    @Test
    public void testRemoveTaskById() {
        Task task = new Task(1, "Task 1", "Description of Task 1", TaskStatus.NEW);
        taskManager.createTask(task);
        taskManager.removeTaskById(task.getId());

        assertNull(taskManager.getTask(task.getId()));
    }

    @Test
    public void testRemoveEpicAndSubtasks() {
        Epic epic = new Epic(1,"Epic 1", "Description of Epic 1");
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask(2,"Subtask 1", "Description of Subtask 1", TaskStatus.NEW, epic.getId());
        taskManager.createSubtask(subtask);

        taskManager.removeTaskById(epic.getId());

        assertNull(taskManager.getEpic(epic.getId()));
        assertNull(taskManager.getSubtask(subtask.getId()));
    }

    @Test
    public void testIdConflict() {
        Task task1 = new Task(1,"Task 1", "Description of Task 1", TaskStatus.NEW);
        task1.setId(1);
        taskManager.createTask(task1);

        Task task2 = new Task(1,"Task 2", "Description of Task 2", TaskStatus.NEW);
        taskManager.createTask(task2);

        Task retrievedTask1 = taskManager.getTask(task1.getId());
        Task retrievedTask2 = taskManager.getTask(task2.getId());

        assertEquals(task1.getId(), retrievedTask1.getId());
        assertEquals(task2.getId(), retrievedTask2.getId());
        assertNotEquals(retrievedTask1.getId(), retrievedTask2.getId());

    }

    @Test
    public void testTaskImmutabilityAfterAdding() {
        Task originalTask = new Task(1,"Task 1", "Description of Task 1", TaskStatus.NEW);
        taskManager.createTask(originalTask);

        Task retrievedTask = taskManager.getTask(originalTask.getId());

        assertEquals(originalTask.getId(), retrievedTask.getId());
        assertEquals(originalTask.getName(), retrievedTask.getName());
        assertEquals(originalTask.getDescription(), retrievedTask.getDescription());
        assertEquals(originalTask.getStatus(), retrievedTask.getStatus());

    }

}
