package manager;

import model.*;

import java.io.File;
import java.nio.file.Files;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.TreeSet;

import exception.ManagerSaveException;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    private static File saveFile;

    public FileBackedTaskManager(File file) {
        this.saveFile = file;
    }

    private void save() {
        try {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("id,type,name,status,description,start,duration,end,epic\n");

            /*for (Epic epic : getAllEpics()) {
                stringBuilder.append(toString(epic)).append("\n");
            }
            for (Subtask subtask : getAllSubtasks()) {
                stringBuilder.append(toString(subtask)).append("\n");
            }
            for (Task task : getAllTasks()) {
                stringBuilder.append(toString(task)).append("\n");
            }*/

            for (int i = 1; i < idMaker; i++) {
                if (epics.containsKey(i)) {
                    stringBuilder.append(toString(epics.get(i))).append("\n");
                } else if (tasks.containsKey(i)) {
                    stringBuilder.append(toString(tasks.get(i))).append("\n");
                } else if (subtasks.containsKey(i)) {
                    stringBuilder.append(toString(subtasks.get(i))).append("\n");
                }
            }

            Files.writeString(saveFile.toPath(), stringBuilder.toString());
            updatePrioritizedTasks();
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении в файл");
        }
    }

    private String toString(Task task) {
        String type = "TASK";
        String epicId = "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy.MM.dd|HH:mm");

        if (task instanceof Subtask) {
            type = "SUBTASK";
            epicId = String.valueOf(((Subtask) task).getParentEpicId());
        } else if (task instanceof Epic) {
            type = "EPIC";
        }
        return String.format("%d,%s,%s,%s,%s,%s,%d,%s,%s",
                task.getId(),
                type,
                task.getName(),
                task.getStatus(),
                task.getDescription(),
                task.getStartTime().format(formatter),
                task.getDuration().toMinutes(),
                task.getEndTime().format(formatter),
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
                        //manager.tasks.put(task.getId(), task);
                        manager.addTask(task);
                        break;
                    case EPIC:
                        //manager.epics.put(task.getId(), (Epic) task);
                        manager.addEpic((Epic) task);
                        break;
                    case SUBTASK:
                        //manager.subtasks.put(task.getId(), (Subtask) task);
                        manager.addSubtask((Subtask) task);
                        break;
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при загрузке из файла");
        }
        return manager;
    }

    private static Task fromString(String value) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy.MM.dd|HH:mm");
        String[] parts = value.split(",");

        int id = Integer.parseInt(parts[0]);
        TaskType type = TaskType.valueOf(parts[1]);
        String name = parts[2];
        Status status = Status.valueOf(parts[3]);
        String description = parts[4];
        LocalDateTime startTime = LocalDateTime.parse(parts[5], formatter);
        Duration duration = Duration.ofMinutes(Integer.parseInt(parts[6]));
        LocalDateTime endTime = LocalDateTime.parse(parts[7], formatter);

        switch (type) {
            case TASK:
                return new Task(name, description, status, id, startTime, duration);
            case EPIC:
                return new Epic(name, description, status, id, startTime, duration, endTime);
            case SUBTASK:
                int epicId = Integer.parseInt(parts[8]);
                return new Subtask(name, description, status, id, startTime, duration, epicId);
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
