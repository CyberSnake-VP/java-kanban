package tasktracker.manager.httpservertest;

import com.google.gson.Gson;
import tasktracker.httpserver.HttpTaskServer;
import tasktracker.manager.Managers;
import tasktracker.manager.TaskManager;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HttpTaskServerTest {

   // создаём экземпляр InMemoryTaskManager
   TaskManager manager = Managers.getDefault();
   // передаём его в качестве аргумента в конструктор HttpTaskServer
   HttpTaskServer taskServer = new HttpTaskServer(manager);
   Gson gson = HttpTaskServer.getGson();

   @BeforeEach
   public void setUp() throws IOException {
      manager.deleteTaskList();
      manager.deleteSubtaskList();
      manager.deleteEpicList();
      HttpTaskServer.start();
   }

   @AfterEach
   public void shutDown() {
      taskServer.stop();
   }

   @Test
   public void testAddTask() throws IOException, InterruptedException {
      // создаём задачу
      Task task = new Task("Test 2", "Testing task 2",
              TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now());
      // конвертируем её в JSON
      String taskJson = gson.toJson(task);

      // создаём HTTP-клиент и запрос
      HttpClient client = HttpClient.newHttpClient();
      URI url = URI.create("http://localhost:8080/tasks");
      HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

      // вызываем рест, отвечающий за создание задач
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
      // проверяем код ответа
      assertEquals(200, response.statusCode());

      // проверяем, что создалась одна задача с корректным именем
      List<Task> tasksFromManager = manager.getTasks();

      assertNotNull(tasksFromManager, "Задачи не возвращаются");
      assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
      assertEquals("Test 2", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
   }
}