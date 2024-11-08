import java.util.HashMap;

public class TaskManager {
    private static int taskId = 0;
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();

    public static int updateTaskId() {
        return ++taskId;
    }

    // Метод для добавления задачи
    public void addTask(String description, TaskStatus status) {
        int id = updateTaskId();
        Task newTask = new Task(id, description, status);
        tasks.put(id, newTask);
    }

    public void addEpic(String description, TaskStatus status) {
        int id = updateTaskId();
        Epic newEpic = new Epic(id, description, status);
        epics.put(id, newEpic);

    }

    public void addSubtask(String description, TaskStatus status, int epicId) {
        if (!epics.containsKey(epicId)) {
            throw new IllegalArgumentException("Epic with ID " + epicId + " does not exist.");
        }
        int id = updateTaskId();
        Subtask newSubtask = new Subtask(id, description, status, epicId);
        subtasks.put(id, newSubtask);
        epics.get(epicId).addSubtaskId(id);
    }

}