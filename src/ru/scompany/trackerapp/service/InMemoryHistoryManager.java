package ru.scompany.trackerapp.service;

import ru.scompany.trackerapp.model.Epic;
import ru.scompany.trackerapp.model.Subtask;
import ru.scompany.trackerapp.model.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node> nodeMap = new HashMap<>();
    private Node head;
    private Node tail;

    @Override
    public void add(Task task) {
        if (nodeMap.containsKey(task.getId())) {
            Node oldNode = nodeMap.remove(task.getId());
            removeNode(oldNode);
        }
        Node newNode = linkLast(task);
        nodeMap.put(task.getId(), newNode);
    }

    @Override
    public void add(Subtask subtask) {
        if (nodeMap.containsKey(subtask.getId())) {
            Node oldNode = nodeMap.remove(subtask.getId());
            removeNode(oldNode);
        }
        Node newNode = linkLast(subtask);
        nodeMap.put(subtask.getId(), newNode);
    }

    @Override
    public void add(Epic epic) {
        if (nodeMap.containsKey(epic.getId())) {
            Node oldNode = nodeMap.remove(epic.getId());
            removeNode(oldNode);
        }
        Node newNode = linkLast(epic);
        nodeMap.put(epic.getId(), newNode);
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void remove(int id) {
        Node node = nodeMap.remove(id);
        if (node != null) {
            removeNode(node);
        }
    }

    public static class Node {
        Task task;
        Node prev;
        Node next;

        public Node(Task task) {
            this.task = task;
        }
    }

    private Node linkLast(Task task) {
        Node newNode = new Node(task);
        if (tail == null) {
            head = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
        }
        tail = newNode;
        return newNode;
    }

    private List<Task> getTasks() {
        List<Task> taskList = new ArrayList<>();
        Node current = head;

        while (current != null) {
            taskList.add(current.task);
            current = current.next;
        }

        return taskList;
    }

    private void removeNode(Node node) {
        if (node == null) return;
        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            head = node.next;
        }
        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            tail = node.prev;
        }
    }

}