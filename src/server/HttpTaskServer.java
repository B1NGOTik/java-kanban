package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import model.Epic;
import model.Subtask;
import model.Task;
import manager.TaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HttpTaskServer {

    private TaskManager taskManager;
    private HttpServer httpServer;

    public HttpTaskServer(TaskManager taskManager) throws IOException {

        this.taskManager = taskManager;
        createHTTPServer();
    }

    private static final String TASK = "task";
    private static final String EPIC = "epic";
    private static final String SUBTASK = "subtask";
    private static final String HISTORY = "history";

    private static final int PORT = 8080;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .create();

    public static Gson getGson() {
        return gson;
    }

    private void createHTTPServer() throws IOException {
        httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TaskHandler());
    }

    public void startHttpServer() {
        httpServer.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }

    public void stopHttpServer() {
        httpServer.stop(1);
    }

    private class TaskHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange h) throws IOException {
            String methodRequest = h.getRequestMethod();
            URI requestURI = h.getRequestURI();
            String path = requestURI.getPath();
            String[] splitPath = path.split("/");

            if (splitPath.length == 2 && methodRequest.equals("GET")) {
                handleGetPrioritizedTasks(h);
            }

            switch (methodRequest) {
                case "POST":
                    switch (splitPath[2]) {
                        case TASK -> handlePostAddUpdateTask(h);
                        case EPIC -> handlePostAddUpdateEpic(h);
                        case SUBTASK -> handlePostAddUpdateSubTask(h);
                        default -> outputStreamWrite(h, "Запрашиваемая страница не найдена", 404);
                    }
                    break;
                case "GET":
                    switch (splitPath[2]) {
                        case TASK -> handleGetTaskGetTasksMap(h);
                        case EPIC -> handleGetEpicGetEpicsMap(h);
                        case SUBTASK -> handleGetSubTaskGetSubTasksMap(h);
                        case HISTORY -> handleGetHistory(h);
                        default -> outputStreamWrite(h, "Запрашиваемая страница не найдена", 404);
                    }
                    break;
                case "DELETE":
                    switch (splitPath[2]) {
                        case TASK -> handleDeleteTask(h);
                        case EPIC -> handleDeleteEpic(h);
                        case SUBTASK -> handleDeleteSubTask(h);
                        default -> outputStreamWrite(h, "Запрашиваемая страница не найдена", 404);
                    }
                    break;
                default:
                    outputStreamWrite(h, "Неизвестный HTTP запрос", 405);
            }
        }

        int setId(HttpExchange httpExchange) {
            int id = Integer.parseInt(httpExchange.getRequestURI().toString()
                    .split("\\?")[1].split("=")[1]);
            return id;
        }

        void outputStreamWrite(HttpExchange h, String response, int code) throws IOException {
            h.sendResponseHeaders(code, 0);
            try (OutputStream os = h.getResponseBody()) {
                os.write(response.getBytes());
            }
        }

        private String readText(HttpExchange h) throws IOException {
            return new String(h.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
        }

        public void handleGetPrioritizedTasks(HttpExchange h) throws IOException {
            if (!taskManager.getPrioritizedTasks().isEmpty()) {
                outputStreamWrite(h, gson.toJson(taskManager.getPrioritizedTasks()), 200);
            } else {
                outputStreamWrite(h, "Отсортированный список задач не найден в базе.", 404);
            }
        }

        public void handlePostAddUpdateTask(HttpExchange h) throws IOException {
            String body = readText(h);
            if (body.isEmpty()) {
                outputStreamWrite(h, "Ничего не передано.", 400);
                return;
            }
            Task task = gson.fromJson(body, Task.class);
            Integer idTask = task.getId();
            if (idTask == null) {
                taskManager.addTask(task);
                outputStreamWrite(h, String.format("Создали новую задачу с Id: %d", task.getId()), 200);
            } else {
                if (taskManager.getAllTasks().contains(task)) {
                    taskManager.updateTask(task);
                    outputStreamWrite(h, String.format("Обновили задачу с Id: %d", idTask), 200);
                } else {
                    outputStreamWrite(h, String.format("Задачи с Id %d нет в базе.", idTask), 404);
                }
            }
        }

        public void handlePostAddUpdateEpic(HttpExchange h) throws IOException {
            String body = readText(h);
            if (body.isEmpty()) {
                outputStreamWrite(h, "Ничего не передано.", 400);
                return;
            }
            Epic epic = gson.fromJson(body, Epic.class);
            Integer idEpic = epic.getId();
            if (idEpic == null) {
                taskManager.addEpic(epic);
                outputStreamWrite(h, String.format("Создали новый эпик с Id: %d", epic.getId()), 200);
            } else {
                if (taskManager.getAllEpics().contains(epic)) {
                    taskManager.updateEpic(epic);
                    outputStreamWrite(h, String.format("Обновили эпик с Id: %d", idEpic), 200);
                } else {
                    outputStreamWrite(h, String.format("Эпика с Id %d нет в базе.", idEpic), 404);
                }
            }
        }

        public void handlePostAddUpdateSubTask(HttpExchange h) throws IOException {
            String body = readText(h);
            if (body.isEmpty()) {
                outputStreamWrite(h, "Ничего не передано.", 400);
                return;
            }
            Subtask subtask = gson.fromJson(body, Subtask.class);
            Integer idSubtask = subtask.getId();
            if (idSubtask == null) {
                if (taskManager.getAllEpics().contains(taskManager.getEpicById(subtask.getParentEpicId()))) {
                    taskManager.addSubtask(subtask);
                    outputStreamWrite(h, String.format("Создали новую подзадачу с Id: %d", subtask.getId()), 200);
                } else {
                    outputStreamWrite(h, String.format("Эпика с Id %d нет в базе.", subtask.getParentEpicId()), 404);
                }
            } else {
                if (taskManager.getAllSubtasks().contains(subtask)) {
                    taskManager.updateSubtask(subtask);
                    outputStreamWrite(h, String.format("Обновили подзадачу с Id: %d", idSubtask), 200);
                } else {
                    outputStreamWrite(h, String.format("Подзадачи с Id %d нет в базе.", idSubtask), 404);
                }
            }
        }

        public void handleGetTaskGetTasksMap(HttpExchange h) throws IOException {
            if (h.getRequestURI().getQuery() != null) {
                int idTask = setId(h);
                if (taskManager.getAllTasks().contains(taskManager.getTaskById(idTask))) {
                    Task task = taskManager.getTaskById(idTask);
                    outputStreamWrite(h, gson.toJson(task), 200);
                } else {
                    outputStreamWrite(h, String.format("Задача с Id %d не найдена в базе.", idTask), 404);
                }
            } else {
                if (!taskManager.getAllTasks().isEmpty()) {
                    outputStreamWrite(h, gson.toJson(taskManager.getAllTasks()), 200);
                } else {
                    outputStreamWrite(h, "Список задач не найден в базе.", 404);
                }
            }
        }

        public void handleGetEpicGetEpicsMap(HttpExchange h) throws IOException {
            if (h.getRequestURI().getQuery() != null) {
                int idEpic = setId(h);
                if (taskManager.getAllEpics().contains(taskManager.getEpicById(idEpic))) {
                    Epic epic = taskManager.getEpicById(idEpic);
                    outputStreamWrite(h, gson.toJson(epic), 200);
                } else {
                    outputStreamWrite(h, String.format("Эпик с Id %d не найден в базе.", idEpic), 404);
                }
            } else {
                if (!taskManager.getAllEpics().isEmpty()) {
                    outputStreamWrite(h, gson.toJson(taskManager.getAllEpics()), 200);
                } else {
                    String message = "Список эпиков не найден в базе.";
                    outputStreamWrite(h, message, 404);
                }
            }
        }

        public void handleGetSubTaskGetSubTasksMap(HttpExchange h) throws IOException {
            if (h.getRequestURI().getQuery() != null) {
                int idSubTask = setId(h);
                if (taskManager.getAllSubtasks().contains(taskManager.getSubtaskById(idSubTask))) {
                    Subtask subTask = taskManager.getSubtaskById(idSubTask);
                    outputStreamWrite(h, gson.toJson(subTask), 200);
                } else {
                    outputStreamWrite(h, String.format("Подзадача с Id %d не найдена в базе.", idSubTask), 404);
                }
            } else {
                if (!taskManager.getAllSubtasks().isEmpty()) {
                    outputStreamWrite(h, gson.toJson(taskManager.getAllSubtasks()), 200);
                } else {
                    outputStreamWrite(h, "Список подзадач не найден в базе.", 404);
                }
            }
        }

        public void handleGetHistory(HttpExchange h) throws IOException {
            if (!taskManager.getHistory().isEmpty()) {
                outputStreamWrite(h, gson.toJson(taskManager.getHistory()), 200);
            } else {
                outputStreamWrite(h, "Cписок просмотра задач пуст.", 404);
            }
        }

        public void handleDeleteSubTask(HttpExchange h) throws IOException {
            if (h.getRequestURI().getQuery() != null) {
                int idSubtask = setId(h);
                if (taskManager.getAllSubtasks().contains(taskManager.getSubtaskById(idSubtask))) {
                    Subtask subTask = taskManager.getAllSubtasks().get(idSubtask);
                    taskManager.removeSubtaskById(subTask.getId());
                    outputStreamWrite(h, "Удалили " + gson.toJson(subTask), 200);
                } else {
                    outputStreamWrite(h, String.format("Подзадача с Id %d не найдена в базе.", idSubtask), 404);
                }
            } else {
                handleDeleteTasksEpicsSubTasksMap(h);
            }
        }

        public void handleDeleteEpic(HttpExchange h) throws IOException {
            if (h.getRequestURI().getQuery() != null) {
                int idEpic = setId(h);
                if (taskManager.getAllEpics().contains(taskManager.getEpicById(idEpic))) {
                    Epic epic = taskManager.getAllEpics().get(idEpic);
                    taskManager.removeEpicById(epic.getId());
                    outputStreamWrite(h, "Удалили " + gson.toJson(epic), 200);
                } else {
                    outputStreamWrite(h, String.format("Эпик с Id %d не найден в базе.", idEpic), 404);
                }
            } else {
                handleDeleteTasksEpicsSubTasksMap(h);
            }
        }

        public void handleDeleteTask(HttpExchange h) throws IOException {
            if (h.getRequestURI().getQuery() != null) {
                int idTask = setId(h);
                if (taskManager.getAllTasks().contains(taskManager.getTaskById(idTask))) {
                    Task task = taskManager.getAllTasks().get(idTask);
                    taskManager.removeTaskById(task.getId());
                    outputStreamWrite(h, "Удалили " + gson.toJson(task), 200);
                } else {
                    outputStreamWrite(h, String.format("Задача с Id %d не найдена в базе.", idTask), 404);
                }
            } else {
                handleDeleteTasksEpicsSubTasksMap(h);
            }
        }

        public void handleDeleteTasksEpicsSubTasksMap(HttpExchange h) throws IOException {
            if (!taskManager.getAllTasks().isEmpty() ||
                    !taskManager.getAllEpics().isEmpty() ||
                    !taskManager.getAllSubtasks().isEmpty()) {
                taskManager.removeAllTasks();
                taskManager.removeAllSubtasks();
                taskManager.removeAllEpics();
                outputStreamWrite(h, "Все задачи удалены.", 200);
            } else {
                outputStreamWrite(h, "Задач для удаления нет.", 404);
            }
        }
    }

    private static class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
        private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy(HH:mm)");

        @Override
        public void write(final JsonWriter jsonWriter, final LocalDateTime localDateTime) throws IOException {
            if (localDateTime == null) {
                jsonWriter.nullValue();
                return;
            }
            jsonWriter.value(localDateTime.format(DATE_TIME_FORMATTER));
        }

        @Override
        public LocalDateTime read(final JsonReader jsonReader) throws IOException {
            return LocalDateTime.parse(jsonReader.nextString(), DATE_TIME_FORMATTER);
        }
    }
}