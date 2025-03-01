package manager;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    void addTask(Task task);
    void addEpic(Epic epic);
    void addSubtask(Subtask subtask);

    void removeAllTasks();
    void removeAllSubtasks();
    void removeAllEpics();

    ArrayList<Task> getAllTasks();
    ArrayList<Subtask> getAllSubtasks();
    ArrayList<Epic> getAllEpics();

    Task getTaskByID(int id);
    Epic getEpicByID(int id);
    Subtask getSubtaskByID(int id);
    ArrayList<Subtask> getEpicSubtasks(int epicId);

    void removeTaskByID(int id);
    void removeEpicByID(int id);
    void removeSubtaskByID(int id);

    void updateTask(Task newTask);
    void updateEpic(Epic newEpic);
    void updateSubtask(Subtask newSubtask);

    List<Task> getHistory();

    void updateEpicStatus(int id);
}
