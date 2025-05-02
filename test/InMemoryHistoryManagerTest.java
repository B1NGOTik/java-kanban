import manager.InMemoryTaskManager;


import model.*;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InMemoryHistoryManagerTest {
    InMemoryTaskManager taskManager = new InMemoryTaskManager();

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
        Subtask subtask1 = new Subtask("Собрать документы", "Собрать требуемые документы", Status.NEW, LocalDateTime.now().plusMinutes(40), Duration.ofMinutes(10),
                epic1.getId());
        taskManager.addSubtask(subtask1);
        taskManager.getSubtaskById(subtask1.getId());
        taskManager.updateSubtask(new Subtask("Новое имя", "Новое описание",
                Status.NEW, epic1.getId(), LocalDateTime.now().plusMinutes(60), Duration.ofMinutes(10), subtask1.getId()));
        List<Task> subtasks = taskManager.getHistory();
        Subtask oldSubtask = (Subtask) subtasks.getFirst();
        assertEquals(subtask1.getName(), oldSubtask.getName());
        assertEquals(subtask1.getDescription(), oldSubtask.getDescription());
    }

    @Test
    public void getHistoryShouldntReturnRepeatedOldTasks() {
        Task task1 = new Task("Сдача проекта", "Сдать проект в Яндекс.Практикуме", Status.NEW);
        taskManager.addTask(task1);
        Task task2 = new Task("Приготовить ужин", "Приготовить на ужин лазанью", Status.NEW);
        taskManager.addTask(task2);
        Task task3 = new Task("Искупать обезьянку", "Искупать обезьянку Мари", Status.NEW);
        taskManager.addTask(task3);
        taskManager.getTaskById(1);
        taskManager.getTaskById(3);
        taskManager.getTaskById(2);
        taskManager.getTaskById(1);
        taskManager.getTaskById(3);
        List<Task> history = taskManager.getHistory();
        List<Task> expectedHistory = new ArrayList<>(Arrays.asList(task2, task1, task3));
        assertEquals(history, expectedHistory);
    }

    @Test
    public void getHistoryShouldntReturnDeletedTasks() {
        Task task1 = new Task("Name1", "Description1", Status.NEW);
        taskManager.addTask(task1);
        Task task2 = new Task("Name2", "Description2", Status.NEW);
        taskManager.addTask(task2);
        Task task3 = new Task("Name3", "Description3", Status.NEW);
        taskManager.addTask(task3);
        taskManager.getTaskById(1);
        taskManager.getTaskById(2);
        taskManager.getTaskById(3);
        taskManager.removeTaskFromHistory(2);
        taskManager.removeTaskFromHistory(1);
        List<Task> history = taskManager.getHistory();
        List<Task> expectedHistory = new ArrayList<>(Arrays.asList(task3));
        assertEquals(history, expectedHistory);
    }
}