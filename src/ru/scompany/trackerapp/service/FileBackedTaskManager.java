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
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private static final String CSV_HEADER = "id,type,name,status,description,duration,startTime,epic\n";
    private final File file;

    public FileBackedTaskManager(File file, HistoryManager historyManager) {
        super();
        this.file = file;
        this.historyManager = historyManager;
    }

    public void save() {
        try (Writer writer = new FileWriter(file, false)) {
            writer.write(CSV_HEADER);

            getAllTask().stream()
                    .map(this::toString)
                    .forEach(taskString -> writeToFile(writer, taskString));

            getAllEpic().stream()
                    .map(this::toString)
                    .forEach(epicString -> writeToFile(writer, epicString));

            getAllSubtask().stream()
                    .map(this::toString)
                    .forEach(subtaskString -> writeToFile(writer, subtaskString));

            writer.write("\n");
            writer.write(historyToString(historyManager));

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения данных в файл: " + file.getName(), e);
        }
    }

    private void writeToFile(Writer writer, String line) {
        try {
            writer.write(line + "\n");
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка записи в файл", e);
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file, new InMemoryHistoryManager());

        try {
            List<String> lines = Files.readAllLines(file.toPath());

            if (lines.isEmpty() || lines.stream().allMatch(String::isBlank)) {
                return manager;
            }

            lines.stream()
                    .skip(1)
                    .takeWhile(line -> !line.trim().isEmpty())
                    .map(FileBackedTaskManager::fromString)
                    .forEach(task -> {
                        if (task instanceof Epic) {
                            manager.getEpics().put(task.getId(), (Epic) task);
                        } else if (task instanceof Subtask) {
                            manager.getSubtasks().put(task.getId(), (Subtask) task);
                        } else {
                            manager.getTasks().put(task.getId(), task);
                        }
                    });

            lines.stream()
                    .skip(lines.indexOf("") + 1)
                    .findFirst()
                    .ifPresent(historyLine -> restoreHistoryFromString(manager, historyLine));

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка чтения данных из файла: " + file.getName(), e);
        } catch (IllegalArgumentException e) {
            throw new ManagerSaveException("Ошибка при обработке данных из файла: " + file.getName(), e);
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

        Duration duration = task.getDuration();
        if (duration != null) {
            sb.append(duration.toMinutes()).append(",");
        } else {
            sb.append("0,");
        }

        sb.append(task.getStartTime());

        if (task instanceof Subtask) {
            sb.append(",").append(((Subtask) task).getEpicId());
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

    private static Task fromString(String str) {
        String[] fields = str.split(",");
        int id = Integer.parseInt(fields[0]);
        String name = fields[2];
        String description = fields[4];
        TaskStatus status = TaskStatus.valueOf(fields[3]);

        String durationStr = fields[5];
        Duration duration = parseDuration(durationStr);

        String startTimeStr = fields[6];
        LocalDateTime startTime = (startTimeStr.equals("null") || startTimeStr.isEmpty())
                ? LocalDateTime.now()
                : LocalDateTime.parse(startTimeStr);

        if (fields[1].equals("TASK")) {
            return new Task(id, name, description, status, duration, startTime);
        } else if (fields[1].equals("EPIC")) {
            return new Epic(id, name, description);
        } else {
            return new Subtask(id, name, description, status, duration, startTime, Integer.parseInt(fields[7]));
        }
    }

    private static Duration parseDuration(String durationStr) {
        try {
            long minutes = Long.parseLong(durationStr);
            return Duration.ofMinutes(minutes);
        } catch (NumberFormatException e) {
            return Duration.ZERO;
        }
    }

    private static String historyToString(HistoryManager historyManager) {
        return historyManager.getHistory().stream()
                .map(task -> String.valueOf(task.getId()))
                .collect(Collectors.joining(","));
    }


    private static void restoreHistoryFromString(FileBackedTaskManager manager, String historyLine) {
        Stream.of(historyLine.split(","))
                .map(Integer::parseInt)
                .map(taskId -> {
                    if (manager.getTasks().containsKey(taskId)) {
                        return manager.getTasks().get(taskId);
                    } else if (manager.getEpics().containsKey(taskId)) {
                        return manager.getEpics().get(taskId);
                    } else {
                        return manager.getSubtasks().get(taskId);
                    }
                })
                .filter(Objects::nonNull)
                .forEach(manager.historyManager::add);
    }

}