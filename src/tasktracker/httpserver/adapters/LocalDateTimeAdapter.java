package tasktracker.httpserver.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
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
