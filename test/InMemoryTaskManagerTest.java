import manager.InMemoryTaskManager;

import model.*;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest {
    @Test
    public void AnyTasksShouldBeAddedToListsAndGotBack() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        taskManager.addTask(new Task("Задача1", "Описание задачи1", Status.NEW));
        taskManager.addTask(new Task("Задача2", "Описание задачи2", Status.NEW));
        taskManager.addEpic(new Epic("Эпик1", "Описание эпика1", Status.NEW));
        taskManager.addEpic(new Epic("Эпик2", "Описание эпика2", Status.NEW));
        taskManager.addSubtask(new Subtask("Подзадача1", "Описание подзадачи1", Status.NEW, 3));
        taskManager.addSubtask(new Subtask("Подзадача2", "Описание подзадачи2", Status.NEW, 3));
        taskManager.addSubtask(new Subtask("Подзадача3", "Описание подзадачи3", Status.NEW, 4));

        assertEquals("Описание задачи2", taskManager.getTaskById(2).getDescription());

        assertEquals("Описание эпика2", taskManager.getEpicById(4).getDescription());

        assertEquals("Описание подзадачи1", taskManager.getSubtaskById(5).getDescription());

        assertEquals("Описание подзадачи3", taskManager.getSubtaskById(7).getDescription());
    }
}