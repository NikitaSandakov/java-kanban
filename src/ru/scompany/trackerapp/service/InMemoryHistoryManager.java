package ru.scompany.trackerapp.service;

import ru.scompany.trackerapp.model.Task;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private List<Task> history = new ArrayList<>();
    private DoublyLinkedList taskList = new DoublyLinkedList();
    private Map<Integer, Node> nodeMap = new HashMap<>();

    public void add(Task task) {
        for (int i = 0; i < history.size(); i++) {
            if (history.get(i).getId() == task.getId()) {
                history.set(i, task);
                return;
            }
        }

        history.add(task.copy());
        Node newNode = new Node(task);
        taskList.linkLast(task);
        nodeMap.put(task.getId(), newNode);
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(history);
    }

    @Override
    public void remove(int id) {
        Task taskToRemove = null;
        for (Task task : history) {
            if (task.getId() == id) {
                taskToRemove = task;
                break;
            }
        }

        if (taskToRemove != null) {
            history.remove(taskToRemove);
            Node node = nodeMap.remove(id);
            taskList.removeNode(node);
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

    public class DoublyLinkedList {
        private Node head;
        private Node tail;

        public void linkLast(Task task) {
            Node newNode = new Node(task);
            if (tail == null) {
                head = newNode;
            } else {
                tail.next = newNode;
                newNode.prev = tail;
            }
            tail = newNode;

        }

        public List<Task> getTasks() {
            List<Task> taskList = new ArrayList<>();

            Node current = head;
            while (current != null) {
                taskList.add(current.task);
                current = current.next;
            }
            return taskList;
        }

        public void removeNode(Node node) {
            if (node == null) {
                return;
            }

            if (node.prev != null) {
                node.prev.next = node.next;
            } else {
                taskList.head = node.next;
            }

            if (node.next != null) {
                node.next.prev = node.prev;
            } else {
                taskList.tail = node.prev;
            }

            if (history.contains(node.task)) {
                history.remove(node.task);
            }
        }
    }

}