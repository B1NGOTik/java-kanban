package model;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Subtask> subtasks;

    public Epic(String name, String description, Status status) {
        super(name, description, status);
        subtasks = new ArrayList<>();
    }

    public Epic(String name, String description, Status status, Integer id) {
        super(name, description, status, id);
        subtasks = new ArrayList<>();
    }

    public void addSubtaskInEpic(Subtask subtask) {
        subtasks.add(subtask);
    }

    public void removeSubtask(Subtask subtask) {
        subtasks.remove(subtask);
    }

    public void removeAllSubtasks() {
        subtasks.clear();
    }

    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }
}
