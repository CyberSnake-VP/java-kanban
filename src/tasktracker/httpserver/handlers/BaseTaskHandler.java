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
    protected final int OK = 200;
    protected final int CREATED = 201;
    protected final int NOTE_FOUND = 404;
    protected final int NOT_ACCEPTABLE = 406;
    protected final int SERVER_ERROR = 500;

    public BaseTaskHandler(TaskManager manager, Gson jsonMapper) {
        this.manager = manager;
        this.jsonMapper = jsonMapper;
    }

    protected void sendResponse(HttpExchange h, String response, Integer code) throws IOException {
        byte[] resp = response.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(code, resp.length);
        h.getResponseBody().write(resp);
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


