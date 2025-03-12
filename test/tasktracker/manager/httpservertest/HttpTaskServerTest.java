package tasktracker.manager.httpservertest;

import com.google.gson.Gson;
import tasktracker.enumeration.Status;
import tasktracker.httpserver.HttpTaskServer;
import tasktracker.manager.Managers;
import tasktracker.manager.TaskManager;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import tasktracker.tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskServerTest {

    private final TaskManager manager = Managers.getDefault();
    private final HttpTaskServer server = new HttpTaskServer(manager);
    private final Gson gson = HttpTaskServer.getGson();
    private final HttpClient client = HttpClient.newHttpClient();
    private URI url;
    private URI urlById;

    public HttpTaskServerTest() throws IOException {
    }

    @BeforeEach
    public void setUp() throws IOException {
        url = URI.create("http://localhost:8080/tasks");
        urlById = URI.create("http://localhost:8080/tasks/1");
        manager.deleteTaskList();
        manager.deleteSubtaskList();
        manager.deleteEpicList();
        server.start();
    }

    @AfterEach
    public void shutDown() {
        server.stop();
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        // создаём задачу
        Task task = new Task("task", "Testing task 2",
                Status.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        // конвертируем её в JSON
        String taskJson = gson.toJson(task);

        // создаём HTTP-клиент и запрос
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Task> tasksFromManager = manager.getTaskList();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("task", tasksFromManager.getFirst().getName(), "Некорректное имя задачи");
    }

    @Test
    public void testGetTaskById() throws IOException, InterruptedException {
        // создаём задачу
        Task task = new Task("task", "Testing task 2",
                Status.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        // конвертируем её в JSON
        String taskJson = gson.toJson(task);

        // создаём HTTP-клиент и запрос
        HttpRequest createTask = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> responseCreate = client.send(createTask, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, responseCreate.statusCode());

        HttpRequest requestGetTaskById = HttpRequest.newBuilder()
                .GET()
                .uri(urlById)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> responseGet = client.send(requestGetTaskById, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseGet.statusCode());

        Task actualTask = manager.getTask(1);
        assertEquals("task", actualTask.getName(), "Задачи отличаются.");
    }

    @Test
    public void deleteTaskById() throws IOException, InterruptedException {
        // создаём задачу
        Task task = new Task("task", "Testing task 2",
                Status.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        // конвертируем её в JSON
        String taskJson = gson.toJson(task);

        // создаём HTTP-клиент и запрос
        HttpRequest createTask = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> responseCreate = client.send(createTask, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, responseCreate.statusCode());

        HttpRequest requestGetTaskById = HttpRequest.newBuilder()
                .DELETE()
                .uri(urlById)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> responseGet = client.send(requestGetTaskById, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseGet.statusCode());

        Task actualTask = manager.getTask(1);
        assertNull(actualTask, "Нет удаления задачи.");
    }

    @Test
    public void codeMustBe404() throws IOException, InterruptedException {
        HttpRequest requestGetTaskById = HttpRequest.newBuilder()
                .DELETE()
                .uri(urlById)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> responseGet = client.send(requestGetTaskById, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, responseGet.statusCode(), "Код не соответствует");
    }

    @Test
    public void codeMustBe406() throws IOException, InterruptedException {
        // создаём задачу
        Task task = new Task("task", "Testing task 2",
                Status.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        // конвертируем её в JSON
        String taskJson = gson.toJson(task);

        // создаём HTTP-клиент и запрос
        HttpRequest createTask = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> responseCreate = client.send(createTask, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, responseCreate.statusCode());

        // создаём HTTP-клиент и запрос
        HttpRequest createTaskIntersection = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        // Должно быть пересечение по времени
        HttpResponse<String> responseIntersection = client.send(createTaskIntersection, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, responseIntersection.statusCode());
    }

    @Test
    public void updateTask() throws IOException, InterruptedException {
        // создаём задачу
        Task task = new Task("task", "description",
                Status.NEW, LocalDateTime.of(2000,1,1, 10,0), Duration.ofMinutes(5));
        // конвертируем её в JSON
        String taskJson = gson.toJson(task);

        // создаём HTTP-клиент и запрос
        HttpRequest createTask = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> responseCreate = client.send(createTask, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, responseCreate.statusCode());

        Task taskForUpdate = manager.getTask(1);
        taskForUpdate.setStatus(Status.IN_PROGRESS);
        taskForUpdate.setName("taskUpdate");
        String taskUpdateJson = gson.toJson(taskForUpdate);

        HttpRequest updateTask = HttpRequest.newBuilder().
                 uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskUpdateJson))
                .build();

        HttpResponse<String> responseUpdate = client.send(updateTask, HttpResponse.BodyHandlers.ofString());
        Task actualTask = manager.getTask(1);

        assertEquals(201, responseUpdate.statusCode());
        assertNotEquals(task.getName(), actualTask.getName(), "Имена не должны совпадать .");
        assertNotEquals(task.getStatus(), actualTask.getStatus(), "Имена не должны совпадать .");
        assertEquals(task.getDescription(), actualTask.getDescription(), "Описание задача не должно быть изменено. ");
        assertEquals(task.getStartTime(), actualTask.getStartTime(), "Время начала не должно быть изменено. ");
        assertEquals(task.getDuration(), actualTask.getDuration(), "Продолжительность не должна быть изменена. ");
    }

}