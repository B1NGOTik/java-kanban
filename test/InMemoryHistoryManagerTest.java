import manager.InMemoryTaskManager;


import model.*;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class InMemoryHistoryManagerTest {
    InMemoryTaskManager taskManager = new InMemoryTaskManager();

    @Test
    public void getHistoryShouldReturnListOf10Tasks() {
        for (int i = 0; i < 11; i++) {
            taskManager.addTask(new Task("Название", "Описание", Status.NEW));
        }

        List<Task> tasks = taskManager.getAllTasks();
        for (Task task : tasks) {
            taskManager.getTaskById(task.getId());
        }

        List<Task> list = taskManager.getHistory();
        assertEquals(10, list.size());
    }

    @Test
    public void getHistoryShouldReturnOldTaskAfterUpdate() {
        Task task1 = new Task("Переехать", "Переехать на новую квартиру", Status.NEW);
        taskManager.addTask(task1);
        taskManager.getTaskById(task1.getId());
        taskManager.updateTask(new Task("Накормить кошку", "Найти, погладить и покормить кошку", Status.NEW, task1.getId()));
        List<Task> tasks = taskManager.getHistory();
        Task oldTask = tasks.getFirst();
        assertEquals(task1.getName(), oldTask.getName());
        assertEquals(task1.getDescription(), oldTask.getDescription());

    }

    @Test
    public void getHistoryShouldReturnOldEpicAfterUpdate() {
        Epic epic1 = new Epic("Сделать проект", "Шаги по сдаче проекта", Status.NEW);
        taskManager.addEpic(epic1);
        taskManager.getEpicById(epic1.getId());
        taskManager.updateEpic(new Epic("Новое имя", "Новое описание", Status.NEW, epic1.getId()));
        List<Task> epics = taskManager.getHistory();
        Epic oldEpic = (Epic) epics.getFirst();
        assertEquals(epic1.getName(), oldEpic.getName());
        assertEquals(epic1.getDescription(), oldEpic.getDescription());
    }

    @Test
    public void getHistoryShouldReturnOldSubtaskAfterUpdate() {
        Epic epic1 = new Epic("Поступление в университет", "Шаги при поступлении в университет", Status.NEW);
        taskManager.addEpic(epic1);
        Subtask subtask1 = new Subtask("Собрать документы", "Собрать требуемые документы", Status.NEW,
                epic1.getId());
        taskManager.addSubtask(subtask1);
        taskManager.getSubtaskById(subtask1.getId());
        taskManager.updateSubtask(new Subtask("Новое имя", "Новое описание",
                Status.NEW, epic1.getId(), subtask1.getId()));
        List<Task> subtasks = taskManager.getHistory();
        Subtask oldSubtask = (Subtask) subtasks.getFirst();
        assertEquals(subtask1.getName(), oldSubtask.getName());
        assertEquals(subtask1.getDescription(), oldSubtask.getDescription());
    }
}