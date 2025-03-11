package tasktracker.httpserver.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpExchange;
import tasktracker.enumeration.Endpoint;
import tasktracker.exceptions.JsonErrorConverter;
import tasktracker.manager.TaskManager;
import tasktracker.tasks.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

public class TaskHandler extends BaseTaskHandler {

    public TaskHandler(TaskManager manager, Gson jsonMapper) {
        super(manager, jsonMapper);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        try {
            Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());
            switch (endpoint) {
                case GET_TASKS: {
                    handleGetTaskList(exchange);
                    break;
                }
                case GET_TASK_ID: {
                    handleGEtTaskById(exchange);
                    break;
                }
                case POST_TASK: {
                    handleCreateOrUpdateTask(exchange);
                    break;
                }
                case DELETE_TASK: {
                    handleDeleteTaskById(exchange);
                    break;
                }
                case UNKNOWN: {
                    sendResponse(exchange, "Неверно указан адрес, проверьте составление запроса.", NOTE_FOUND);
                }
            }
        } catch (IOException | JsonErrorConverter e) {
            sendResponse(exchange, "Внутренняя ошибка сервера. " + e.getMessage(), SERVER_ERROR);
        }
    }

    private void handleDeleteTaskById(HttpExchange exchange) throws IOException {
        String idStr = exchange.getRequestURI().getPath().split("/")[2];
        int id = Integer.parseInt(idStr);
        Task task = manager.deleteTask(id);
        if (Objects.isNull(task)) {
            sendResponse(exchange, "Задача с указанным ID не найдена", NOTE_FOUND);
            return;
        }
        String taskJson = jsonMapper.toJson(task);
        sendResponse(exchange, taskJson, OK);

    }

    private void handleCreateOrUpdateTask(HttpExchange exchange) throws IOException, JsonErrorConverter{
        byte[] bytes = exchange.getRequestBody().readAllBytes();
        String jsonBody = new String(bytes, StandardCharsets.UTF_8);
        try {
            JsonElement je = JsonParser.parseString(jsonBody);
            JsonElement name = je.getAsJsonObject().get("name");
            JsonElement description = je.getAsJsonObject().get("description");
            if(Objects.isNull(name) || Objects.isNull(description)) {
                throw new JsonErrorConverter("Неверно указаны epic поля name и description. Проверьте корректность ввода. ");
            }

            Task task = jsonMapper.fromJson(jsonBody, Task.class);
            if (task.getId() == 0) {
                Task taskWithId = manager.createTask(task);
                String jsonTaskWithId = jsonMapper.toJson(taskWithId);
                sendResponse(exchange, jsonTaskWithId, CREATED);
                return;
            }
            Task updateTask = manager.updateTask(task);
            String jsonUpdatedTask = jsonMapper.toJson(updateTask);
            sendResponse(exchange, jsonUpdatedTask, CREATED);
        } catch (JsonSyntaxException e) {
            throw new JsonErrorConverter("Не корректное тело запроса. Проверьте правильность составления тела JSON запроса.");
        }
    }

    private void handleGEtTaskById(HttpExchange exchange) throws IOException {
        String idStr = exchange.getRequestURI().getPath().split("/")[2];
        int id = Integer.parseInt(idStr);
        Task task = manager.getTask(id);
        if (Objects.isNull(task)) {
            sendResponse(exchange, "Задача с указанным ID не найдена", NOTE_FOUND);
            return;
        }
        String taskJson = jsonMapper.toJson(task);
        sendResponse(exchange, taskJson, OK);
    }

    private void handleGetTaskList(HttpExchange exchange) throws IOException {
        List<Task> taskList = manager.getTaskList();
        String jsonListTask = jsonMapper.toJson(taskList);
        sendResponse(exchange, jsonListTask, OK);

    }

    private Endpoint getEndpoint(String stringPath, String requestMethod) {
        String[] elements = stringPath.split("/");

        switch (requestMethod) {
            case "GET":
                if (elements.length == 2 && elements[1].equals("tasks")) {
                    return Endpoint.GET_TASKS;
                }
                if (elements.length == 3 && elements[1].equals("tasks") && isNumber(elements[2])) {
                    return Endpoint.GET_TASK_ID;
                }
            case "POST":
                if (elements.length == 2 && elements[1].equals("tasks")) {
                    return Endpoint.POST_TASK;
                }
            case "DELETE":
                if(elements.length == 3 && elements[1].equals("tasks") && isNumber(elements[2])) {
                    return Endpoint.DELETE_TASK;
                }
            default:
                return Endpoint.UNKNOWN;
        }
    }
}
