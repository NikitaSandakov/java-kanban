public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = new TaskManager();

        Task task1 = new Task(0, "Task 1", TaskStatus.NEW);
        Task task2 = new Task(0, "Task 2", TaskStatus.NEW);
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        Epic epic1 = new Epic(0, "Epic 1", TaskStatus.NEW);
        taskManager.createEpic(epic1);

        Subtask subtask1 = new Subtask(0, "Subtask 1 for Epic 1", TaskStatus.NEW, epic1.getId());
        Subtask subtask2 = new Subtask(0, "Subtask 2 for Epic 1", TaskStatus.NEW, epic1.getId());
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        Epic epic2 = new Epic(0, "Epic 2", TaskStatus.NEW);
        taskManager.createEpic(epic2);

        Subtask subtask3 = new Subtask(0, "Subtask 3 for Epic 2", TaskStatus.NEW, epic2.getId());
        taskManager.createSubtask(subtask3);

        System.out.println("Список всех задач:");
        System.out.println(taskManager.getAllTask());

        System.out.println("\nСписок всех эпиков:");
        System.out.println(taskManager.getAllEpic());

        System.out.println("\nСписок всех подзадач:");
        System.out.println(taskManager.getAllSubtask());

        task1.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateTask(task1);
        subtask1.setStatus(TaskStatus.DONE);
        taskManager.updateTask(subtask1);
        subtask2.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateTask(subtask2);
        subtask3.setStatus(TaskStatus.DONE);
        taskManager.updateTask(subtask3);

        System.out.println("Список всех задач после изменения статусов:");
        System.out.println(taskManager.getAllTask());

        System.out.println("\nСписок всех эпиков после изменения статусов:");
        System.out.println(taskManager.getAllEpic());

        System.out.println("\nСписок всех подзадач после изменения статусов:");
        System.out.println(taskManager.getAllSubtask());

        taskManager.removeTaskById(task1.getId());
        taskManager.removeTaskById(epic1.getId());

        System.out.println("Список всех задач после удаления:");
        System.out.println(taskManager.getAllTask());

        System.out.println("\nСписок всех эпиков после удаления:");
        System.out.println(taskManager.getAllEpic());

        System.out.println("\nСписок всех подзадач после удаления:");
        System.out.println(taskManager.getAllSubtask());

    }
}