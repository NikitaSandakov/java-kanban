package test;

import org.junit.jupiter.api.Test;
import ru.scompany.trackerapp.model.Epic;
import ru.scompany.trackerapp.model.Subtask;
import ru.scompany.trackerapp.model.TaskStatus;
import ru.scompany.trackerapp.service.InMemoryHistoryManager;
import ru.scompany.trackerapp.service.InMemoryTaskManager;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class SubtaskTest {

    @Test
    public void testSubtaskEquality() {
        Epic epic = new Epic(1, "Epic 1", "Description of Epic");
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        taskManager.createEpic(epic);

        Subtask subtask1 = new Subtask(1, "Task 1", "Description 1", TaskStatus.NEW, Duration.ofHours(1), LocalDateTime.now(), epic.getId());
        Subtask subtask2 = new Subtask(1, "Task 1", "Description 1", TaskStatus.NEW, Duration.ofHours(1), LocalDateTime.now(), epic.getId());

        assertEquals(subtask1, subtask2);
    }

    @Test
    public void testSubtaskCannotBeItsOwnEpic() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        Epic epic = new Epic(1, "1 epic", "Description of epic");
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask(1, "Subtask", "Description of subtask", TaskStatus.NEW, Duration.ofHours(1), LocalDateTime.now(), epic.getId());

        assertNotEquals(epic, subtask);
    }

}