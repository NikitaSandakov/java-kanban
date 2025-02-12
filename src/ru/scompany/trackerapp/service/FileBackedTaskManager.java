package ru.scompany.trackerapp.service;

import ru.scompany.trackerapp.model.Epic;
import ru.scompany.trackerapp.model.Subtask;
import ru.scompany.trackerapp.model.Task;
import ru.scompany.trackerapp.model.TaskStatus;
import ru.scompany.trackerapp.model.TaskType;
import ru.scompany.trackerapp.model.ManagerSaveException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.util.List;


public class FileBackedTaskManager extends InMemoryTaskManager {
    private static final String CSV_HEADER = "id,type,name,status,description,epic\n";
    private final File file;

    public FileBackedTaskManager(File file, HistoryManager historyManager) {
        super(historyManager);
        this.file = file;
    }

    public void save() {
        try (Writer writer = new FileWriter(file, false)) {
            writer.write(CSV_HEADER);
            for (Task task : getAllTask()) {
                writer.write(toString(task) + "\n");
            }
            for (Epic epic : getAllEpic()) {
                writer.write(toString(epic) + "\n");
            }
            for (Subtask subtask : getAllSubtask()) {
                writer.write(toString(subtask) + "\n");
            }

            writer.write("\n");
            writer.write(historyToString(historyManager));

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения данных в файл: " + file.getName(), e);
        }
    }


    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file, new InMemoryHistoryManager());

        try {
            List<String> lines = Files.readAllLines(file.toPath());
            if (lines.isEmpty()) {
                return manager;
            }

            int i = 1;
            while (i < lines.size() && !lines.get(i).isEmpty()) {
                Task task = fromString(lines.get(i));
                if (task instanceof Epic) {
                    manager.getEpics().put(task.getId(), (Epic) task);
                } else if (task instanceof Subtask) {
                    manager.getSubtasks().put(task.getId(), (Subtask) task);
                } else {
                    manager.getTasks().put(task.getId(), task);
                }
                i++;
            }

            if (i < lines.size()) {
                i++;
            }

            if (i < lines.size() && !lines.get(i).isEmpty()) {
                String historyLine = lines.get(i);
                restoreHistoryFromString(manager, historyLine);
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка чтения данных из файла: " + file.getName(), e);
        }

        return manager;
    }


    private String toString(Task task) {
        StringBuilder sb = new StringBuilder();
        sb.append(task.getId()).append(",")
                .append(getTaskType(task)).append(",")
                .append(task.getName()).append(",")
                .append(task.getStatus()).append(",")
                .append(task.getDescription()).append(",");

        if (task instanceof Subtask) {
            sb.append(((Subtask) task).getEpicId());
        }

        return sb.toString();
    }

    private TaskType getTaskType(Task task) {
        if (task instanceof Epic) {
            return TaskType.EPIC;
        } else if (task instanceof Subtask) {
            return TaskType.SUBTASK;
        } else {
            return TaskType.TASK;
        }
    }

    private static Task fromString(String value) {
        String[] fields = value.split(",");

        if (fields.length < 5) {
            throw new IllegalArgumentException("Некорректный формат строки: " + value);
        }

        int id = Integer.parseInt(fields[0]);
        TaskType type = TaskType.valueOf(fields[1]);
        String name = fields[2];
        TaskStatus status = TaskStatus.valueOf(fields[3]);
        String description = fields[4];

        switch (type) {
            case TASK:
                return new Task(id, name, description, status);
            case EPIC:
                return new Epic(id, name, description);
            case SUBTASK:
                if (fields.length < 6) {
                    throw new IllegalArgumentException("Ошибка в данных подзадачи: " + value);
                }
                int epicId = Integer.parseInt(fields[5]);
                return new Subtask(id, name, description, status, epicId);
            default:
                throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
        }
    }


    private static String historyToString(HistoryManager historyManager) {
        List<Task> history = historyManager.getHistory();

        StringBuilder sb = new StringBuilder();
        for (Task task : history) {
            sb.append(task.getId()).append(",");
        }
        if (!history.isEmpty()) {
            sb.deleteCharAt(sb.length() - 1);
        }


        return sb.toString();
    }


    private static void restoreHistoryFromString(FileBackedTaskManager manager, String historyLine) {
        String[] ids = historyLine.split(",");
        for (String id : ids) {
            int taskId = Integer.parseInt(id);
            if (manager.getTasks().containsKey(taskId)) {
                manager.historyManager.add(manager.getTasks().get(taskId));
            } else if (manager.getEpics().containsKey(taskId)) {
                manager.historyManager.add(manager.getEpics().get(taskId));
            } else if (manager.getSubtasks().containsKey(taskId)) {
                manager.historyManager.add(manager.getSubtasks().get(taskId));
            }
        }
    }

}