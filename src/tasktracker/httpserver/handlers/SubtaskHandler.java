package tasktracker.httpserver.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import tasktracker.enumeration.Endpoint;
import tasktracker.exceptions.JsonErrorConverter;
import tasktracker.manager.TaskManager;
import tasktracker.tasks.Epic;
import tasktracker.tasks.Subtask;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;

public class SubtaskHandler extends BaseTaskHandler {

    public SubtaskHandler(TaskManager manager, Gson jsonMapper) {
        super(manager, jsonMapper);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());
            switch (endpoint) {
                case GET_SUBTASK: {
                    handleGetSubtaskList(exchange);
                    break;
                }
                case GET_SUBTASK_ID: {
                    handleGetSubtaskById(exchange);
                    break;
                }
                case POST_SUBTASK: {
                    handleCreateOrUpdateSubtask(exchange);
                    break;
                }
                case DELETE_SUBTASK: {
                    handleDeleteSubtask(exchange);
                }
                case UNKNOWN: {
                    sendResponse(exchange, "Не верно указан адрес, проверьте составление запроса.", NOTE_FOUND);
                }
            }
        } catch (IOException | JsonErrorConverter e) {
            sendResponse(exchange, "Внутренняя ошибка сервера. " + e.getMessage(), SERVER_ERROR);
        }

    }

    private void handleCreateOrUpdateSubtask(HttpExchange exchange) throws IOException, JsonErrorConverter {
        try {
            byte[] bytes = exchange.getRequestBody().readAllBytes();
            String jsonBody = new String(bytes, StandardCharsets.UTF_8);

            /** Чтобы создать подзадачу, нужны поля name и description и epicId */
            JsonElement je = JsonParser.parseString(jsonBody);
            JsonElement name = je.getAsJsonObject().get("name");
            JsonElement description = je.getAsJsonObject().get("description");
            JsonElement epicId = je.getAsJsonObject().get("epicId");
            if (Objects.isNull(name) || Objects.isNull(description) || Objects.isNull(epicId)) {
                throw new JsonErrorConverter("Поля name, description и id эпика обязательны для внесения подзадачи. " +
                        "Проверьте корректность ввода.");
            }

            Subtask subtask = jsonMapper.fromJson(jsonBody, Subtask.class);
            Epic epic = manager.getEpic(subtask.getEpicId());
            if (Objects.isNull(epic)) {
                sendResponse(exchange, "Эпик с указанным ID для внесения подзадачи не найден.", NOTE_FOUND);
                return;
            }
            if (subtask.getId() == 0) {
                Subtask subtaskWithId = manager.createSubtask(new Subtask(subtask.getName(), subtask.getDescription(), epic, subtask.getStartTime(), subtask.getDuration()));
                String jsonSubtaskWithId = jsonMapper.toJson(subtaskWithId);
                sendResponse(exchange, jsonSubtaskWithId, CREATED);
                return;
            }

            Subtask subtaskForUpdate = new Subtask(subtask.getName(), subtask.getDescription(), epic, subtask.getStartTime(), subtask.getDuration());
            subtaskForUpdate.setId(subtask.getId());

            Subtask epdateSubtask = manager.updateSubtask(subtaskForUpdate);
            if (Objects.nonNull(epdateSubtask)) {
                String jsonUpdatedEpic = jsonMapper.toJson(epdateSubtask);
                sendResponse(exchange, jsonUpdatedEpic, CREATED);
            } else  {
                sendResponse(exchange, "Подзадача с указанным ID не найдена", NOTE_FOUND);
            }

        } catch (JsonSyntaxException e) {
            throw new JsonErrorConverter("Не корректное тело запроса. Проверьте правильность составления тела JSON запроса.");
        } catch (DateTimeParseException e) {
            throw new JsonErrorConverter("Не корректное введение формата даты и времени. Введите дату и время в формате dd-mm-yyyy|hh:mm");
        } catch (NumberFormatException e) {
            throw new JsonErrorConverter("Не корректное введение продолжительности минут. Вероятно введенное значение не является числом.");
        }
    }

    private void handleDeleteSubtask(HttpExchange exchange) throws IOException, JsonErrorConverter {
        String idStr = exchange.getRequestURI().getPath().split("/")[2];
        int id = Integer.parseInt(idStr);
        Subtask subtask = manager.deleteSubtask(id);
        if (Objects.isNull(subtask)) {
            sendResponse(exchange, "Подзадача с указанным ID не найдена", NOTE_FOUND);
            return;
        }
        String epicJson = jsonMapper.toJson(subtask);
        sendResponse(exchange, epicJson, OK);
    }

    private void handleGetSubtaskById(HttpExchange exchange) throws IOException, JsonErrorConverter {
        String idStr = exchange.getRequestURI().getPath().split("/")[2];
        int id = Integer.parseInt(idStr);
        Subtask subtask = manager.getSubtask(id);
        if (Objects.isNull(subtask)) {
            sendResponse(exchange, "Подзадача с указанным ID не найдена", NOTE_FOUND);
            return;
        }
        String epicJson = jsonMapper.toJson(subtask);
        sendResponse(exchange, epicJson, OK);
    }

    private void handleGetSubtaskList(HttpExchange exchange) throws IOException, JsonErrorConverter {
        List<Subtask> list = manager.getSubtaskList();
        String jsonList = jsonMapper.toJson(list);
        sendResponse(exchange, jsonList, OK);
    }

    private Endpoint getEndpoint(String path, String requestMethod) {
        String[] elements = path.split("/");

        switch (requestMethod) {
            case "GET":
                if (elements.length == 2 && elements[1].equals("subtasks")) {
                    return Endpoint.GET_SUBTASK;
                }
                if (elements.length == 3 && elements[1].equals("subtasks") && isNumber(elements[2])) {
                    return Endpoint.GET_SUBTASK_ID;
                }
            case "POST":
                if (elements.length == 2 && elements[1].equals("subtasks")) {
                    return Endpoint.POST_SUBTASK;
                }
            case "DELETE":
                if (elements.length == 3 && elements[1].equals("subtasks") && isNumber(elements[2])) {
                    return Endpoint.DELETE_SUBTASK;
                }
            default:
                return Endpoint.UNKNOWN;
        }
    }
}
