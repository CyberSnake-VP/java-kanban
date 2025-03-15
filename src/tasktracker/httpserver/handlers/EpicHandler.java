package tasktracker.httpserver.handlers;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import tasktracker.exceptions.JsonErrorConverter;
import tasktracker.manager.TaskManager;
import tasktracker.tasks.Epic;
import tasktracker.tasks.Subtask;


import java.io.IOException;

import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;


public class EpicHandler extends BaseHandler {
    public EpicHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    protected void handleGetList(HttpExchange exchange) throws IOException {
        List<Epic> list = manager.getEpicList();
        String jsonList = jsonMapper.toJson(list);
        sendResponse(exchange, jsonList, OK);
    }

    @Override
    protected void handleGetById(HttpExchange exchange) throws IOException {
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

    @Override
    protected void handleCreateOrUpdate(HttpExchange exchange) throws IOException, JsonErrorConverter {
        /** Создаем эпик, через получение нужных полей из json */
        try {
            byte[] bytes = exchange.getRequestBody().readAllBytes();
            String jsonBody = new String(bytes, StandardCharsets.UTF_8);

            /** Попробовал реализовать валидацию тела запроса, чтобы создать эпик, нужно поле name */
            /** Посчитал, что создание эпика с полем name - null не целесообразно...*/
            JsonElement je = JsonParser.parseString(jsonBody);
            JsonElement name = je.getAsJsonObject().get("name");
            if (Objects.isNull(name)) {
                throw new JsonErrorConverter("Неверно указаны epic поле name. Проверьте корректность ввода. ");
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

    @Override
    protected void handleDeleteById(HttpExchange exchange) throws IOException {
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

    @Override
    protected void runProcess(String path, String method, HttpExchange exchange) throws IOException, JsonErrorConverter {
        String[] elements = path.split("/");
        boolean isBadPath = false;
        switch (method) {
            case "GET":
                if (elements.length == 2 && elements[1].equals("epics")) {
                    handleGetList(exchange);
                }
                if (elements.length == 3 && elements[1].equals("epics") && isNumber(elements[2])) {
                    handleGetById(exchange);
                }
                if (elements.length == 4 && elements[1].equals("epics") && isNumber(elements[2]) && elements[3].equals("subtasks")) {
                    handleGetSubtaskList(exchange);
                }
                isBadPath = true;
                break;
            case "POST":
                if (elements.length == 2 && elements[1].equals("epics")) {
                    handleCreateOrUpdate(exchange);
                }
                isBadPath = true;
                break;
            case "DELETE":
                if (elements.length == 3 && elements[1].equals("epics") && isNumber(elements[2])) {
                    handleDeleteById(exchange);
                }
        }
        if (isBadPath) {
            sendResponse(exchange, "Неверно указан адрес, проверьте составление запроса.", NOTE_FOUND);
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
}
