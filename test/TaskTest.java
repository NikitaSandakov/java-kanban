package test;

import java.time.Duration;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import ru.scompany.trackerapp.model.Task;
import ru.scompany.trackerapp.model.TaskStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TaskTest {

    @Test
    public void testTaskEquality() {
        LocalDateTime startTime = LocalDateTime.now(); // Текущее время
        Duration duration = Duration.ofHours(1); // 1 час

        Task task1 = new Task(1, "Task 1", "Description 1", TaskStatus.NEW, duration, startTime);
        Task task2 = new Task(1, "Task 1", "Description 1", TaskStatus.NEW, duration, startTime);

        assertEquals(task1, task2);
    }
}
