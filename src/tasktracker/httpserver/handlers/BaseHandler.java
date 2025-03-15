package tasktracker.httpserver.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import tasktracker.httpserver.adapters.DurationAdapter;
import tasktracker.httpserver.adapters.LocalDateTimeAdapter;
import tasktracker.manager.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;


public class BaseHandler implements HttpHandler {

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
        String method = exchange.getRequestMethod();
        switch (method) {
            case "GET":
                processGet(exchange);
                break;
            case "POST":
                processPost(exchange);
                break;
            case "DELETE":
                processDelete(exchange);
                break;
            default: writeToUser(exchange, "Данный метод не предусмотрен.");
        }
    }

    private void processDelete(HttpExchange exchange) {
    }

    private void processPost(HttpExchange exchange) {
    }

    private void processGet(HttpExchange exchange) {
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

    public Gson getJsonMapper() {
        return jsonMapper;
    }

}


