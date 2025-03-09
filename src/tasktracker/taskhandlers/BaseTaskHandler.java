package tasktracker.taskhandlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import tasktracker.manager.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public abstract class BaseTaskHandler implements HttpHandler {

    protected TaskManager manager;
    protected Gson jsonMapper;

    public BaseTaskHandler(TaskManager manager, Gson jsonMapper) {
        this.manager = manager;
        jsonAdapter(jsonMapper);
    }

    private void jsonAdapter(Gson jsonMapper) {
        this.jsonMapper = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }

    protected void sendResponse(HttpExchange h, String response, Integer code) throws IOException {
        byte[] resp = response.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(code, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

}

class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy|HH:mm");

    @Override
    public void write(JsonWriter jsonWriter, LocalDateTime dateTime) throws IOException {
        jsonWriter.value(dateTime.format(formatter));
    }

    @Override
    public LocalDateTime read(JsonReader jsonReader) throws IOException {
        return LocalDateTime.parse(jsonReader.nextString(), formatter);
    }
}
