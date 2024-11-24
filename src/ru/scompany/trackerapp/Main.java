package ru.scompany.trackerapp;

import ru.scompany.trackerapp.model.*;
import ru.scompany.trackerapp.service.*;

public class Main {

    public static void main(String[] args) {

        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
        Task task1 = new Task(0, "Name of the first task", "Task 1", TaskStatus.NEW);
        Task task2 = new Task(0, "Name of the second epic", "Task 2", TaskStatus.NEW);
        inMemoryTaskManager.createTask(task1);
        inMemoryTaskManager.createTask(task2);

        Epic epic1 = new Epic(0, "Name of the first epic", "Epic 1");
        inMemoryTaskManager.createEpic(epic1);
        Subtask subtask1 = new Subtask(0, "Name of the first subtask", "Subtask 1 for Epic 1", TaskStatus.NEW, epic1.getId());
        Subtask subtask2 = new Subtask(0, "Name of the second epic", "Subtask 2 for Epic 1", TaskStatus.NEW, epic1.getId());
        inMemoryTaskManager.createSubtask(subtask1);
        inMemoryTaskManager.createSubtask(subtask2);

        Epic epic2 = new Epic(0, "Name of the second epic", "Epic 2");
        inMemoryTaskManager.createEpic(epic2);
        Subtask subtask3 = new Subtask(0, "Name of the third subtask", "Subtask 3 for Epic 2", TaskStatus.NEW, epic2.getId());
        inMemoryTaskManager.createSubtask(subtask3);

        System.out.println("\nСписок всех задач:");
        System.out.println(inMemoryTaskManager.getAllTask());
        System.out.println("\nСписок всех эпиков:");
        System.out.println(inMemoryTaskManager.getAllEpic());
        System.out.println("\nСписок всех подзадач:");
        System.out.println(inMemoryTaskManager.getAllSubtask());

        task1.setStatus(TaskStatus.IN_PROGRESS);
        inMemoryTaskManager.updateTask(task1);
        subtask1.setStatus(TaskStatus.DONE);
        inMemoryTaskManager.updateTask(subtask1);
        subtask2.setStatus(TaskStatus.IN_PROGRESS);
        inMemoryTaskManager.updateTask(subtask2);
        subtask3.setStatus(TaskStatus.DONE);
        inMemoryTaskManager.updateTask(subtask3);

        System.out.println("\nСписок всех задач после изменения статусов:");
        System.out.println(inMemoryTaskManager.getAllTask());
        System.out.println("\nСписок всех эпиков после изменения статусов:");
        System.out.println(inMemoryTaskManager.getAllEpic());
        System.out.println("\nСписок всех подзадач после изменения статусов:");
        System.out.println(inMemoryTaskManager.getAllSubtask());

        inMemoryTaskManager.removeTaskById(task1.getId());
        inMemoryTaskManager.removeTaskById(epic1.getId());

        System.out.println("\nСписок всех задач после удаления:");
        System.out.println(inMemoryTaskManager.getAllTask());
        System.out.println("\nСписок всех эпиков после удаления:");
        System.out.println(inMemoryTaskManager.getAllEpic());
        System.out.println("\nСписок всех подзадач после удаления:");
        System.out.println(inMemoryTaskManager.getAllSubtask());
    }
}