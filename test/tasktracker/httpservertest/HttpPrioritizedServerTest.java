package tasktracker.httpservertest;

import org.junit.jupiter.api.Test;
import tasktracker.enumeration.Status;
import tasktracker.tasks.Epic;
import tasktracker.tasks.Subtask;
import tasktracker.tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpPrioritizedServerTest extends HttpTaskServerTest{
    public HttpPrioritizedServerTest() throws IOException {
    }

    @Test
    public void getPrioritizedList() throws IOException, InterruptedException {
        // создаём задачу
        Task task1 = new Task("task", "Testing task 2",
                Status.NEW, LocalDateTime.of(2000, 1, 1, 10, 0), Duration.ofMinutes(10));

        String task1Json = gson.toJson(task1);
        HttpRequest createTask1 = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(task1Json))
                .build();

        HttpResponse<String> responseCreateTask1 = client.send(createTask1, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, responseCreateTask1.statusCode());

        // создаём задачу
        Task task2 = new Task("task", "Testing task 2",
                Status.NEW, LocalDateTime.of(2000, 1, 1, 10,10), Duration.ofMinutes(10));

        String task2Json = gson.toJson(task2);
        HttpRequest createTask2= HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(task2Json))
                .build();

        HttpResponse<String> responseCreateTask2 = client.send(createTask2, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, responseCreateTask2.statusCode());

        Epic epic = new Epic("test", "description");
        String epicJson = gson.toJson(epic);
        HttpRequest createEpic = HttpRequest.newBuilder()
                .uri(urlEpic)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();
        HttpResponse<String> createEpicResponse = client.send(createEpic, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, createEpicResponse.statusCode());

        Epic epicWithId = manager.getEpic(3);

        Subtask subtask1 = new Subtask("subtask", "description", epicWithId, LocalDateTime.of(2000, 1, 1, 10,20), Duration.ofMinutes(10));
        String subtaskJson = gson.toJson(subtask1);

        HttpRequest createSubtask = HttpRequest.newBuilder()
                .uri(urlSubtask)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();
        HttpResponse<String> createSubtaskResponse1 = client.send(createSubtask, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, createSubtaskResponse1.statusCode());

        Subtask subtask2= new Subtask("subtask", "description", epicWithId, LocalDateTime.of(2000, 1, 1, 10,30), Duration.ofMinutes(10));
        String subtaskJson2 = gson.toJson(subtask2);

        HttpRequest createSubtask2 = HttpRequest.newBuilder()
                .uri(urlSubtask)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson2))
                .build();
        HttpResponse<String> createSubtaskResponse2 = client.send(createSubtask2, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, createSubtaskResponse2.statusCode());

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/prioritized"))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Неверный код ответа. ");

        List<Task> prioritizeTask = manager.getPrioritizedTasks();

        assertNotNull(prioritizeTask, "Пустой список приоритета. ");
        assertEquals(4, prioritizeTask.size());
        assertEquals(prioritizeTask.getFirst().getName(), task1.getName(), "Не верная сортировка в списке приоритета. ");
    }
}
