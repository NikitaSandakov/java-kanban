package test;

import org.junit.jupiter.api.Test;
import ru.scompany.trackerapp.model.Epic;
import ru.scompany.trackerapp.model.Subtask;
import ru.scompany.trackerapp.model.Task;
import ru.scompany.trackerapp.model.TaskStatus;
import ru.scompany.trackerapp.service.FileBackedTaskManager;
import ru.scompany.trackerapp.service.InMemoryHistoryManager;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {

    @Test
    void shouldSaveAndLoadEmptyFile() throws IOException {
        File tempFile = File.createTempFile("test_empty", ".csv");
        tempFile.deleteOnExit();

        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile, new InMemoryHistoryManager());
        manager.save();

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertTrue(loadedManager.getAllTask().isEmpty(), "Список задач должен быть пустым");
        assertTrue(loadedManager.getAllEpic().isEmpty(), "Список эпиков должен быть пустым");
        assertTrue(loadedManager.getAllSubtask().isEmpty(), "Список подзадач должен быть пустым");
        assertTrue(loadedManager.historyManager.getHistory().isEmpty(), "История должна быть пустой");
    }

    @Test
    void shouldSaveAndLoadMultipleTasks() throws IOException {
        File tempFile = File.createTempFile("test_multiple_tasks", ".csv");
        tempFile.deleteOnExit();

        FileBackedTaskManager manager = getFileBackedTaskManager(tempFile);

        manager.save();

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(2, loadedManager.getAllTask().size(), "Количество задач не совпадает");
        assertEquals(1, loadedManager.getAllEpic().size(), "Количество эпиков не совпадает");
        assertEquals(1, loadedManager.getAllSubtask().size(), "Количество подзадач не совпадает");

        assertEquals(2, loadedManager.historyManager.getHistory().size(), "История просмотров восстановилась некорректно");
    }

    private static FileBackedTaskManager getFileBackedTaskManager(File tempFile) throws IOException {
        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile, new InMemoryHistoryManager());

        Task task1 = new Task(1, "Задача 1", "Описание 1", TaskStatus.NEW);
        Task task2 = new Task(2, "Задача 2", "Описание 2", TaskStatus.IN_PROGRESS);

        Epic epic1 = new Epic(3,"Эпик 1", "Описание эпика");

        Subtask subtask1 = new Subtask(4,"Подзадача 1", "Описание подзадачи", TaskStatus.DONE, epic1.getId());

        manager.createTask(task1);
        manager.createTask(task2);
        manager.createEpic(epic1);
        manager.createSubtask(subtask1);


        manager.getTask(task1.getId());
        manager.getSubtask(subtask1.getId());
        return manager;
    }

    @Test
    void shouldRestoreCorrectDataFromFile() throws IOException {
        File tempFile = File.createTempFile("test_restore_data", ".csv");
        tempFile.deleteOnExit();

        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile, new InMemoryHistoryManager());

        Task task = new Task(1,"Сходить в магазин", "Купить хлеб", TaskStatus.NEW);
        manager.createTask(task);

        Epic epic = new Epic(2,"Ремонт", "Ремонт в квартире");
        manager.createEpic(epic);

        Subtask subtask = new Subtask(3,"Купить краску", "Синий цвет", TaskStatus.IN_PROGRESS, epic.getId());
        manager.createSubtask(subtask);

        manager.save();

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(1, loadedManager.getAllTask().size(), "Неверное количество загруженных задач");
        assertEquals(1, loadedManager.getAllEpic().size(), "Неверное количество загруженных эпиков");
        assertEquals(1, loadedManager.getAllSubtask().size(), "Неверное количество загруженных подзадач");

        Task loadedTask = loadedManager.getAllTask().getFirst();
        assertEquals(task.getName(), loadedTask.getName(), "Имя задачи не совпадает");
        assertEquals(task.getStatus(), loadedTask.getStatus(), "Статус задачи не совпадает");

        Epic loadedEpic = loadedManager.getAllEpic().getFirst();
        assertEquals(epic.getName(), loadedEpic.getName(), "Имя эпика не совпадает");

        Subtask loadedSubtask = loadedManager.getAllSubtask().getFirst();
        assertEquals(subtask.getEpicId(), loadedSubtask.getEpicId(), "Epic ID подзадачи не совпадает");
    }

    @Test
    void shouldHandleEmptyHistory() throws IOException {
        File tempFile = File.createTempFile("test_empty_history", ".csv");
        tempFile.deleteOnExit();

        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile, new InMemoryHistoryManager());

        Task task = new Task(1,"Тестовая задача", "Описание", TaskStatus.NEW);
        manager.createTask(task);
        manager.save();

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertTrue(loadedManager.historyManager.getHistory().isEmpty(), "История должна быть пустой");
    }
}
