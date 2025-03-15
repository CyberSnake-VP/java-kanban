package tasktracker.httpserver.handlers;

import com.sun.net.httpserver.HttpExchange;
import tasktracker.exceptions.JsonErrorConverter;
import tasktracker.manager.TaskManager;
import tasktracker.tasks.Task;

import java.io.IOException;
import java.util.List;

public class HistoryHandler extends BaseHandler {

    public HistoryHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    protected void handleGetList(HttpExchange exchange) throws IOException {
        List<Task> history = manager.getHistory();
        String jsonHistory = jsonMapper.toJson(history);
        sendResponse(exchange, jsonHistory, OK);
    }

    @Override
    protected void runProcess(String path, String method, HttpExchange exchange) throws IOException, JsonErrorConverter {
        String[] elements = path.split("/");

        if (method.equals("GET")) {
            if (elements.length == 2 && elements[1].equals("history")) {
                handleGetList(exchange);
            } else {
                sendResponse(exchange, "Неверно указан адрес, проверьте составление запроса.", NOTE_FOUND);
            }
        } else {
            sendResponse(exchange, "Выберите правильный метод, для получения приоритетного списка задач.", METHOD_NOT_ALLOWED);
        }
    }
}
