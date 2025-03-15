package tasktracker.httpserver.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import tasktracker.exceptions.JsonErrorConverter;
import tasktracker.httpserver.adapters.DurationAdapter;
import tasktracker.httpserver.adapters.LocalDateTimeAdapter;
import tasktracker.manager.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;


public abstract class BaseHandler implements HttpHandler {

    protected TaskManager manager;
    protected Gson jsonMapper;
    protected static final int OK = 200;
    protected static final int CREATED = 201;
    protected static final int NOTE_FOUND = 404;
    protected static final int NOT_ACCEPTABLE = 406;
    protected static final int SERVER_ERROR = 500;
    protected static final int METHOD_NOT_ALLOWED = 405;

    public BaseHandler(TaskManager manager) {
        this.manager = manager;
        this.jsonMapper = new GsonBuilder()
                .serializeNulls()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            switch (method) {
                case "GET":
                    processGet(exchange, path);
                    break;
                case "POST":
                    processPost(exchange, path);
                    break;
                case "DELETE":
                    processDelete(exchange, path);
                    break;
                default:
                    sendResponse(exchange, "Данный метод не предусмотрен.", METHOD_NOT_ALLOWED);
            }
        } catch (IOException | JsonErrorConverter e) {
            sendResponse(exchange, "Внутренняя ошибка сервера. " + e.getMessage(), SERVER_ERROR);
        }
    }

    // В этих методах запускается переопределяемый метод getEndpoint, который запускает необходимый метод для обработки запроса
    protected void processDelete(HttpExchange exchange, String path) throws IOException, JsonErrorConverter {
        runProcess(path, "DELETE", exchange);
    }

    protected void processPost(HttpExchange exchange, String path) throws IOException, JsonErrorConverter {
        runProcess(path, "POST", exchange);
    }

    protected void processGet(HttpExchange exchange, String path) throws IOException, JsonErrorConverter {
        runProcess(path, "GET", exchange);
    }

    // Общие методы для переопределения в Hanlder классах, метод getList и runProcess есть во всех класса, сделал абстрактным...
    abstract void runProcess(String path, String method, HttpExchange exchange) throws IOException, JsonErrorConverter;

    abstract void handleGetList(HttpExchange exchange) throws IOException;

    protected void handleGetById(HttpExchange exchange) throws IOException {
    }

    protected void handleCreateOrUpdate(HttpExchange exchange) throws IOException, JsonErrorConverter {
    }

    protected void handleDeleteById(HttpExchange exchange) throws IOException {
    }

    protected void sendResponse(HttpExchange h, String response, Integer code) throws IOException {
        byte[] resp = response.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(code, resp.length);
        h.getResponseBody().write(resp);
        h.getResponseBody().flush();
        h.close();
    }

    public boolean isNumber(String str) {
        try {
            Integer.parseInt(str);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

}


