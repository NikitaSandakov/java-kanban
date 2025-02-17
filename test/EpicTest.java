package test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.scompany.trackerapp.model.Epic;
import ru.scompany.trackerapp.model.Subtask;
import ru.scompany.trackerapp.model.TaskStatus;
import ru.scompany.trackerapp.service.InMemoryTaskManager;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class EpicTest {

    @Test
    public void testEpicEquality() {
        Epic epic1 = new Epic(1, "Task 1", "Description 1");
        Epic epic2 = new Epic(1, "Task 1", "Description 1");

        Assertions.assertEquals(epic1, epic2);
    }

    @Test
    public void testEpicCannotAddItselfAsSubTask() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        Epic epic = new Epic(1, "1 epic", "Description 1 epic");

        taskManager.createEpic(epic);

        Subtask subtask = new Subtask(1, "Subtask", "Description", TaskStatus.NEW, Duration.ofHours(1),
                LocalDateTime.now(), epic.getId());

        Assertions.assertDoesNotThrow(() -> taskManager.createSubtask(subtask));
    }

    @Test
    public void testSubtaskTimeOverlap() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        Epic epic = new Epic(1, "Epic 1", "Epic description");
        taskManager.createEpic(epic);

        Subtask subtask1 = new Subtask(1, "Subtask 1", "Description 1", TaskStatus.NEW, Duration.ofHours(1),
                LocalDateTime.of(2025, 2, 12, 12, 0), epic.getId());
        Subtask subtask2 = new Subtask(2, "Subtask 2", "Description 2", TaskStatus.NEW, Duration.ofHours(1),
                LocalDateTime.of(2025, 2, 12, 12, 30), epic.getId());

        taskManager.createSubtask(subtask1);

        boolean overlapBefore = taskManager.checkForTimeOverlap(subtask2);
        assertTrue(overlapBefore, "Пересечение должно быть обнаружено перед добавлением подзадачи");

        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            taskManager.createSubtask(subtask2);
        });

        Assertions.assertEquals("Subtask overlaps with another task's time.", exception.getMessage());

        boolean overlapAfter = taskManager.checkForTimeOverlap(subtask2);
        assertTrue(overlapAfter, "После добавления пересечение должно быть обнаружено");
    }


    @Test
    public void testEpicStatusAllNew() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        Epic epic = new Epic(1, "Epic 1", "Epic description");
        taskManager.createEpic(epic);

        Subtask subtask1 = new Subtask(1, "Subtask 1", "Description 1", TaskStatus.NEW, Duration.ofHours(1),
                LocalDateTime.of(2025, 2, 12, 12, 0), epic.getId());
        Subtask subtask2 = new Subtask(2, "Subtask 2", "Description 2", TaskStatus.NEW, Duration.ofHours(1),
                LocalDateTime.of(2025, 2, 12, 13, 0), epic.getId());
        Subtask subtask3 = new Subtask(3, "Subtask 3", "Description 3", TaskStatus.NEW, Duration.ofHours(1),
                LocalDateTime.of(2025, 2, 12, 14, 0), epic.getId());

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask3);

        taskManager.updateEpicStatus(epic.getId());
        Assertions.assertEquals(TaskStatus.NEW, epic.getStatus());
    }


    @Test
    public void testEpicStatusAllDone() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        Epic epic = new Epic(1, "Epic 1", "Epic description");
        taskManager.createEpic(epic);

        Subtask subtask1 = new Subtask(1, "Subtask 1", "Description 1", TaskStatus.DONE, Duration.ofHours(1),
                LocalDateTime.of(2025, 2, 12, 12, 0), epic.getId());
        Subtask subtask2 = new Subtask(2, "Subtask 2", "Description 2", TaskStatus.DONE, Duration.ofHours(1),
                LocalDateTime.of(2025, 2, 12, 13, 0), epic.getId());

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        taskManager.updateEpicStatus(epic.getId());
        Assertions.assertEquals(TaskStatus.DONE, epic.getStatus());
    }


    @Test
    public void testEpicStatusMixedNewAndDone() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        Epic epic = new Epic(1, "Epic 1", "Epic description");
        taskManager.createEpic(epic);

        Subtask subtask1 = new Subtask(1, "Subtask 1", "Description 1", TaskStatus.NEW, Duration.ofHours(1),
                LocalDateTime.of(2025, 2, 12, 12, 0), epic.getId());
        Subtask subtask2 = new Subtask(2, "Subtask 2", "Description 2", TaskStatus.DONE, Duration.ofHours(1),
                LocalDateTime.of(2025, 2, 12, 14, 0), epic.getId());
        Subtask subtask3 = new Subtask(3, "Subtask 3", "Description 3", TaskStatus.NEW, Duration.ofHours(1),
                LocalDateTime.of(2025, 2, 12, 16, 0), epic.getId());

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask3);

        taskManager.updateEpicStatus(epic.getId());
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }

    @Test
    public void testEpicStatusInProgress() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        Epic epic = new Epic(1, "Epic 1", "Epic description");
        taskManager.createEpic(epic);

        Subtask subtask1 = new Subtask(1, "Subtask 1", "Description 1", TaskStatus.IN_PROGRESS, Duration.ofHours(1),
                LocalDateTime.of(2025, 2, 12, 12, 0), epic.getId());
        Subtask subtask2 = new Subtask(2, "Subtask 2", "Description 2", TaskStatus.IN_PROGRESS, Duration.ofHours(1),
                LocalDateTime.of(2025, 2, 12, 13, 0), epic.getId());

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        taskManager.updateEpicStatus(epic.getId());
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }

}
