import java.util.ArrayList;
import java.util.List;

public class Subtask extends Task {
    private int epicId;

    public Subtask(int id, String description, TaskStatus status, int epicId) {
        super(id, description, status);
        this.epicId = this.epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", epicId=" + epicId +
                '}';
    }

}