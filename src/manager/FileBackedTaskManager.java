package manager;

import model.*;

import java.io.File;
import java.nio.file.Files;
import java.io.IOException;

import exception.ManagerSaveException;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    private static File saveFile;

    public FileBackedTaskManager(File file) {
        this.saveFile = file;
    }

    private void save() {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("id,type,name,status,description,epic\n");

            for (Epic epic : getAllEpics()) {
                sb.append(toString(epic)).append("\n");
            }
            for (Subtask subtask : getAllSubtasks()) {
                sb.append(toString(subtask)).append("\n");
            }
            for (Task task : getAllTasks()) {
                sb.append(toString(task)).append("\n");
            }

            Files.writeString(saveFile.toPath(), sb.toString());
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении в файл");
        }
    }

    private String toString(Task task) {
        String type = "TASK";
        String epicId = "";

        if (task instanceof Subtask) {
            type = "SUBTASK";
            epicId = String.valueOf(((Subtask) task).getParentEpicId());
        } else if (task instanceof Epic) {
            type = "EPIC";
        }
        return String.format("%d,%s,%s,%s,%s,%s",
                task.getId(),
                type,
                task.getName(),
                task.getStatus(),
                task.getDescription(),
                epicId);
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        try {
            String content = Files.readString(saveFile.toPath());
            String[] lines = content.split("\n");
            for (int i = 1; i < lines.length; i++) {
                Task task = fromString(lines[i]);
                switch (task.getType()) {
                    case TASK:
                        manager.tasks.put(task.getId(),task);
                        break;
                    case EPIC:
                        manager.epics.put(task.getId(),(Epic) task);
                        break;
                    case SUBTASK:
                        manager.subtasks.put(task.getId(), (Subtask) task);
                        break;
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при загрузке из файла");
        }
        return manager;
    }

    private static Task fromString(String value) {
        String[] parts = value.split(",");

        int id = Integer.parseInt(parts[0]);
        TaskType type = TaskType.valueOf(parts[1]);
        String name = parts[2];
        Status status = Status.valueOf(parts[3]);
        String description = parts[4];

        switch(type) {
            case TASK:
                return new Task(name,description,status,id);
            case EPIC:
                return new Epic(name, description, status, id);
            case SUBTASK:
                int epicId = Integer.parseInt(parts[5]);
                return new Subtask(name, description,status,id,epicId);
            default:
                return null;
        }
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void removeTaskById(int id) {
        super.removeTaskById(id);
        save();
    }

    @Override
    public void removeEpicById(int id) {
        super.removeEpicById(id);
        save();
    }

    @Override
    public void removeSubtaskById(int id) {
        super.removeSubtaskById(id);
        save();
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
        save();
    }

}
