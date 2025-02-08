package ru.scompany.trackerapp;

import ru.scompany.trackerapp.model.*;
import ru.scompany.trackerapp.service.Managers;
import ru.scompany.trackerapp.service.HistoryManager;
import ru.scompany.trackerapp.service.TaskManager;
import ru.scompany.trackerapp.service.InMemoryTaskManager;

import java.io.IOException;


public class Main {

    public static void main(String[] args) {
        HistoryManager  historyManager = Managers.getDefaultHistory();
        TaskManager<Task> manager = new InMemoryTaskManager(historyManager);

        Task task1 = new Task(0, "Name of the first task", "Task 1", TaskStatus.NEW);
        Task task2 = new Task(0, "Name of the second epic", "Task 2", TaskStatus.NEW);
        manager.createTask(task1);
        manager.createTask(task2);

        Epic epic1 = new Epic(0, "Name of the first epic", "Epic 1");
        manager.createEpic(epic1);
        Subtask subtask1 = new Subtask(0, "Name of the first subtask", "Subtask 1 for Epic 1",
                TaskStatus.NEW, epic1.getId());
        Subtask subtask2 = new Subtask(0, "Name of the second epic", "Subtask 2 for Epic 1",
                TaskStatus.NEW, epic1.getId());
        try {
            manager.createSubtask(subtask1);
            manager.createSubtask(subtask2);
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при создании подзадачи", e);
        }

        Epic epic2 = new Epic(0, "Name of the second epic", "Epic 2");
        manager.createEpic(epic2);
        Subtask subtask3 = new Subtask(0, "Name of the third subtask", "Subtask 3 for Epic 2",
                TaskStatus.NEW, epic2.getId());
        try {
            manager.createSubtask(subtask3);
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при создании подзадачи", e);
        }

        System.out.println("\nСписок всех задач:");
        System.out.println(manager.getAllTask());
        System.out.println("\nСписок всех эпиков:");
        System.out.println(manager.getAllEpic());
        System.out.println("\nСписок всех подзадач:");
        System.out.println(manager.getAllSubtask());

        task1.setStatus(TaskStatus.IN_PROGRESS);
        manager.updateTask(task1);
        subtask1.setStatus(TaskStatus.DONE);
        manager.updateTask(subtask1);
        subtask2.setStatus(TaskStatus.IN_PROGRESS);
        manager.updateTask(subtask2);
        subtask3.setStatus(TaskStatus.DONE);
        manager.updateTask(subtask3);

        System.out.println("\nСписок всех задач после изменения статусов:");
        System.out.println(manager.getAllTask());
        System.out.println("\nСписок всех эпиков после изменения статусов:");
        System.out.println(manager.getAllEpic());
        System.out.println("\nСписок всех подзадач после изменения статусов:");
        System.out.println(manager.getAllSubtask());

        manager.removeTaskById(task1.getId());
        manager.removeTaskById(epic1.getId());

        System.out.println("\nСписок всех задач после удаления:");
        System.out.println(manager.getAllTask());
        System.out.println("\nСписок всех эпиков после удаления:");
        System.out.println(manager.getAllEpic());
        System.out.println("\nСписок всех подзадач после удаления:");
        System.out.println(manager.getAllSubtask());


        System.out.println("\nПроверка из практикума");
        System.out.println("Задачи:");
        for (Task task : manager.getAllTask()) {
            System.out.println(task);
        }

        System.out.println("Эпики:");
        for (Epic epic : manager.getAllEpic()) {
            System.out.println(epic);

            for (Subtask subtask : manager.getSubtasksOfEpic(epic.getId())) {
                System.out.println("--> " + subtask);
            }
        }

        System.out.println("Подзадачи:");
        for (Subtask subtask : manager.getAllSubtask()) {
            System.out.println(subtask);
        }

        manager.getTask(2);

        System.out.println("История:");
        for (Task task : (historyManager).getHistory()) {
            System.out.println(task);
        }
    }

}