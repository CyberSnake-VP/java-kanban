package tasktracker.httpserver;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.sun.net.httpserver.HttpServer;
import tasktracker.httpserver.adapters.DurationAdapter;
import tasktracker.httpserver.adapters.LocalDateTimeAdapter;
import tasktracker.httpserver.handlers.*;
import tasktracker.manager.Managers;
import tasktracker.manager.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private static HttpServer server;
    private final TaskManager manager;

    public HttpTaskServer(TaskManager manager) throws IOException {
        this.manager = manager;
    }

    private static final int PORT = 8080;

    public static void main(String[] args) throws IOException {
        System.out.println("Сервер запущен с порта: " + PORT);
        new HttpTaskServer(Managers.getDefault()).start();
    }

    public static Gson getGson() {
        return new GsonBuilder()
                .serializeNulls()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
    }

    public void start() throws IOException {
        Gson jsonMapper = getGson();
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/tasks", new TaskHandler(manager, jsonMapper));
        server.createContext("/epics", new EpicHandler(manager, jsonMapper));
        server.createContext("/subtasks", new SubtaskHandler(manager, jsonMapper));
        server.createContext("/history", new HistoryHandler(manager, jsonMapper));
        server.createContext("/prioritized", new PrioritizedHandler(manager, jsonMapper));
        server.start();
    }

    public void stop() {
        server.stop(1);
    }
}

