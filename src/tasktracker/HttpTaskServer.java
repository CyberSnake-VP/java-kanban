package tasktracker;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import tasktracker.manager.Managers;
import tasktracker.manager.TaskManager;
import tasktracker.taskhandlers.TaskHandler;
import tasktracker.tasks.Task;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private static final int PORT = 8080;

    public static void main(String[] args) throws IOException {
        TaskManager manager = Managers.getDefault();
        Gson jsonMapper = new Gson();

        manager.createTask(new Task("задача1", "действие", LocalDateTime.now(), Duration.ofMinutes(15)));
        manager.createTask(new Task("задача2", "действие", LocalDateTime.now(), Duration.ofMinutes(15)));
        manager.createTask(new Task("задача3", "действие", LocalDateTime.now(), Duration.ofMinutes(15)));
        manager.createTask(new Task("задача4", "действие", LocalDateTime.now(), Duration.ofMinutes(15)));

        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/tasks", new TaskHandler(manager, jsonMapper));


        System.out.println("Сервер запущен на порту: " + PORT);
        server.start();

    }
}
