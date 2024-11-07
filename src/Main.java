import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        System.out.println("Что вы хотите сделать?");
        printMenu();
        Scanner scanner = new Scanner(System.in);
        int command = scanner.nextInt();
        TaskManager taskManager = new TaskManager();

        switch (command) {
            case 1:
                System.out.println(taskManager.showAllTask()); // допилить
            case 2:
                System.out.println("Задачи удалены"); // допилить
            case 3:
                System.out.println("Вывод по идентификатору"); // допилить
            case 4:

        }


    }

    public static void printMenu() {
        System.out.println("1. Получение списка всех задач");
        System.out.println("2. Удаление всех задач");
        System.out.println("3. Получение по идентификатору");
        System.out.println("4. Создание. Сам объект должен передаваться в качестве параметра");
        System.out.println("5. Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.");
        System.out.println("6. Удаление по идентификатору");
    }

    public enum Type {
        NEW,
        IN_PROGRESS,
        DONE
    }
}