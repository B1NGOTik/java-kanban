package manager;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected HashMap<Integer, Task> tasks = new HashMap<>();
    protected HashMap<Integer, Epic> epics = new HashMap<>();
    protected HashMap<Integer, Subtask> subtasks = new HashMap<>();
    protected TreeSet<Task> prioritizedTasks;
    protected Integer idMaker = 1;
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private static final String OVERLAPPING_MESSAGE = "Задача пересекается во времени с другой";

    @Override
    public void addTask(Task task) {
        if (!isTaskOverlapping(task)) {
            int id = idMaker++;
            task.setId(id);
            tasks.put(id, task);
        } else {
            System.out.println(OVERLAPPING_MESSAGE);
        }
    }

    @Override
    public void addEpic(Epic epic) {
        int id = idMaker++;
        epic.setId(id);
        epics.put(id, epic);
    }

    @Override
    public void addSubtask(Subtask subtask) {
        if (!isTaskOverlapping(subtask)) {
            int id = idMaker++;
            subtask.setId(id);
            subtasks.put(id, subtask);
            epics.get(subtask.getParentEpicId()).addSubtaskInEpic(subtask);
            updateEpic(subtask.getParentEpicId());
        } else {
            System.out.println(OVERLAPPING_MESSAGE);
        }
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
        updateEpic(subtasks.get(id).getParentEpicId());
    }

    @Override
    public void updateTask(Task newTask) {
        if (!isTaskOverlapping(newTask)) {
            if (tasks.containsKey(newTask.getId()) && (tasks.get(newTask.getId()) != null)) {
                tasks.put(newTask.getId(), newTask);
            }
        } else {
            System.out.println(OVERLAPPING_MESSAGE);
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
        if (!isTaskOverlapping(newSubtask)) {
            Subtask originalSubtask = subtasks.get(newSubtask.getId());
            if (originalSubtask != null && originalSubtask.getParentEpicId() == newSubtask.getParentEpicId()) {
                if (epics.get(newSubtask.getParentEpicId()) != null) {
                    subtasks.put(newSubtask.getId(), newSubtask);
                    updateEpic(newSubtask.getParentEpicId());
                }
            }
        } else {
            System.out.println(OVERLAPPING_MESSAGE);
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
    public void updateEpic(int id) {
        long newStatusCounter = epics.get(id).getSubtasks().stream()
                .filter(subtask -> subtask.getStatus() == Status.DONE)
                .count();
        long doneStatusCounter = epics.get(id).getSubtasks().stream()
                .filter(subtask -> subtask.getStatus() == Status.NEW)
                .count();

        if (doneStatusCounter == epics.get(id).getSubtasks().size()) {
            epics.get(id).setStatus(Status.DONE);
        } else if (newStatusCounter == epics.get(id).getSubtasks().size()) {
            epics.get(id).setStatus(Status.NEW);
        } else {
            epics.get(id).setStatus(Status.IN_PROGRESS);
        }
    }

    public void updatePrioritizedTasks() {
        prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime, Comparator.nullsLast(Comparator.naturalOrder())));
        prioritizedTasks.addAll(getAllSubtasks());
        prioritizedTasks.addAll(getAllTasks());
    }

    public TreeSet<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    public boolean isTasksOverlap(Task task1, Task task2) {
        if (task1.getStartTime() == null || task2.getStartTime() == null) {
            return false;
        }
        return task1.getStartTime().isBefore(task2.getEndTime()) && task2.getStartTime().isBefore(task1.getEndTime());
    }

    public boolean isTaskOverlapping(Task taskToCheck) {
        if (getPrioritizedTasks() != null) {
            return getPrioritizedTasks().stream()
                    .filter(task -> !task.getId().equals(taskToCheck.getId()))
                    .anyMatch(task -> isTasksOverlap(taskToCheck, task));
        }
        return false;
    }
}
