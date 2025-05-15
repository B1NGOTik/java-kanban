package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Subtask> subtasks = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(String name, String description, Status status) {
        super(name, description, status);
        subtasks = new ArrayList<>();
        startTime = LocalDateTime.now();
        endTime = LocalDateTime.now();
        duration = Duration.ZERO;
    }

    public Epic(String name, String description, Status status, Integer id) {
        super(name, description, status, id);
        subtasks = new ArrayList<>();
    }

    public Epic(String name, String description, Status status, Integer id, LocalDateTime startTime, Duration duration, LocalDateTime endTime) {
        super(name, description, status, id, startTime, duration);
        this.endTime = endTime;
    }

    public void addSubtaskInEpic(Subtask subtask) {
        subtasks.add(subtask);
        updateTime();
    }

    public void removeSubtask(Subtask subtask) {
        subtasks.remove(subtask);
        updateTime();
    }

    public void removeAllSubtasks() {
        subtasks.clear();
    }

    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }

    public void calculateStartTime() {
        for (Subtask subtask : subtasks) {
            if (subtask.startTime.isBefore(startTime)) {
                setStartTime(subtask.getStartTime());
            }
        }
    }

    public void calculateDuration() {
        for (Subtask subtask : subtasks) {
            this.setDuration(this.duration.plus(subtask.duration));
        }
    }

    public void calculateEndTime() {
        for (Subtask subtask : subtasks) {
            if (subtask.getEndTime().isAfter(this.endTime)) {
                endTime = subtask.getEndTime();
            }
        }
    }

    public void updateTime() {
        calculateStartTime();
        calculateDuration();
        calculateEndTime();
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "name='" + name + '\'' +
                ", status=" + status +
                ", id=" + id +
                ", start=" + startTime +
                ", end=" + endTime +
                '}';
    }
}