package tasktracker.httpserver;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.sun.net.httpserver.HttpServer;
import tasktracker.httpserver.handlers.*;
import tasktracker.manager.Managers;
import tasktracker.manager.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

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

/**
 * Адаптеры, для форматирования полей времени и продолжительности
 */
class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy|HH:mm");

    @Override
    public void write(JsonWriter jsonWriter, LocalDateTime dateTime) throws IOException {
        jsonWriter.value((Objects.nonNull(dateTime)) ? dateTime.format(formatter) : null);
    }

    // проверяем есть ли null элементы, если есть пропускаем его и выбрасываем null, иначе возвращаем нужный формат.
    @Override
    public LocalDateTime read(JsonReader jsonReader) throws IOException {
        if (jsonReader.peek() == JsonToken.NULL) {
            jsonReader.nextNull();
            return null;
        }
        return LocalDateTime.parse(jsonReader.nextString(), formatter);
    }
}

class DurationAdapter extends TypeAdapter<Duration> {
    @Override
    public void write(JsonWriter jsonWriter, Duration duration) throws IOException {
        jsonWriter.value(Objects.nonNull(duration) ? duration.toMinutes() : null);
    }

    @Override
    public Duration read(JsonReader jsonReader) throws IOException {
        if (jsonReader.peek() == JsonToken.NULL) {
            jsonReader.nextNull();
            return null;
        }
        return Duration.ofMinutes(jsonReader.nextInt());
    }
}
