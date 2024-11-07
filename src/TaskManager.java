public class TaskManager {
    public int updateTaskId() {
        int taskId = taskId++;
        return taskId;
    }

    public String showAllTask() {
        System.out.println("Задача 1.");
    }

}
