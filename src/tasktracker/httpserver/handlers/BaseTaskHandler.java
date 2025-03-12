package tasktracker.httpserver.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import tasktracker.manager.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;


public abstract class BaseTaskHandler implements HttpHandler {

    protected TaskManager manager;
    protected Gson jsonMapper;
    protected static final int OK = 200;
    protected static final int CREATED = 201;
    protected static final int NOTE_FOUND = 404;
    protected static final int NOT_ACCEPTABLE = 406;
    protected static final int SERVER_ERROR = 500;

    public BaseTaskHandler(TaskManager manager, Gson jsonMapper) {
        this.manager = manager;
        this.jsonMapper = jsonMapper;
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


