import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private Integer idMaker = 1;

    public void addTask(Task task) {
        int id = idMaker++;
        task.setId(id);
        tasks.put(id, task);
    }

    public void addEpic(Epic epic) {
        int id = idMaker++;
        epic.setId(id);
        epics.put(id, epic);
    }

    public void addSubtask(Subtask subtask) {
        int id = idMaker++;
        subtask.setId(id);
        subtasks.put(id, subtask);
        epics.get(subtask.getParentEpicId()).addSubtaskInEpic(subtask);
    }

    public void removeAllTasks() {
        tasks.clear();
    }

    public void removeAllSubtasks() {
        subtasks.clear();
    }

    public void removeAllEpics() {
        removeAllSubtasks();
        epics.clear();
    }

    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public Task getTaskByID(int id) {
        return tasks.get(id);
    }

    public Epic getEpicByID(int id) {
        return epics.get(id);
    }

    public Subtask getSubtaskByID(int id) {
        return subtasks.get(id);
    }

    public ArrayList<Subtask> getEpicSubtasks(int epicId) {
        return epics.get(epicId).getSubtasks();
    }

    public void removeTaskByID(int id) {
        tasks.remove(id);
    }

    public void removeEpicByID(int id) {
        epics.remove(id);
    }

    public void removeSubtaskByID(int id) {
        epics.get(subtasks.get(id).getParentEpicId()).removeSubtask(subtasks.get(id));
        subtasks.remove(id);
    }

    public void updateTask(Task newTask) {
        if (tasks.containsKey(newTask.getId()) && (tasks.get(newTask.getId()) != null)) {
            tasks.put(newTask.getId(), newTask);
        }
    }

    public void updateEpic(Epic newEpic) {
        if (epics.containsKey(newEpic.getId()) && (epics.get(newEpic.getId()) != null)) {
            epics.put(newEpic.getId(), newEpic);
        }
    }

    public void updateSubtask(Subtask newSubtask) {
        Subtask originalSubtask = subtasks.get(newSubtask.getId());
        if (originalSubtask != null && originalSubtask.getParentEpicId() == newSubtask.getParentEpicId()) {
            if (epics.get(newSubtask.getParentEpicId()) != null) {
                subtasks.put(newSubtask.getId(), newSubtask);
                updateEpicStatus(newSubtask.getParentEpicId());
            }
        }
    }

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
