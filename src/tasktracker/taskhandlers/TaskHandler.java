package tasktracker.taskhandlers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpExchange;
import tasktracker.enumeration.Endpoint;
import tasktracker.manager.TaskManager;
import tasktracker.tasks.Task;

import java.io.IOException;
import java.util.List;

public class TaskHandler extends BaseTaskHandler {

    public TaskHandler(TaskManager manager, Gson jsonMapper) {
        super(manager, jsonMapper);
    }

    @Override
    public void handle(HttpExchange exchange) {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());
        try {
            switch (endpoint) {
                case GET_TASKS: {
                    handleGetTaskList();
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка!!!!!");
        }

    }

    private void handleGetTaskList() throws IOException {
      List<Task> taskList =  manager.getTaskList();
      String jsonListTask = jsonMapper.toJson(taskList);

    }

    private Endpoint getEndpoint(String stringPath, String requestMethod) {
        String[] pathElements = stringPath.split("/");

        if (pathElements.length == 2 && requestMethod.equals("GET")) {
            return Endpoint.GET_TASKS;
        }
        return Endpoint.UNKNOWN;
    }


}

// Вспомогательный класс для определения типа коллекции для Json
class TaskListTypeToken extends TypeToken<Task> {}
