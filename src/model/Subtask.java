package model;

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

    public Integer getParentEpicId() {
        return parentEpicId;
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }
}
