package tasktracker.httpserver.handlers;

import com.sun.net.httpserver.HttpExchange;
import tasktracker.enumeration.Endpoint;
import tasktracker.exceptions.JsonErrorConverter;
import tasktracker.manager.TaskManager;
import tasktracker.tasks.Task;

import java.io.IOException;
import java.util.List;

public class PrioritizedHandler extends BaseHandler {

    public PrioritizedHandler(TaskManager manager) {
        super(manager);
    }

//    @Override
//    public void handle(HttpExchange exchange) throws IOException {
//        try {
//            Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());
//            switch (endpoint) {
//                case GET_PRIORITIZED: {
//                    handleGetPrioritized(exchange);
//                    break;
//                }
//                case UNKNOWN: {
//                    sendResponse(exchange, "Не верно указан адрес, проверьте составление запроса.", NOTE_FOUND);
//                }
//            }
//        } catch (IOException | JsonErrorConverter e) {
//            sendResponse(exchange, "Внутренняя ошибка сервера. " + e.getMessage(), SERVER_ERROR);
//        }
//    }

    private void handleGetPrioritized(HttpExchange exchange) throws IOException, JsonErrorConverter {
        List<Task> prioritizedTasks = manager.getPrioritizedTasks();
        String jsonHistory = jsonMapper.toJson(prioritizedTasks);
        sendResponse(exchange, jsonHistory, OK);
    }

//    private Endpoint getEndpoint(String path, String requestMethod) {
//        String[] elements = path.split("/");
//
//        switch (requestMethod) {
//            case "GET":
//                if (elements.length == 2 && elements[1].equals("prioritized")) {
//                    return Endpoint.GET_PRIORITIZED;
//                }
//            default:
//                return Endpoint.UNKNOWN;
//        }
//    }
}
