package main;

import manager.*;

import java.io.File;

import model.*;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;


public class Main {
    public static void main(String[] args) {
        /*
        //Добавление задач
        taskManager.addTask();
        taskManager.addEpic();
        taskManager.addSubtask();

        //Получение всех задач
        taskManager.getAllTasks();
        taskManager.getAllEpics();
        taskManager.removeAllSubtasks();
        taskManager.getEpicSubtasks();

        //Удаление всех задач
        taskManager.removeAllTasks();
        taskManager.removeAllEpics();
        taskManager.removeAllSubtasks();

        //Получение по идентификатору
        taskManager.getTaskByID();
        taskManager.getEpicByID();
        taskManager.getSubtaskByID();

        //Удаление по идентификатору
        taskManager.removeTaskByID();
        taskManager.removeEpicByID();
        taskManager.removeSubtaskByID();

        //Обновление задач
        taskManager.updateTask();
        taskManager.updateEpic();
        taskManager.updateSubtask();
        */

        try {
            File file = File.createTempFile("tasks", ".csv");

            // Создаем менеджер и добавляем задачи
            FileBackedTaskManager manager = new FileBackedTaskManager(file);

            Task task1 = new Task("Task 1", "Description 1", Status.NEW, LocalDateTime.now().plusMinutes(67), Duration.ofMinutes(10));
            manager.addTask(task1);

            Task task2 = new Task("Task 2", "Description 2", Status.NEW, LocalDateTime.now().plusMinutes(1), Duration.ofMinutes(20));
            manager.addTask(task2);

            Epic epic1 = new Epic("Epic 1", "Description epic", Status.NEW);
            manager.addEpic(epic1);

            Subtask subtask1 = new Subtask("Subtask 1", "Description subtask", Status.DONE, LocalDateTime.now().plusMinutes(98), Duration.ofMinutes(5), epic1.getId());
            manager.addSubtask(subtask1);

            Subtask subtask2 = new Subtask("Subtask 2", "Description subtask", Status.DONE, LocalDateTime.now().plusMinutes(98), Duration.ofMinutes(15), epic1.getId());
            manager.addSubtask(subtask2);

            // Создаем новый менеджер из файла
            FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);

            // Проверяем, что задачи загрузились
            System.out.println("Tasks: " + loadedManager.getAllTasks());
            System.out.println("Epics: " + loadedManager.getAllEpics());
            System.out.println("Subtasks: " + loadedManager.getAllSubtasks());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}