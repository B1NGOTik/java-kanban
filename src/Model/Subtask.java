package Model;

public class Subtask extends Task {
    private final Integer parentEpicId;

    Subtask(String name, String description, Status status, Integer parentEpicId) {
        super(name, description, status);
        this.parentEpicId = parentEpicId;
    }

    public Integer getParentEpicId() {
        return parentEpicId;
    }
}
