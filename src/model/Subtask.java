package model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private final Integer parentEpicId;

    public Subtask(String name, String description, Status status, Integer parentEpicId) {
        super(name, description, status);
        this.parentEpicId = parentEpicId;
    }

    public Subtask(String name, String description, Status status, Integer id, Integer parentEpicId) {
        super(name, description, status, id);
        this.parentEpicId = parentEpicId;
    }

    public Subtask(String name, String description, Status status, LocalDateTime startTime, Duration duration, Integer parentEpicId) {
        super(name, description, status, startTime, duration);
        this.parentEpicId = parentEpicId;
    }

    public Subtask(String name, String description, Status status, Integer id, LocalDateTime startTime, Duration duration, Integer parentEpicId) {
        super(name, description, status, id, startTime, duration);
        this.parentEpicId = parentEpicId;
    }

    public Integer getParentEpicId() {
        return parentEpicId;
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "name='" + name + '\'' +
                ", status=" + status +
                ", id=" + id +
                ", start=" + startTime +
                ", end=" + getEndTime() +
                ", epicid=" + parentEpicId +
                '}';
    }
}
