public class Main {

    public static void main(String[] args) {

        Task task = new Task(1,"Первая тестовая задача", TaskStatus.NEW);
        Task task1 = new Task( 2,"Вторая тестовая задача", TaskStatus.IN_PROGRESS);
        Epic epic = new Epic(3, "Первый эпик", TaskStatus.NEW);

        // Отображаем все задачи
        System.out.println("Все задачи:");
        System.out.println(task);
        System.out.println(epic);
    }
}