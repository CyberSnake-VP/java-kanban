package tasktracker.httpservertest;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import tasktracker.tasks.Epic;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;


public class HttpEpicServerTest extends HttpTaskServerTest {

    public HttpEpicServerTest() throws IOException {
    }

    @Test
    public void createEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("test", "description");
        String epicJson = gson.toJson(epic);
        HttpRequest createEpic = HttpRequest.newBuilder()
                .uri(urlEpic)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(createEpic, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Неверный код при создании эпика. ");

        List<Epic> epics = manager.getEpicList();

        assertNotNull(epics, "Эпик не создается в менеджере .");
        assertEquals(1, epics.size(), "Неправильное количество эпиков ");
        assertEquals(epics.getFirst().getName(), epic.getName(), "Некорректное имя эпика. ");
    }

    @Test
    public void getEpicById() throws IOException, InterruptedException {
        Epic epic = new Epic("test", "description");
        String epicJson = gson.toJson(epic);
        HttpRequest createEpic = HttpRequest.newBuilder()
                .uri(urlEpic)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(createEpic, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Неверный код при создании эпика. ");

        // Получаем эпик из тела ответа
        HttpRequest getEpic = HttpRequest.newBuilder()
                .uri(urlEpicById)
                .GET()
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> getEpicResponse = client.send(getEpic, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, getEpicResponse.statusCode(), "Код получения эпика неверный. ");
        Epic epicInManager = manager.getEpic(1);
        Epic epicInBody = gson.fromJson(getEpicResponse.body(), Epic.class);

        assertEquals(epicInManager, epicInBody, "Эпики не совпадают. ");
    }

    @Test
    public void deleteEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("test", "description");
        String epicJson = gson.toJson(epic);
        HttpRequest createEpic = HttpRequest.newBuilder()
                .uri(urlEpic)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> createResponse = client.send(createEpic, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, createResponse.statusCode(), "Неверный код при создании эпика. ");

        HttpRequest deleteEpic = HttpRequest.newBuilder()
                .uri(urlEpicById)
                .DELETE()
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> deleteEpicResponse = client.send(deleteEpic, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, deleteEpicResponse.statusCode());

        Epic epicDeleted = manager.getEpic(1);

        assertNull(epicDeleted, "Эпик не был удален. ");
    }

}
