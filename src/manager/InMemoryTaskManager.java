package manager;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private Integer idMaker = 1;
    private final HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public void addTask(Task task) {
        int id = idMaker++;
        task.setId(id);
        tasks.put(id, task);
    }

    @Override
    public void addEpic(Epic epic) {
        int id = idMaker++;
        epic.setId(id);
        epics.put(id, epic);
    }

    @Override
    public void addSubtask(Subtask subtask) {
        int id = idMaker++;
        subtask.setId(id);
        subtasks.put(id, subtask);
        epics.get(subtask.getParentEpicId()).addSubtaskInEpic(subtask);
        updateEpicStatus(subtask.getParentEpicId());
    }

    @Override
    public void removeAllTasks() {
        tasks.clear();
    }

    @Override
    public void removeAllSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.removeAllSubtasks();
        }
    }

    @Override
    public void removeAllEpics() {
        removeAllSubtasks();
        epics.clear();
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public ArrayList<Subtask> getEpicSubtasks(int epicId) {
        return epics.get(epicId).getSubtasks();
    }

    @Override
    public void removeTaskById(int id) {
        tasks.remove(id);
    }

    @Override
    public void removeEpicById(int id) {
        epics.remove(id);
    }

    @Override
    public void removeSubtaskById(int id) {
        epics.get(subtasks.get(id).getParentEpicId()).removeSubtask(subtasks.get(id));
        subtasks.remove(id);
        updateEpicStatus(subtasks.get(id).getParentEpicId());
    }

    @Override
    public void updateTask(Task newTask) {
        if (tasks.containsKey(newTask.getId()) && (tasks.get(newTask.getId()) != null)) {
            tasks.put(newTask.getId(), newTask);
        }
    }

    @Override
    public void updateEpic(Epic newEpic) {
        if (epics.containsKey(newEpic.getId()) && (epics.get(newEpic.getId()) != null)) {
            epics.put(newEpic.getId(), newEpic);
        }
    }

    @Override
    public void updateSubtask(Subtask newSubtask) {
        Subtask originalSubtask = subtasks.get(newSubtask.getId());
        if (originalSubtask != null && originalSubtask.getParentEpicId() == newSubtask.getParentEpicId()) {
            if (epics.get(newSubtask.getParentEpicId()) != null) {
                subtasks.put(newSubtask.getId(), newSubtask);
                updateEpicStatus(newSubtask.getParentEpicId());
            }
        }
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(historyManager.getHistory());
    }

    public void removeTaskFromHistory(int id) {
        historyManager.remove(id);
    }

    @Override
    public void updateEpicStatus(int id) {
        int newStatusCounter = 0;
        int doneStatusCounter = 0;
        for (Subtask subtask : epics.get(id).getSubtasks()) {
            if (subtask.getStatus() == Status.DONE) {
                doneStatusCounter++;
            } else if (subtask.getStatus() == Status.NEW) {
                newStatusCounter++;
            }
        }
        if (doneStatusCounter == epics.get(id).getSubtasks().size()) {
            epics.get(id).setStatus(Status.DONE);
        } else if (newStatusCounter == epics.get(id).getSubtasks().size()) {
            epics.get(id).setStatus(Status.NEW);
        } else {
            epics.get(id).setStatus(Status.IN_PROGRESS);
        }
    }


}
