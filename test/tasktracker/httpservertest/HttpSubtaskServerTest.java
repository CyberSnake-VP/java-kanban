package tasktracker.httpservertest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasktracker.tasks.Epic;
import tasktracker.tasks.Subtask;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpSubtaskServerTest extends HttpTaskServerTest {
    private Epic epic;

    public HttpSubtaskServerTest() throws IOException {
    }

    @BeforeEach
    public void init() {
        epic = new Epic("epic", "description");
    }

    @Test
    public void createSubtask() throws IOException, InterruptedException {
        String epicJson = gson.toJson(epic);
        HttpRequest createEpic = HttpRequest.newBuilder()
                .uri(urlEpic)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();
        HttpResponse<String> createEpicResponse = client.send(createEpic, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, createEpicResponse.statusCode());

        Epic epicWithId = manager.getEpic(1);

        Subtask subtask = new Subtask("subtask", "description", epicWithId, LocalDateTime.now(), Duration.ofMinutes(0));
        String subtaskJson = gson.toJson(subtask);

        HttpRequest createSubtask = HttpRequest.newBuilder()
                .uri(urlSubtask)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();
        HttpResponse<String> createSubtaskResponse = client.send(createSubtask, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, createSubtaskResponse.statusCode());

        List<Subtask> subtaskList = manager.getSubtaskList();

        assertNotNull(subtaskList, "Подзадачи не создаются. ");
        assertEquals(1, subtaskList.size(), "Количество подзадач неправильное. ");
        assertEquals(subtaskList.getFirst().getName(), subtask.getName(), "Имена задач не совпадают. ");

    }

    @Test
    public void getSubtaskById() throws IOException, InterruptedException {
        String epicJson = gson.toJson(epic);
        HttpRequest createEpic = HttpRequest.newBuilder()
                .uri(urlEpic)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();
        HttpResponse<String> createEpicResponse = client.send(createEpic, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, createEpicResponse.statusCode());

        Epic epicWithId = manager.getEpic(1);

        Subtask subtask = new Subtask("subtask", "description", epicWithId, LocalDateTime.now(), Duration.ofMinutes(0));
        String subtaskJson = gson.toJson(subtask);
        // Создаем подзадачу
        HttpRequest createSubtask = HttpRequest.newBuilder()
                .uri(urlSubtask)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();
        HttpResponse<String> createSubtaskResponse = client.send(createSubtask, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, createSubtaskResponse.statusCode());

        // Получаем подзадачу по ID
        HttpRequest getSubtask = HttpRequest.newBuilder()
                .uri(urlSubtaskById)
                .GET()
                .build();
        HttpResponse<String> getSubtaskResponse = client.send(getSubtask, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, getSubtaskResponse.statusCode());

        Subtask subtaskWithId = manager.getSubtask(2);

        assertEquals(subtask.getName(), subtaskWithId.getName(), "Имена подзадач не совпадают. ");
    }

    @Test
    public void deleteSubtask() throws IOException, InterruptedException {
        String epicJson = gson.toJson(epic);
        HttpRequest createEpic = HttpRequest.newBuilder()
                .uri(urlEpic)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();
        HttpResponse<String> createEpicResponse = client.send(createEpic, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, createEpicResponse.statusCode());

        Epic epicWithId = manager.getEpic(1);

        Subtask subtask = new Subtask("subtask", "description", epicWithId, LocalDateTime.now(), Duration.ofMinutes(0));
        String subtaskJson = gson.toJson(subtask);

        // Создаем подзадачу
        HttpRequest createSubtask = HttpRequest.newBuilder()
                .uri(urlSubtask)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();
        HttpResponse<String> createSubtaskResponse = client.send(createSubtask, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, createSubtaskResponse.statusCode());

        // Удаляем подзадачу
        HttpRequest deleteTask = HttpRequest.newBuilder()
                .uri(urlSubtaskById)
                .DELETE()
                .build();
        HttpResponse<String> deleteResponse = client.send(deleteTask, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, createEpicResponse.statusCode());


    }

    @Test
    public void getSubtaskInEpic() throws IOException, InterruptedException {
        String epicJson = gson.toJson(epic);
        HttpRequest createEpic = HttpRequest.newBuilder()
                .uri(urlEpic)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();
        HttpResponse<String> createEpicResponse = client.send(createEpic, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, createEpicResponse.statusCode());

        Epic epicWithId = manager.getEpic(1);

        Subtask subtask = new Subtask("subtask", "description", epicWithId, LocalDateTime.now(), Duration.ofMinutes(0));
        String subtaskJson = gson.toJson(subtask);

        // Создаем подзадачу
        HttpRequest createSubtask = HttpRequest.newBuilder()
                .uri(urlSubtask)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();
        HttpResponse<String> createSubtaskResponse = client.send(createSubtask, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, createSubtaskResponse.statusCode());

        // Получаем подзадачу из эпика
        HttpRequest getSubtask = HttpRequest.newBuilder()
                .uri(urlEpicSubtask)
                .GET()
                .build();
        HttpResponse<String> getSubtaskResponse = client.send(getSubtask, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, getSubtaskResponse.statusCode());
        String body = getSubtaskResponse.body().replace("[", "").replace("]","");
        Subtask subtaskInManager = manager.getSubtask(2);
        Subtask subtaskInEpic = gson.fromJson(body, Subtask.class);

        assertEquals(subtaskInManager, subtaskInEpic, "Разные задачи");
    }
}






