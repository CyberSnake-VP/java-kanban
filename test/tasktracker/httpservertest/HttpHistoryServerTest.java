package tasktracker.httpservertest;

import org.junit.jupiter.api.Test;
import tasktracker.enumeration.Status;
import tasktracker.tasks.Task;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpHistoryServerTest extends HttpTaskServerTest {
    public HttpHistoryServerTest() throws IOException {
    }

    @Test
    public void getHistory() throws IOException, InterruptedException {
        // создаём задачу
        Task task1 = new Task("task", "Testing task 2",
                Status.NEW, LocalDateTime.of(2000, 1, 1, 10, 0), Duration.ofMinutes(10));

        String taskJson = gson.toJson(task1);
        HttpRequest createTask = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> responseCreateTask = client.send(createTask, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, responseCreateTask.statusCode());

        HttpRequest getTask = HttpRequest.newBuilder()
                .uri(urlById)
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> responseGetTask = client.send(getTask, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseGetTask.statusCode());

        List<Task> historyList = manager.getHistory();
        assertNotNull(historyList, "Список истории пуст. ");
        assertEquals(1, historyList.size(), "Не верное количество задач в истории. ");

    }

}
