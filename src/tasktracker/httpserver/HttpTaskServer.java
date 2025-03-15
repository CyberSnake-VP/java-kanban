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
    private static final int PORT = 8080;

    public HttpTaskServer(TaskManager manager) throws IOException {
        this.manager = manager;
    }

    public static void main(String[] args) throws IOException {
        System.out.println("Сервер запущен с порта: " + PORT);
        new HttpTaskServer(Managers.getDefault()).start();
    }

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/tasks", new TaskHandler(manager));
        server.createContext("/epics", new EpicHandler(manager));
        server.createContext("/subtasks", new SubtaskHandler(manager));
        server.createContext("/history", new HistoryHandler(manager));
        server.createContext("/prioritized", new PrioritizedHandler(manager));
        server.start();
    }

    public void stop() {
        server.stop(1);
    }
}

