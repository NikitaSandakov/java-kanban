package test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import ru.scompany.trackerapp.model.Epic;
import ru.scompany.trackerapp.model.ManagerSaveException;
import ru.scompany.trackerapp.model.Task;
import ru.scompany.trackerapp.model.TaskStatus;
import ru.scompany.trackerapp.service.FileBackedTaskManager;
import ru.scompany.trackerapp.service.InMemoryHistoryManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;

public class FileBackedTaskManagerTest {

    private static final File TEST_FILE = new File("testFile.csv");
    private FileBackedTaskManager manager;

    @BeforeEach
    void setUp() {
        manager = new FileBackedTaskManager(TEST_FILE, new InMemoryHistoryManager());
    }

    @Test
    void shouldThrowManagerSaveExceptionWhenSaveFails() {
        Assertions.assertThrows(ManagerSaveException.class, () -> {
            FileBackedTaskManager managerWithBadFile = new FileBackedTaskManager(new File("/restricted/path"), new InMemoryHistoryManager());
            managerWithBadFile.save();
        });
    }

    @Test
    void shouldThrowManagerSaveExceptionWhenLoadFails() {
        File nonExistentFile = new File("non_existent_file.csv");

        Assertions.assertThrows(ManagerSaveException.class, () -> {
            FileBackedTaskManager.loadFromFile(nonExistentFile);
        });
    }


    @Test
    void shouldNotThrowExceptionWhenFileIsValid() throws IOException {
        Assertions.assertDoesNotThrow(() -> {
            if (!TEST_FILE.exists()) {
                Files.createFile(TEST_FILE.toPath());
            }

            // Убедитесь, что duration не равен null
            Task task1 = new Task(1, "Task 1", "Description of Task 1", TaskStatus.NEW, Duration.ofHours(1), LocalDateTime.now());
            Epic epic1 = new Epic(2, "Epic 1", "Epic description");

            manager.createTask(task1);
            manager.createEpic(epic1);

            manager.save();

            FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(TEST_FILE);
            Assertions.assertNotNull(loadedManager);

            Assertions.assertEquals(1, loadedManager.getAllTask().size());
            Assertions.assertEquals(1, loadedManager.getAllEpic().size());

            Files.delete(TEST_FILE.toPath());
        });
    }

    @Test
    void shouldHandleSaveAndLoadCorrectly() throws IOException {
        // Создаем несколько задач и сохраняем их
        Task task1 = new Task(1, "Task 1", "Description of Task 1", ru.scompany.trackerapp.model.TaskStatus.NEW, Duration.ofHours(1), LocalDateTime.now());
        Epic epic1 = new Epic(2, "Epic 1", "Epic description");

        manager.createTask(task1);
        manager.createEpic(epic1);

        // Сохраняем в файл
        manager.save();

        // Загружаем данные из файла
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(TEST_FILE);

        // Проверяем, что задачи успешно загружены
        Assertions.assertEquals(1, loadedManager.getAllTask().size());
        Assertions.assertEquals(1, loadedManager.getAllEpic().size());

        // Удаляем файл после теста
        Files.delete(TEST_FILE.toPath());
    }

    @Test
    void shouldThrowManagerSaveExceptionWhenFileIsNotAccessible() {
        // Проверка, что исключение выбрасывается, если файл не доступен для записи
        Assertions.assertThrows(ManagerSaveException.class, () -> {
            FileBackedTaskManager managerWithNoPermission = new FileBackedTaskManager(new File("/restricted/path"), new InMemoryHistoryManager());
            managerWithNoPermission.save(); // Ожидаем, что это вызовет исключение
        });
    }

    @Test
    void shouldHandleEmptyFileGracefully() throws IOException {
        if (!TEST_FILE.exists()) {
            Files.createFile(TEST_FILE.toPath());
        }

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(TEST_FILE);

        Assertions.assertEquals(0, loadedManager.getAllTask().size());
        Assertions.assertEquals(0, loadedManager.getAllEpic().size());
        Assertions.assertEquals(0, loadedManager.getAllSubtask().size());

        Files.delete(TEST_FILE.toPath());
    }


    @Test
    void shouldThrowExceptionIfFileIsCorrupted() throws IOException {
        if (TEST_FILE.exists()) {
            Files.delete(TEST_FILE.toPath());
        }

        Files.createFile(TEST_FILE.toPath());

        Files.write(TEST_FILE.toPath(), "corrupted data".getBytes());

        Assertions.assertThrows(ManagerSaveException.class, () -> {
            FileBackedTaskManager.loadFromFile(TEST_FILE);
        });

        Files.delete(TEST_FILE.toPath());
    }


}
