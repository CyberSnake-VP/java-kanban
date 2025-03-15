package tasktracker.httpserver.handlers;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import tasktracker.enumeration.Endpoint;
import tasktracker.exceptions.IntersectionsException;
import tasktracker.exceptions.JsonErrorConverter;
import tasktracker.manager.TaskManager;
import tasktracker.tasks.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeParseException;
import java.util.*;

public class TaskHandler extends BaseHandler {

    public TaskHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    protected void handleDeleteById(HttpExchange exchange) throws IOException {
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

    @Override
    protected void handleCreateOrUpdate(HttpExchange exchange) throws IOException, JsonErrorConverter {
        byte[] bytes = exchange.getRequestBody().readAllBytes();
        String jsonBody = new String(bytes, StandardCharsets.UTF_8);
        try {
            Task task = jsonMapper.fromJson(jsonBody, Task.class);
            if(Objects.isNull(task)) {
                throw new JsonErrorConverter("Задача не может быть создана пустой, необходимо заполнить хотя бы одно поле name.");
            }
            if (task.getId() == 0) {
                /* Проверяем корректность полей в запросе тела Json, задачу можно создать, если верно указать поле name
                 * Думаю, чтобы создать задачу, нужно хотя бы указать ее название... */
                JsonElement je = JsonParser.parseString(jsonBody);
                JsonElement name = je.getAsJsonObject().get("name");
                if (Objects.isNull(name)) {
                    throw new JsonErrorConverter("Неверно указано поле name. Задача должна иметь название. Проверьте корректность ввода.");
                }
                /* Десереализуем задачу, проверяем id, если его нет, создаем новую задачу, исп-ем поля из объекта.
                 * Обязательные поле имя, статус будет NEW через конструктор, если есть поля начала и продолжительности
                 * то они тоже будут инициализированны через конструктор */
                Task taskWithId = manager.createTask(new Task(task.getName(), task.getDescription(), task.getStartTime(), task.getDuration()));
                String jsonTaskWithId = jsonMapper.toJson(taskWithId);
                sendResponse(exchange, jsonTaskWithId, CREATED);
                return;
            }

            /** Для обновления задачи, определяю какие поля были указаны в json запросе, обновляем задачу.
             * Если не фильтровать поля запроса, то вместе с их обновлением, все остальные поля становятся null
             * Парсим тело запроса на элементы JSON, после чего фильтруем на null, записываем значения оставшихся полей
             * в список. Далее получаем актуальную задачу по id, после чего меняем ей значение нужных полей и записываем
             * все изменения через метод updateTask */
            ArrayList<String> jsonElementList = new ArrayList<>();
            JsonElement jsonElement = JsonParser.parseString(jsonBody);
            jsonElement.getAsJsonObject().entrySet().stream()
                    .filter(Objects::nonNull)
                    .map(Map.Entry::getKey)
                    .filter(id -> !id.equals("id"))
                    .forEach(jsonElementList::add);

            Task taskForUpdate = manager.getTask(task.getId());
            if (Objects.isNull(taskForUpdate)) {
                sendResponse(exchange, "Задача с указанным ID не найдена", NOTE_FOUND);
                return;
            }

            for (String element : jsonElementList) {
                switch (element) {
                    case "name" -> taskForUpdate.setName(task.getName());
                    case "description" -> taskForUpdate.setDescription(task.getDescription());
                    case "status" -> {
                        taskForUpdate.setStatus(task.getStatus());      // Статус задачи важен и не должен быть null
                        if (Objects.isNull(taskForUpdate.getStatus())) {
                            throw new JsonErrorConverter("Неверный формат статуса задачи. Формат статуса: NEW, IN_PROGRESS, DONE. ");
                        }
                    }
                    case "startTime" -> taskForUpdate.setStartTime(task.getStartTime());
                    case "duration" -> taskForUpdate.setDuration(task.getDuration());
                    default -> throw new JsonErrorConverter("Неверно указаны поля для внесения изменений задачи. " +
                            "Проверьте правильность ввода. ");
                }
            }

            Task epdateTask = manager.updateTask(taskForUpdate);
            String jsonUpdatedTask = jsonMapper.toJson(epdateTask);
            sendResponse(exchange, jsonUpdatedTask, CREATED);

        } catch (JsonSyntaxException e) {
            throw new JsonErrorConverter("Не корректное тело запроса. Проверьте правильность составления тела JSON запроса.");
        } catch (DateTimeParseException e) {
            throw new JsonErrorConverter("Не корректное введение формата даты и времени. Введите дату и время в формате dd-mm-yyyy|hh:mm");
        } catch (NumberFormatException e) {
            throw new JsonErrorConverter("Не корректное введение продолжительности минут. Вероятно введенное значение не является числом.");
        } catch (IntersectionsException e) {
            sendResponse(exchange, e.getMessage(), NOT_ACCEPTABLE);
        }
    }

    @Override
    protected void handleGetById(HttpExchange exchange) throws IOException {
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

    @Override
    protected void handleGetList(HttpExchange exchange) throws IOException {
        List<Task> taskList = manager.getTaskList();
        String jsonListTask = jsonMapper.toJson(taskList);
        sendResponse(exchange, jsonListTask, OK);

    }

    @Override
    protected void getEndpoint(String path, String method, HttpExchange exchange) throws IOException, JsonErrorConverter {
        String[] elements = path.split("/");
        switch (method) {
            case "GET":
                if (elements.length == 2 && elements[1].equals("tasks")) {
                    handleGetList(exchange);
                }
                if (elements.length == 3 && elements[1].equals("tasks") && isNumber(elements[2])) {
                    handleGetById(exchange);
                }
            case "POST":
                if (elements.length == 2 && elements[1].equals("tasks")) {
                    handleCreateOrUpdate(exchange);
                }
            case "DELETE":
                if (elements.length == 3 && elements[1].equals("tasks") && isNumber(elements[2])) {
                    handleDeleteById(exchange);
                }
            default:
                sendResponse(exchange, "Неверно указан адрес, проверьте составление запроса.", NOTE_FOUND);
        }
    }
}
