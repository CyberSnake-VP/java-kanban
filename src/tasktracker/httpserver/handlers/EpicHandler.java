package tasktracker.httpserver.handlers;

import com.google.gson.*;
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


public class EpicHandler extends BaseTaskHandler {
    public EpicHandler(TaskManager manager, Gson jsonMapper) {
        super(manager, jsonMapper);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());
            switch (endpoint) {
                case GET_EPICS: {
                    handleGetEpicList(exchange);
                    break;
                }
                case GET_EPIC_ID: {
                    handleGetEPicById(exchange);
                    break;
                }
                case GET_SUBTASK_EPIC: {
                    handleGetSubtaskList(exchange);
                    break;
                }
                case POST_EPIC: {
                    handleCreateOrUpdateEpic(exchange);
                    break;
                }
                case DELETE_EPIC: {
                    handleDeleteEpic(exchange);
                }
                case UNKNOWN: {
                    sendResponse(exchange, "Не верно указан адрес, проверьте составление запроса.", NOTE_FOUND);
                }
            }
        } catch (IOException | JsonErrorConverter e) {
            sendResponse(exchange, "Внутренняя ошибка сервера. " + e.getMessage(), SERVER_ERROR);
        }
    }

    private void handleGetSubtaskList(HttpExchange exchange) throws IOException {
        String idStr = exchange.getRequestURI().getPath().split("/")[2];
        int id = Integer.parseInt(idStr);
        Epic epic = manager.getEpic(id);
        List<Subtask> subtasks = manager.getSubtaskListInEpic(epic);
        if (Objects.isNull(epic)) {
            sendResponse(exchange, "Epic с указанным ID не найден", NOTE_FOUND);
            return;
        }
        String subtaskJson = jsonMapper.toJson(subtasks);
        sendResponse(exchange, subtaskJson, OK);
    }

    private void handleCreateOrUpdateEpic(HttpExchange exchange) throws IOException, JsonErrorConverter {
        /** Создаем эпик, через получение нужных полей из json */
        try {
            byte[] bytes = exchange.getRequestBody().readAllBytes();
            String jsonBody = new String(bytes, StandardCharsets.UTF_8);

            /** Попробовал реализовать валидацию тела запроса, чтобы создать эпик, нужно два поля name и description */
            /** Посчитал, что создание эпика с полем name null не целесообразно...*/
            JsonElement je = JsonParser.parseString(jsonBody);
            JsonElement name = je.getAsJsonObject().get("name");
            if (Objects.isNull(name)) {
                throw new JsonErrorConverter("Неверно указаны epic поля name и description. Проверьте корректность ввода. ");
            }

            Epic epic = jsonMapper.fromJson(jsonBody, Epic.class);
            if (epic.getId() == 0) {
                Epic epicWithId = manager.createEpic(new Epic(epic.getName(), epic.getDescription()));
                String jsonEpicWithId = jsonMapper.toJson(epicWithId);
                sendResponse(exchange, jsonEpicWithId, CREATED);
                return;
            }
            /**Обновление эпика, необходимые поля для обновления, имя и описание и id*/
            Epic epicForUpdate = new Epic(epic.getName(), epic.getDescription());
            epicForUpdate.setId(epic.getId());
            Epic epdateEpic = manager.updateEpic(epicForUpdate);
            if (Objects.nonNull(epdateEpic)) {
                String jsonUpdatedEpic = jsonMapper.toJson(epdateEpic);
                sendResponse(exchange, jsonUpdatedEpic, CREATED);
                return;
            }
            sendResponse(exchange, "Эпик с указанным ID не найден", NOTE_FOUND);
        } catch (JsonSyntaxException e) {
            throw new JsonErrorConverter("Не корректное тело запроса. Проверьте правильность составления тела JSON запроса.");
        } catch (DateTimeParseException e) {
            throw new JsonErrorConverter("Не корректное введение формата даты и времени. Введите дату и время в формате dd-mm-yyyy|hh:mm");
        } catch (NumberFormatException e) {
            throw new JsonErrorConverter("Не корректное введение продолжительности минут. Вероятно введенное значение не является числом.");
        }
    }

    private void handleDeleteEpic(HttpExchange exchange) throws IOException {
        String idStr = exchange.getRequestURI().getPath().split("/")[2];
        int id = Integer.parseInt(idStr);
        Epic epic = manager.deleteEpic(id);
        if (Objects.isNull(epic)) {
            sendResponse(exchange, "Эпик с указанным ID не найден", NOTE_FOUND);
            return;
        }
        String epicJson = jsonMapper.toJson(epic);
        sendResponse(exchange, epicJson, OK);
    }

    private void handleGetEPicById(HttpExchange exchange) throws IOException {
        String idStr = exchange.getRequestURI().getPath().split("/")[2];
        int id = Integer.parseInt(idStr);
        Epic epic = manager.getEpic(id);
        if (Objects.isNull(epic)) {
            sendResponse(exchange, "Epic с указанным ID не найден", NOTE_FOUND);
            return;
        }
        String epicJson = jsonMapper.toJson(epic);
        sendResponse(exchange, epicJson, OK);
    }

    private void handleGetEpicList(HttpExchange exchange) throws IOException {
        List<Epic> list = manager.getEpicList();
        String jsonList = jsonMapper.toJson(list);
        sendResponse(exchange, jsonList, OK);
    }

    private Endpoint getEndpoint(String path, String requestMethod) {
        String[] elements = path.split("/");

        switch (requestMethod) {
            case "GET":
                if (elements.length == 2 && elements[1].equals("epics")) {
                    return Endpoint.GET_EPICS;
                }
                if (elements.length == 3 && elements[1].equals("epics") && isNumber(elements[2])) {
                    return Endpoint.GET_EPIC_ID;
                }
                if (elements.length == 4 && elements[1].equals("epics") && isNumber(elements[2]) && elements[3].equals("subtasks")) {
                    return Endpoint.GET_SUBTASK_EPIC;
                }
            case "POST":
                if (elements.length == 2 && elements[1].equals("epics")) {
                    return Endpoint.POST_EPIC;
                }
            case "DELETE":
                if (elements.length == 3 && elements[1].equals("epics") && isNumber(elements[2])) {
                    return Endpoint.DELETE_EPIC;
                }
            default:
                return Endpoint.UNKNOWN;
        }
    }
}
