import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private static List<Integer> subtasksIds;

    public Epic(int id, String description, TaskStatus status) {
        super(id, description, status);
        this.subtasksIds = new ArrayList<>();
    }

    public List<Integer> getSubtasksId() {
        return subtasksIds;
    }

    public void addSubtaskId(int subtaskId) {
        subtasksIds.add(subtaskId);
    }

    public void removeSubtaskId(int subtaskId) {
        subtasksIds.remove(Integer.valueOf(subtaskId));
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", subtaskIds=" + subtasksIds +
                '}';
    }

}