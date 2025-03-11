package tasktracker.httpserver;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.sun.net.httpserver.HttpServer;
import tasktracker.httpserver.handlers.SubtaskHandler;
import tasktracker.manager.Managers;
import tasktracker.manager.TaskManager;
import tasktracker.httpserver.handlers.EpicHandler;
import tasktracker.httpserver.handlers.TaskHandler;
import tasktracker.tasks.Epic;
import tasktracker.tasks.Subtask;
import tasktracker.tasks.Task;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class HttpTaskServer {
    private static final int PORT = 8080;


    public static void main(String[] args) throws IOException {
        TaskManager manager = Managers.getDefault();
        Gson jsonMapper = new GsonBuilder()
                .serializeNulls()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();

        manager.createTask(new Task("задача1", "действие", LocalDateTime.now(), Duration.ofMinutes(15)));
        manager.createTask(new Task("задача2", "действие", LocalDateTime.now(), Duration.ofMinutes(15)));
        manager.createTask(new Task("задача3", "действие", LocalDateTime.now(), Duration.ofMinutes(15)));
        manager.createTask(new Task("задача4", "действие", LocalDateTime.now(), Duration.ofMinutes(15)));

        Epic epic1 = manager.createEpic(new Epic("эпик1", "действие"));
        manager.createEpic(new Epic("эпик2", "действие"));

        manager.createSubtask(new Subtask("подзадача1", "действие", epic1, Duration.ofMinutes(10)));
        manager.createSubtask(new Subtask("подзадача2", "действие", epic1, Duration.ofMinutes(20)));


        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/tasks", new TaskHandler(manager, jsonMapper));
        server.createContext("/epics", new EpicHandler(manager, jsonMapper));
        server.createContext("/subtasks", new SubtaskHandler(manager, jsonMapper));


        System.out.println("Сервер запущен на порту: " + PORT);
        server.start();
    }
}


class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy|HH:mm");

    @Override
    public void write(JsonWriter jsonWriter, LocalDateTime dateTime) throws IOException {
        jsonWriter.value((Objects.nonNull(dateTime)) ? dateTime.format(formatter) : null);
    }

    @Override
    public LocalDateTime read(JsonReader jsonReader) throws IOException {
        return (jsonReader.peek() != null) ? LocalDateTime.parse(jsonReader.nextString(), formatter) : null;
    }
}

class DurationAdapter extends TypeAdapter<Duration> {
    @Override
    public void write(JsonWriter jsonWriter, Duration duration) throws IOException {
        jsonWriter.value(Objects.nonNull(duration) ? duration.toMinutes() : null);
    }

    @Override
    public Duration read(JsonReader jsonReader) throws IOException {
        return (jsonReader.peek() != null) ? Duration.ofMinutes(jsonReader.nextInt()) : null;
    }
}