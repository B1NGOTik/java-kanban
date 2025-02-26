package Model;

import java.util.Objects;

public class Task {
    private String name;
    private String description;
    private Status status;
    private Integer id;

    Task(String name, String description, Status status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        Task otherTask = (Task) o;
        return Objects.equals(this.name, otherTask.name) &&
                Objects.equals(this.description, otherTask.description) &&
                Objects.equals(this.status, otherTask.status) &&
                Objects.equals(this.id, otherTask.id);
    }

    @Override
    public String toString() {
        return "Model.Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", id=" + id +
                '}';
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + description.hashCode();
        result = 31 * result + id.hashCode();
        return result;
    }
}